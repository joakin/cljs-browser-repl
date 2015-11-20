(ns cljs-browser-repl.actions.repl
  (:require [cljs-browser-repl.state :as state]
            [cljs-browser-repl.compiler :refer [cljs-read-eval-print!]]
            [replumb.core :refer [error->str]]
            [replumb.repl :refer [current-ns]]
            [clojure.string :refer [blank?]]
            ))

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
     (cljs-read-eval-print! code
       (fn [{:keys [value error] :as ret}]
         ; Add result to history
         (when history?
           (swap! state/history state/add-entry
                  (if error
                    (state/to-repl-error (error->str error))
                    (do
                      (reset! state/current-ns (current-ns))
                      (state/to-repl-result value))))))))))

(defn insert-repl-intro! []
  (reset! state/history state/initial-history-messages))
