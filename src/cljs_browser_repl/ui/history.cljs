(ns cljs-browser-repl.ui.history
  (:require [reagent.core :as reagent]
            [clojure.string :as string]
            [cljs-browser-repl.ui.history-entry :refer [history-entry]]))

(defn- history-raw [{:keys [on-event]} hs]
  [:div.history
   (for [entry hs]
     ^{:key (str "hist-" (:type entry) "-" (:date entry) "-" (str (:value entry)))}
     [history-entry {:emit on-event} entry])])

(def history
  (with-meta
    history-raw
    {:component-will-update
     (fn [this new-argv]
       (let [node (reagent/dom-node this)
             should-scroll? (= (+ (.-scrollTop node) (.-offsetHeight node)
                               (.-scrollHeight node)))]
         (set! (.-shouldScrollBottom this) should-scroll?)))
     :component-did-update
     (fn [this old-argv]
       (when (.-shouldScrollBottom this)
         (let [node (reagent/dom-node this)]
           (set! (.-scrollTop node) (.-scrollHeight node)))))
     }))
