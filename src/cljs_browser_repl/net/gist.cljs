(ns cljs-browser-repl.net.gist
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [cljs.pprint :refer [pprint]]
            [cljs-browser-repl.state :refer [to-repl-error]]
            [cljs.reader :as edn]
            ))

(defn get!
  "Retrieves a gist by id. Returns a channel that will be filled with a clj-http
  response {:success true :body ...}"
  [id]
  (http/get (str "https://api.github.com/gists/" id)
            {:with-credentials? false}))

(defn invalid-gist [gist err]
  [(to-repl-error (str err "\n\n"
                      (with-out-str (pprint gist))))])

(defn get-commands
  "Given a gist it will return a list of commands for the repl to run.
  The gist must not be truncated, must have an index.json file."
  [gist file-name]
  (let [files (get-in gist [:body :files])
        file (or ((keyword (str file-name ".edn")) files)
                 ((keyword (str file-name ".json")) files))]
    (if (and file
             (not (:truncated file)))
      (condp = (:language file)
        "edn"
        (edn/read-string (:content file))
        "JSON"
        (try
          (map #(assoc % :type (keyword (:type %)))
               (js->clj (.parse js/JSON (:content file))
                        :keywordize-keys true))
          (catch :default e
            (invalid-gist gist e)))
      (invalid-gist gist "Invalid gist contents.")))))

#_(ns cljs.user
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [<!]]
            [cljs.pprint :refer [pprint]]))
#_(go (pprint (gist/get-commands (<! (gist/get! "609b68e1a720e9eebc0f")))))
