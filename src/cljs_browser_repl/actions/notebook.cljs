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
   :cmds nil
   :position 0})

(defonce current-notebook (atom empty-notebook))

(defn cmd-to-history! [cmd]
  (swap! state/history state/add-entry (state/to-repl cmd)))

(defn play-notebook! []
  (let [{:keys [cmds position]} @current-notebook]
    (when cmds
    (loop [pos (or position 0)]
      (let [{:keys [type value silent?] :as cmd} (nth cmds pos)
            new-pos (inc pos)]
        (swap! current-notebook assoc :position new-pos)
        (case type
          :input (repl-entry! value (not silent?))
          :stop nil
          (cmd-to-history! cmd))
        ;; If the command is stop or we're at the end of the book. Go away
        (when (and (not= type :stop)
                   (< new-pos (count cmds)))
          (recur new-pos)))))))

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
          (swap! current-notebook assoc :id id :gist gist :cmds commands)
          (play-notebook!)))))))
