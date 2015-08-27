(ns cljs-browser-repl.ui.top-bar)

(defn top-bar []
  [:div.top-bar
   [:span.bar-title "cljs"]
   [:a.bar-link
    {:href "https://github.com/joakin/cljs-browser-repl"
     :title "Github repository"}
    "g"]])
