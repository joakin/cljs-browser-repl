(ns cljs-browser-repl.actions.notebook
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-browser-repl.net.gist :as gist]
            [cljs-browser-repl.actions.repl :refer [repl-entry!]]
            [cljs.core.async :refer [<!]]
            [cljs.pprint :refer [pprint]]
            [cljs-browser-repl.state :as state]
            ))

(def empty-notebook
  {:id nil
   :gist nil
   :cmds nil})

(defonce current-notebook (atom empty-notebook))

(defn cmd-to-history! [cmd]
  (swap! state/history state/add-entry (state/to-repl cmd)))

(defn play-notebook! []
  (let [commands (:cmds @current-notebook)]
    (doseq [{:keys [type value silent?] :as cmd} commands]
      (case type
        :input (repl-entry! value (not silent?))
        (cmd-to-history! cmd)))))

(defn from-gist!
  ([id] (from-gist! id "index"))
  ([id file-name]
   (if (= (:id @current-notebook) id)
    (do ; If we are already on the notebook, just swap commands
      (swap! current-notebook assoc
             :cmds (gist/get-commands (:gist @current-notebook) file-name))
      (play-notebook!))
    (do ; If it's a new notebook, re-fetch, store and play
      (reset! state/history [])
      (go
        (let [gist (<! (gist/get! id))
              commands (gist/get-commands gist file-name)]
          (reset! current-notebook empty-notebook)
          (play-notebook!)))))))
