(ns cljs-browser-repl.ui.history-entry)

(declare history-entry)

(defn history-input [{:keys [value]}]
  [:div.history-input value])

(defn- response-with-meta->entry [{:keys [value] :as entry}]
  (let [sub-type (:type (meta value))
        is-value-map? (= (type value) cljs.core/PersistentArrayMap)
        value-of-value? (not (nil? (:value value)))
        new-value (if (and is-value-map? value-of-value?)
                    (:value value) value)]
    (with-meta (assoc entry :type sub-type :value new-value) nil)))

(defn history-response [{:keys [value] :as entry}]
  (let [sub-type (:type (meta value))]
    [:div.history-response
     {:class (if sub-type "" "history-response-cljs")}
     (if sub-type
       [history-entry nil (response-with-meta->entry entry)]
       (println-str value))]))

(defn history-response-error [{:keys [value]}]
  [:div.history-response-error (.. value -cause -message)])

(defn history-unknown [entry]
  [:pre.history-unknown (println-str entry)])

(defn history-html [{:keys [value]}]
  (let [html value]
    [:div.history-html
     {:dangerouslySetInnerHTML {:__html html}}]))


(defn history-entry [{:keys [on-click]} entry]
  [:div.history-entry
   {:on-click (if on-click #(on-click (response-with-meta->entry entry)) nil)}
   [(case (:type entry)
      :input history-input
      :response-error history-response-error
      :response history-response
      :html history-html
      history-unknown) entry]])
