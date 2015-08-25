(ns cljs-browser-repl.actions.notebook
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-browser-repl.net.gist :as gist]
            [cljs-browser-repl.actions.repl :refer [repl-entry!]]
            [cljs.core.async :refer [<!]]
            [cljs.pprint :refer [pprint]]
            [cljs-browser-repl.state :as state]
            ))

(defn cmd-to-history! [cmd]
  (swap! state/history state/add-entry (state/to-repl cmd)))

(defn from-gist! [id]
  (go
    (let [gist (<! (gist/get! id))
          commands (gist/get-commands gist)]
      (reset! state/history [])
      (doseq [{:keys [type value silent?] :as cmd} commands]
        (case type
          :input (repl-entry! value (not silent?))
          (cmd-to-history! cmd))
        ))))
