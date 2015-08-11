(ns cljs-browser-repl.ui.history
  (:require [clojure.string :as string]
            [cljs-browser-repl.ui.history-entry :refer [history-entry]]))

(defn- history-raw [hs]
  [:div.history
   (for [entry hs]
     ^{:key (str "hist-" (:date entry))}
     [history-entry entry])])

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
