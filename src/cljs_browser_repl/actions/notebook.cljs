(ns cljs-browser-repl.actions.notebook
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-browser-repl.net.gist :as gist]
            [cljs-browser-repl.net.file :as file]
            [cljs-browser-repl.actions.repl :refer [repl-entry!]]
            [cljs.core.async :refer [<!]]
            [cljs.pprint :refer [pprint]]
            [cljs-browser-repl.state :as state]
            ))

(defn cmd-to-history! [cmd]
  (swap! state/history state/add-entry (state/to-repl cmd)))

(defn play-notebook! []
  (let [{:keys [cmds position]} @state/current-notebook]
    (when cmds
    (loop [pos (or position 0)]
      (let [{:keys [type value silent?] :as cmd} (nth cmds pos)
            new-pos (inc pos)]
        (swap! state/current-notebook assoc :position new-pos)
        (case type
          :input (repl-entry! value (not silent?))
          (cmd-to-history! cmd))
        ;; If the command is stop or we're at the end of the book. Go away
        (when (and (not= type :stop)
                   (< new-pos (count cmds)))
          (recur new-pos)))))))

(defn from-gist!
  ([id] (from-gist! id "index"))
  ([id file-name]
   (if (= (:id @state/current-notebook) id)
    (do ; If we are already on the notebook, just swap commands
      (swap! state/current-notebook assoc
             :cmds (gist/get-commands (:gist @state/current-notebook) file-name))
      (play-notebook!))
    (do ; If it's a new notebook, re-fetch, store and play
      (reset! state/history [])
      (go
        (let [gist (<! (gist/get! id))
              commands (gist/get-commands gist file-name)]
          (reset! state/current-notebook state/empty-notebook)
          (swap! state/current-notebook assoc :id id :gist gist :cmds commands)
          (play-notebook!)))))))

(defn from-filesystem!
  ([id] (from-filesystem! id "index"))
  ([id file-name]
   (reset! state/history [])
   (go
     (let [file (<! (file/get! id file-name))
           commands (file/get-commands file)]
       (reset! state/current-notebook state/empty-notebook)
       (swap! state/current-notebook
              assoc :id id :file-name file-name :file file :cmds commands)
       (play-notebook!)))))
