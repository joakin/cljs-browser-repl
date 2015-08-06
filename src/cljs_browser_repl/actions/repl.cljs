(ns cljs-browser-repl.actions.repl
  (:require [cljs-browser-repl.state :as state]
            [cljs.js :as cljs]
            ))

(defonce repl-compiler-state (cljs/empty-state))

(defn- eval-code [compiler-state code cb]
  (cljs/eval-str compiler-state code nil
          {:eval cljs/js-eval
           :source-map true}
          cb))

(defn repl-input! [code]
  ; Add just typed command to history
  (swap! state/history state/add-entry (state/to-repl-entry code))
  (eval-code
    repl-compiler-state code
    (fn [{:keys [ns value error] :as ret}]
      ; Add result to history
      (swap! state/history state/add-entry
             (if error
               (state/to-repl-error error)
               (state/to-repl-result value))))
    ))
