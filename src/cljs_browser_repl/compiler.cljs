(ns cljs-browser-repl.compiler
  (:require-macros [cljs.env.macros :refer [with-compiler-env]])
  (:require [replumb.core :as replumb]
            [replumb.repl :refer [current-ns]]
            [cljs.tools.reader :as r]
            ))

(defn cljs-read-eval-print!
  [line cb]
  (try
    (replumb/read-eval-call {} cb line)
    (catch js/Error err
      (println "Caught js/Error during read-eval-print: " err)
      (cb {:error err :ns (current-ns)})
      )))

(defn is-readable? [line]
  (try
    (r/read-string line)
    true
    (catch :default _
      false)))

