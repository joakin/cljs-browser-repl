(ns cljs-browser-repl.compiler
  (:require-macros [cljs.env.macros :refer [with-compiler-env]])
  (:require [replumb.core :as replumb]
            [replumb.repl :refer [current-ns]]
            [cljs.tools.reader :as r])
  (:import goog.net.XhrIo)
  )

(defn fetch-file!
  "Very simple implementation of XMLHttpRequests that given a file path
  calls src-cb with the string fetched of nil in case of error.
  See doc at https://developers.google.com/closure/library/docs/xhrio"
  [file-url src-cb]
  (try
    (.send XhrIo file-url
           (fn [e]
             (if (.isSuccess (.-target e))
               (src-cb (.. e -target getResponseText))
               (src-cb nil))))
    (catch :default e
      (src-cb nil))))

(def repl-opts
  (merge (replumb/options :browser
                          ["/src/clj" "/src/cljs" "/js/compiled/out"]
                          fetch-file!)
         {:warning-as-error true
          :verbose true}))

(defn cljs-read-eval-print!
  [line cb]
  (try
    (replumb/read-eval-call repl-opts cb line)
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

