(ns ^:figwheel-always cljs-browser-repl.core
    (:require [reagent.core :as reagent]
              [cljs-browser-repl.actions.repl :refer [repl-entry!]]
              [cljs-browser-repl.ui.cljs-browser-repl :refer [cljs-browser-repl]]
              [cljs-browser-repl.router :as router]))

(enable-console-print!)

(defn render! []
  (reagent/render-component [cljs-browser-repl]
                            (. js/document (getElementById "app"))))

(defonce initialize-repl-ns
  (do
    (.initializeTouchEvents js/React true)
    ; Init compiler env
    (repl-entry! "(ns cljs.user)" false)
    ; First render
    (render!)
    ; Init router
    (router/init)
    ))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  (render!)
)

