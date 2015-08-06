(ns cljs-browser-repl.actions.repl
  (:require [cljs-browser-repl.state :as state]))

(defn repl-input! [code]
  (swap! state/history state/add-entry  (state/to-repl-entry code)))
