(ns cljs-browser-repl.net.file
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<! chan]]
            [cljs.pprint :refer [pprint]]
            [cljs-browser-repl.state :refer [to-repl-error]]
            [cljs.reader :as edn]
            ))

(defn get!
  "Retrieves a notebook file by id and file name. Returns a channel that will
  be filled with a clj-http response {:success true :body ...}"
  [id file-name]
  (let [resp (chan)
        url #(str "notebooks/" id "/" file-name %)]
    (go
      ; Try to get the edn, or the json, or return first failed response
      (let [edn-res (<! (http/get (url ".edn") {:content-type "application/edn"}))]
        (if (:success edn-res)
          (>! resp edn-res)
          (let [json (<! (http/get (url ".json") {:content-type "application/json"}))]
            (if (:success json)
              (>! resp json)
              (>! resp edn-res))))))
    resp))

(defn invalid-file [file err]
  [(to-repl-error (str err "\n\n"
                       (with-out-str (pprint file))))])

(defn get-commands
  "Given a file response it will return a list of commands for the repl to run."
  [{:keys [success body] :as file}]
  (if success
    (if (= (type body) js/String)
      (edn/read-string body)
      body)
    (invalid-file file (js/Error. "Request failed"))))

#_(ns cljs.user
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [<!]]
            [cljs.pprint :refer [pprint]]))
#_(go (pprint (gist/get-commands (<! (gist/get! "609b68e1a720e9eebc0f")))))
