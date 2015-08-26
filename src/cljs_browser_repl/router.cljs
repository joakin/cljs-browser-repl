(ns cljs-browser-repl.router
  (:require [secretary.core :as secretary :refer-macros [defroute]]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [cljs-browser-repl.actions.repl :as repl]
            [cljs-browser-repl.actions.notebook :as notebook]
            )
  (:import goog.History))

(secretary/set-config! :prefix "#")

(defonce history (History.))

(defn scroll-top [] (.-scrollY js/window))

(defn set-scroll-top! []
  (let [state (or (.-state js/history) #js {})]
    (aset state "scroll-top" (scroll-top))
    (.replaceState js/history state)))

(defn get-scroll-top []
  (when-let [state (.-state js/history)]
    (aget state "scroll-top")))

(defn handle-url-change! [e]
  #_(when-not (.-isNavigation e)
    (js/window.scrollTo 0 0))
  (secretary/dispatch! (.-token e)))

(defn init []
  (goog.events/listen
    history EventType/NAVIGATE #(handle-url-change! %))
  (.setEnabled history true))

(defn navigate! [url]
  (.setToken history url))

(defn replace! [url]
  (.replaceToken history url))

(defn get-token []
  (.getToken history))

;; Route definitions

; home
(defroute "/" []
  (repl/insert-repl-intro!))

; search
(defroute "/gist/:id" [id]
  (notebook/from-gist! id))

(defroute "/gist/:id/file/:file" [id file]
  (notebook/from-gist! id file))
