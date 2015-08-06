(ns cljs-browser-repl.ui.history
  (:require [cljs-browser-repl.state :as state]
            [clojure.string :as string]))

(defn- history-raw []
  [:pre.history (apply str (string/join "\n" @state/history))])

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
