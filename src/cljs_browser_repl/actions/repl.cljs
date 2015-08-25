(ns cljs-browser-repl.actions.repl
  (:require [cljs-browser-repl.state :as state]
            [cljs-browser-repl.compiler :refer [read-eval-print empty-compiler-state]]
            [clojure.string :refer [blank?]]
            ))

(defonce repl-compiler-state (empty-compiler-state))

(defn new-input! [s]
  (reset! state/input s))

(defn repl-entry!
  ([code] (repl-entry! code true))
  ([code history?]
   (when-not (blank? code)
     ; Reset the current input
     (new-input! "")
     ; Add just typed command to history
     (when history?
       (swap! state/history state/add-entry (state/to-repl-input code)))
     (read-eval-print
       repl-compiler-state state/current-ns
       code true
       (fn [{:keys [ns value error] :as ret}]
         ; Add result to history
         (when history?
           (swap! state/history state/add-entry
                  (if error
                    (state/to-repl-error error)
                    (do
                      (reset! state/current-ns ns)
                      (state/to-repl-result value))))))))))

(defn insert-repl-intro! []
  (reset! state/history state/initial-history-messages))
