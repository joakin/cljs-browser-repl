(ns cljs-browser-repl.ui.repl-input
  (:require [reagent.core :as reagent]
            [clojure.string :as string]))

(defn resize [node]
  (set! (.. node -style -height) "auto")
  (set! (.. node -style -height) (str (.-scrollHeight node) "px")))

(defn enter?
  "Is an event the Enter key?"
  [e] (= (.-key e ) "Enter"))

(defn escape?
  "Is an event the Esc key?"
  [e] (= (.-key e ) "Escape"))

(defn get-val [e] (.. e -target -value))

(defn enter-pressed!
  "When shift+enter adds a new line. When only enter if the input is valid it
  runs the callback function and clears value and triggers the resize. If the
  input is not valid i'll do as if it was a shift+enter"
  [e valid? send-input]
  (let [shift? (.-shiftKey e)]
    (when (and (not shift?) valid?)
      (send-input (string/trim (get-val e)))
      (.preventDefault e)
      )))

(defn- repl-input-raw
  [{:keys [pre-label on-change on-valid-input valid-input? value]}]
  [:div.repl-input
   [:span.repl-input-pre pre-label]
   [:textarea.repl-input-input
    {:on-key-down (fn [e]
                    (cond
                      (enter? e)
                      (enter-pressed! e (valid-input? (get-val e)) on-valid-input)
                      (escape? e)
                      (.. e -target blur)
                      ))
     :on-change #(on-change (get-val %))
     :placeholder "Type clojurescript code here"
     :rows 1
     :value value
     }]])

(def repl-input
  (with-meta
    repl-input-raw
    {:component-did-update
     (fn [this old-argv]
       (let [input (.querySelector (reagent/dom-node this) ".repl-input-input")]
         (resize input)))}))
