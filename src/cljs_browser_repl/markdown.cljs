(ns cljs-browser-repl.markdown)

(defonce cm (.-commonmark js/window))
(defonce Parser (.-Parser cm))
(defonce HtmlRenderer (.-HtmlRenderer cm))

(defonce reader (Parser.))
(defonce writer (HtmlRenderer.))

(defn render [md]
  (->> md
      (.parse reader)
      (.render writer)))
