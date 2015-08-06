(ns cljs-browser-repl.actions.repl
  (:require [cljs-browser-repl.state :as state]
            [cljs-browser-repl.compiler :refer [eval-code empty-compiler-state]]
            [clojure.string :refer [blank?]]
            ))

(defonce repl-compiler-state (empty-compiler-state))

(defn repl-input!
  ([code] (repl-input! code true))
  ([code history?]
   (when-not (blank? code)
     ; Add just typed command to history
     (when history?
       (swap! state/history state/add-entry (state/to-repl-input code)))
     (eval-code
       @state/current-ns
       repl-compiler-state code
       (fn [{:keys [ns value error] :as ret}]
         ; Add result to history
         (when history?
           (swap! state/history state/add-entry
                  (if error
                    (state/to-repl-error error)
                    (do
                      (reset! state/current-ns ns)
                      (state/to-repl-result value))))))))))
