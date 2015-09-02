(ns cljs-browser-repl.ui.history-entry
  (:require [clojure.string :as string]
            [cljs-browser-repl.markdown :as md]))

(declare history-entry)

(defn- response-with-meta->entry [{:keys [value] :as entry}]
  (let [sub-type (:type (meta value))
        is-value-map? (= (type value) cljs.core/PersistentArrayMap)
        value-of-value? (not (nil? (:value value)))
        new-value (if (and is-value-map? value-of-value?)
                    (:value value) value)]
    (with-meta (assoc entry :type sub-type :value new-value) nil)))

(defn emit-input! [emit-fn entry]
  (let [sel (.toString (.getSelection js/window))
        value (if (string/blank? sel) (:value entry) sel)
        payload (response-with-meta->entry (assoc entry :value value))]
    (emit-fn :input payload)))

(defn history-input [{:keys [emit]} {:keys [value] :as entry}]
  [:div.history-input
   {:on-click #(emit-input! emit entry)}
   value])

(defn history-response [{:keys [emit]} {:keys [value] :as entry}]
  (let [sub-type (:type (meta value))]
    [:div.history-response
     {:class (if sub-type "" "history-response-cljs")
      :on-click #(emit-input! emit entry)}
     (if sub-type
       [history-entry nil (response-with-meta->entry entry)]
       (println-str value))]))

(defn history-response-error [{:keys [emit]} {:keys [value]}]
  [:div.history-response-error value])

(defn history-unknown [{:keys [emit]} entry]
  [:pre.history-unknown (println-str entry)])

(defn history-html [{:keys [emit]} {:keys [value]}]
  [:div.history-html
   {:dangerouslySetInnerHTML {:__html value}}])

(def internal-re
  (js/RegExp. (str "^"
                   (.-origin js/location)
                   (.-pathname js/location)
                   "/#/file/(.*)")))

(defn history-md [{:keys [emit]} {:keys [value]}]
  [:div.history-markdown
   {:on-click
    #(do
       (.preventDefault %)
       (let [target (.-target %)
             href (.-href target)
             internal? (re-seq internal-re href)]
         (println href)
         (println internal?)
         (if internal?
           (emit :visit-file (second (first internal?)))
           (.open js/window href))))
    :dangerouslySetInnerHTML {:__html (md/render value)}}])

(defn history-separator [] [:hr])

(defn history-stop [{:keys [emit]} {:keys [disabled] :as entry}]
  (if disabled
    [history-separator]
    [:div.history-stop
     {:on-click #(emit :continue)}
     [:button "Next"]]))

(defn history-entry [{:keys [emit] :as attrs} {:keys [type] :as entry}]
  [:div.history-entry
   [(case (:type entry)
      :input history-input
      :error history-response-error
      :response history-response
      :html history-html
      :markdown history-md
      :stop history-stop
      :separator history-separator
      history-unknown) attrs entry]])
