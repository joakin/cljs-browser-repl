(ns cljs-browser-repl.ui.repl-input)

(defn if-enter
  "Returns a function that will call f if the event passed in was a 'Enter' key
  event."
  [f]
  (fn [e]
    (when (= (.-key e ) "Enter")
      (f (.. e -target -value))
      (set! (.. e -target -value) ""))))

(defn repl-input [{:keys [pre-label on-input]}]
  [:div.repl-input
   [:span.repl-input-pre pre-label]
   [:input.repl-input-input
    {:type "text" :on-key-up (if-enter on-input)
     :placeholder "Type clojurescript code here"}]])
