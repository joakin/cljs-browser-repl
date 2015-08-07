(ns cljs-browser-repl.compiler
  (:require [cljs.js :as cljs]))

(defn empty-compiler-state [] (cljs/empty-state))

(defn- eval-code [ns compiler-state code cb]
  (cljs/eval-str
    compiler-state code nil
    {:ns ns
     :eval cljs/js-eval
     :source-map true
     :context :expr
     :def-emits-var true}
    cb))

