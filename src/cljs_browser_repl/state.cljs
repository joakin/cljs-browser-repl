(ns cljs-browser-repl.state
  (:require [reagent.core :refer [atom]]))

(def initial-history-message
  "(comment

  Hi! Welcome to the web clojurescript repl.

  Enter any forms in the input at the bottom. Hit enter to evaluate.

  Here are some examples of things to try:

    (doc inc)
    (inc 5)
    (- 5 3)
    (defn square [x] (* x x))
    (square 6)

  Have fun!

  )
")

(defonce history (atom [initial-history-message]))

(defn add-entry [h e] (conj h e))
(defn to-repl-entry [e] (str "> " e))
(defn to-repl-error [err]
  (let [cause (.-cause err)]
    (str "Error: " (.-message cause) "\n")))
(defn to-repl-result [e] e)
