(ns cljs-browser-repl.compiler
  (:require-macros [cljs.env.macros :refer [with-compiler-env]])
  (:require [cljs.js :as cljs]
            [cljs.tagged-literals :as tags]
            [cljs.tools.reader :as r]
            [cljs.analyzer :as ana]
            [cljs.repl :as repl]
            [cljs.stacktrace :as st]
            [cljs.source-map :as sm]
            ))

(defn empty-compiler-state [] (cljs/empty-state))

(defn repl-read-string [line]
  (r/read-string {:read-cond :allow :features #{:cljs}} line))

(defn is-readable? [line]
  (binding [r/*data-readers* tags/*cljs-data-readers*]
    (try
      (repl-read-string line)
      true
      (catch :default _
        false))))

(defn ns-form? [form]
  (and (seq? form) (= 'ns (first form))))

(def repl-specials '#{in-ns require require-macros doc})

(defn repl-special? [form]
  (and (seq? form) (repl-specials (first form))))

(def repl-special-doc-map
  '{in-ns          {:arglists ([name])
                    :doc      "Sets *cljs-ns* to the namespace named by the symbol, creating it if needed."}
    require        {:arglists ([& args])
                    :doc      "Loads libs, skipping any that are already loaded."}
    require-macros {:arglists ([& args])
                    :doc      "Similar to the require REPL special function but\n  only for macros."}
    doc            {:arglists ([name])
                    :doc      "Prints documentation for a var or special form given its name"}})

(defn- repl-special-doc [name-symbol]
  (assoc (repl-special-doc-map name-symbol)
    :name name-symbol
    :repl-special-function true))

(defn resolve
  "Given an analysis environment resolve a var. Analogous to
   clojure.core/resolve"
  [env sym]
  {:pre [(map? env) (symbol? sym)]}
  (try
    (ana/resolve-var env sym
      (ana/confirm-var-exists-throw))
    (catch :default _
      (ana/resolve-macro-var env sym))))

(defn completion-candidates-for-ns [compiler-state ns-sym allow-private?]
  (map (comp str key)
    (filter (if allow-private?
              identity
              #(not (:private (:meta (val %)))))
      (apply merge
        ((juxt :defs :macros)
          (get (:cljs.analyzer/namespaces compiler-state) ns-sym))))))

(defn is-completion? [buffer-match-suffix candidate]
  (re-find (js/RegExp. (str "^" buffer-match-suffix)) candidate))

(defn ^:export get-completions [compiler-state current-ns buffer]
  (let [namespace-candidates (map str
                               (keys (:cljs.analyzer/namespaces compiler-state)))
        top-form? (re-find #"^\s*\(\s*[^()\s]*$" buffer)
        typed-ns (second (re-find #"(\b[a-zA-Z-.]+)/[a-zA-Z-]+$" buffer))
        all-candidates (set (if typed-ns
                              (completion-candidates-for-ns compiler-state (symbol typed-ns) false)
                              (concat namespace-candidates
                                      (completion-candidates-for-ns compiler-state 'cljs.core false)
                                      (completion-candidates-for-ns compiler-state @current-ns true)
                                      (when top-form? (map str repl-specials)))))]
    (let [buffer-match-suffix (re-find #"[a-zA-Z-]*$" buffer)
          buffer-prefix (subs buffer 0 (- (count buffer) (count buffer-match-suffix)))]
      (clj->js (if (= "" buffer-match-suffix)
                 []
                 (map #(str buffer-prefix %)
                   (sort
                     (filter (partial is-completion? buffer-match-suffix)
                       all-candidates))))))))

(defn require [compiler-state macros-ns? sym reload]
  (cljs.js/require
    {:*compiler*     compiler-state
     :*data-readers* tags/*cljs-data-readers*
     :*eval-fn*      cljs/js-eval}
    sym
    reload
    {:macros-ns  macros-ns?
     :verbose    true
     :source-map true}
    (fn [res]
      #_(println "require result:" res))))

(defn require-destructure [compiler-state macros-ns? args]
  (let [[[_ sym] reload] args]
    (require compiler-state macros-ns? sym reload)))

(defn ^:export run-main [compiler-state main-ns args]
  (let [main-args (js->clj args)]
    (require compiler-state false (symbol main-ns) nil)
    (cljs/eval-str compiler-state
      (str "(var -main)")
      nil
      {:ns         (symbol main-ns)
       :eval       cljs/js-eval
       :source-map true
       :context    :expr}
      (fn [{:keys [ns value error] :as ret}]
        (apply value args)))
    nil))

(defn print-error [error]
  (let [cause (or (.-cause error) error)]
    (println (.-message cause))))

(defn read-eval-print
  [compiler-state current-ns source expression? cb]
  (binding [ana/*cljs-ns* @current-ns
            *ns* (create-ns @current-ns)
            r/*data-readers* tags/*cljs-data-readers*]
    (let [expression-form (and expression? (repl-read-string source))]
      (if (repl-special? expression-form)
        (let [env (assoc (ana/empty-env) :context :expr
                                         :ns {:name @current-ns})]
          (case (first expression-form)
            in-ns (reset! current-ns (second (second expression-form)))
            require (require-destructure compiler-state false (rest expression-form))
            require-macros (require-destructure compiler-state true (rest expression-form))
            doc (cb {:ns @current-ns
                     :value (with-out-str
                              (if (repl-specials (second expression-form))
                                (repl/print-doc (repl-special-doc (second expression-form)))
                                (repl/print-doc
                                  (let [sym (second expression-form)]
                                    (with-compiler-env compiler-state (resolve env sym))))))})))
        (try
          (cljs/eval-str
            compiler-state
            source
            (if expression? source "File")
            (merge
              {:ns         @current-ns
               :eval       cljs/js-eval
               :source-map false
               :verbose    (:verbose true)}
              (when expression?
                {:context       :expr
                 :def-emits-var true}))
            (fn [{:keys [ns value error] :as ret}]
              (when (and expression? (not error))
                (when-not
                  (or ('#{*1 *2 *3 *e} expression-form)
                      (ns-form? expression-form))
                  (set! *3 *2)
                  (set! *2 *1)
                  (set! *1 value))
                (reset! current-ns ns)
                (set! *e error))
              (when error
                (print-error error))
              (cb ret)))
          (catch :default e
            (print-error e)
            (cb {:error e :ns @current-ns})))))))
