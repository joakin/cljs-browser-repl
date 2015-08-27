(ns cljs-browser-repl.state
  (:require [reagent.core :refer [atom]]))

;; History

(defn now [] (.now js/Date))
(defn add-entry [h e] (conj h e))
(defn add-entries [h es] (apply conj h es))

(defn to-repl [o] (assoc o :date (now)))

(defn to-repl-input  [source] (to-repl {:type :input    :value source}))
(defn to-repl-error  [err]    (to-repl {:type :error    :value err}))
(defn to-repl-result [resp]   (to-repl {:type :response :value resp}))

(defn to-repl-entry  [t source] (to-repl {:type t :value source}))

(def initial-history-messages
  (mapv #(apply to-repl-entry %)
       [[:markdown "
  ### Hi!

  # Welcome to the web clojurescript repl.

  Enter any forms in the input at the bottom. Hit enter to evaluate.

  Here are some examples of things to try:"]
        [:input "(doc inc)"]
        [:input "(inc 5)"]
        [:input "(- 5 3)"]
        [:input "(defn square [x] (* x x))"]
        [:input "(square 6)"]
        [:markdown "Have fun!

![](http://media2.giphy.com/media/HVr4gFHYIqeti/giphy.gif)"]
        ]))

(defonce history (atom []))

;; Compiler

(defonce current-ns (atom 'cljs.user))

;; UI

(defonce input (atom ""))

;; Notebook

(def empty-notebook
  {:id nil
   :gist nil
   :cmds nil
   :position 0})

(defonce current-notebook (atom empty-notebook))

(defn current-command [notebook]
  (let [{:keys [cmds position]} notebook]
    (nth cmds position)))
