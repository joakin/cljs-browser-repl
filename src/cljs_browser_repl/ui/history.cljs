(ns cljs-browser-repl.ui.history
  (:require [clojure.string :as string]))

(defn history-input [{:keys [value]}]
  [:p.history-input value])
(defn history-response [{:keys [value]}]
  [:p.history-response value])
(defn history-response-error [{:keys [value]}]
  [:p.history-response-error (.. value -cause -message)])

(defn- history-raw [hs]
  [:div.history
   (for [entry hs]
     ^{:key (str "hist-" (:date entry))}
     (case (:type entry)
       :input [history-input entry]
       :response [history-response entry]
       :response-error [history-response-error entry]))])

(def history
  (with-meta
    history-raw
    {:component-will-update
     (fn [this new-argv]
       (let [node (.getDOMNode this)
             should-scroll? (= (+ (.-scrollTop node) (.-offsetHeight node)
                               (.-scrollHeight node)))]
         (set! (.-shouldScrollBottom this) should-scroll?)))
     :component-did-update
     (fn [this old-argv]
       (when (.-shouldScrollBottom this)
         (let [node (.getDOMNode this)]
           (set! (.-scrollTop node) (.-scrollHeight node)))))
     }))
