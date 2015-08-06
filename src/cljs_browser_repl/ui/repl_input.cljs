(ns cljs-browser-repl.ui.repl-input)

(defn if-enter
  "Returns a function that will call f if the event passed in was a 'Enter' key
  event."
  [f]
  (fn [e]
    (let [shift? (.-shiftKey e)
          enter? (= (.-key e ) "Enter")]
      (when enter?
        (if shift?
          (set! (.. e -target -value) (str (.. e -target -value) "\n"))
          (do
            (f (.. e -target -value))
            (set! (.. e -target -value) "")))))))

(defn repl-input [{:keys [pre-label on-input]}]
  [:div.repl-input
   [:span.repl-input-pre pre-label]
   [:textarea.repl-input-input
    {:on-key-up (if-enter on-input)
     :placeholder "Type clojurescript code here"
     :rows 1}]])
