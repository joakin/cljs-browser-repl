(ns cljs-browser-repl.ui.repl-input
  (:require [clojure.string :as string]))

(defn change [e]
  (let [input (.-target e)]
    (set! (.. input -style -height) "auto")
    (set! (.. input -style -height) (str (.-scrollHeight input) "px"))))

(defn enter?
  "Is an event the Enter key?"
  [f] (fn [e] (when (= (.-key e ) "Enter") (f e))))

(defn set-val! [e v] (set! (.. e -target -value) v))
(defn get-val [e] (.. e -target -value))

(defn enter-pressed!
  "When shift+enter adds a new line. When only enter if the input is valid it
  runs the callback function and clears value and triggers the resize. If the
  input is not valid i'll do as if it was a shift+enter"
  [e valid? send-input]
  (let [shift? (.-shiftKey e)]
    (when (and (not shift?) valid?)
      (send-input (string/trim (get-val e)))
      (set-val! e "")
      (.preventDefault e)
      (change e))))

(defn repl-input [{:keys [pre-label on-input valid-input?]}]
  [:div.repl-input
   [:span.repl-input-pre pre-label]
   [:textarea.repl-input-input
    {:on-key-down #(if (enter? %)
                     (enter-pressed! % (valid-input? (get-val %)) on-input))
     :on-change change
     :on-input change
     :placeholder "Type clojurescript code here"
     :rows 1
     }]])
