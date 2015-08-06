(ns cljs-browser-repl.ui.repl-input
  (:require [clojure.string :as string]))

(defn if-enter
  "Returns a function that gets an event and will call f if the event passed in
  was a 'Enter' key event."
  [f] (fn [e] (when (= (.-key e ) "Enter") (f e))))

(defn enter-pressed!
  "When shift+enter adds a new line. When only enter it runs the callback
  function and clears value."
  [e cb]
  (let [shift? (.-shiftKey e)
        v (.. e -target -value)
        set-val! #(set! (.. e -target -value) %)]
    (if-not shift?
      (do (cb (string/trim v)) (set-val! "") (.preventDefault e)))))

(defn change [e]
  (let [input (.-target e)]
    (set! (.. input -style -height) "auto")
    (set! (.. input -style -height) (str (.-scrollHeight input) "px"))))

(defn repl-input [{:keys [pre-label on-input]}]
  [:div.repl-input
   [:span.repl-input-pre pre-label]
   [:textarea.repl-input-input
    {:on-key-down (if-enter #(enter-pressed! % on-input))
     :on-change change
     :on-input change
     :placeholder "Type clojurescript code here"
     :rows 1
     }]])
