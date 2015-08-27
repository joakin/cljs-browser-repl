(ns cljs-browser-repl.ui.history-entry
  (:require [clojure.string :as string]
            [cljs-browser-repl.markdown :as md]))

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
  [:div.history-response-error value])

(defn history-unknown [entry]
  [:pre.history-unknown (println-str entry)])

(defn history-html [{:keys [value]}]
  [:div.history-html
   {:dangerouslySetInnerHTML {:__html value}}])

(defn history-md [{:keys [value]}]
  [:div.history-markdown
   {:dangerouslySetInnerHTML {:__html (md/render value)}}])

(def clickable-entries #{:input :error :response})

(defn history-entry [{:keys [on-click]} {:keys [type] :as entry}]
  [:div.history-entry
   {:on-click #(if (clickable-entries type)
                 (on-click
                   (let [sel (.toString (.getSelection js/window))
                         value (if (string/blank? sel) (:value entry) sel)]
                     (response-with-meta->entry
                       (assoc entry :value value)))))}
   [(case (:type entry)
      :input history-input
      :error history-response-error
      :response history-response
      :html history-html
      :markdown history-md
      history-unknown) entry]])
