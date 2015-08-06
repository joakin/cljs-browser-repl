(ns cljs-browser-repl.state
  (:require [reagent.core :refer [atom]]))

;; History

(defn now [] (.now js/Date))
(defn add-entry [h e] (conj h e))

(defn to-repl-input  [source] {:date (now) :type :input          :value source})
(defn to-repl-error  [err]    {:date (now) :type :response-error :value err})
(defn to-repl-result [resp]   {:date (now) :type :response       :value resp})

(def initial-history-message
  (to-repl-input "(comment

  Hi! Welcome to the web clojurescript repl.

  Enter any forms in the input at the bottom. Hit enter to evaluate.

  Here are some examples of things to try:

    (doc inc)
    (inc 5)
    (- 5 3)
    (defn square [x] (* x x))
    (square 6)

  Have fun!

  )"))

(defonce history (atom [initial-history-message]))

;; Compiler

(defonce current-ns (atom 'cljs.user))

