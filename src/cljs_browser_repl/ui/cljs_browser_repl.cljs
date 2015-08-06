(ns cljs-browser-repl.ui.cljs-browser-repl
  (:require [cljs-browser-repl.ui.top-bar :refer [top-bar]]
            [cljs-browser-repl.ui.history :refer [history]]
            [cljs-browser-repl.ui.repl-input :refer [repl-input]]
            [cljs-browser-repl.actions.repl :refer [repl-input!]]
            ))

(defn cljs-browser-repl []
  [:div.cljs-browser-repl
   [top-bar]
   [history]
   [repl-input {:on-input repl-input!}]])
