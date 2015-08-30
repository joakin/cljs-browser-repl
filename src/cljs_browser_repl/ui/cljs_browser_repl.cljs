(ns cljs-browser-repl.ui.cljs-browser-repl
  (:require [cljs-browser-repl.ui.top-bar :refer [top-bar]]
            [cljs-browser-repl.ui.history :refer [history]]
            [cljs-browser-repl.ui.repl-input :refer [repl-input]]
            [cljs-browser-repl.actions.repl :refer [repl-entry! new-input!]]
            [cljs-browser-repl.actions.notebook :refer [play-notebook!]]
            [cljs-browser-repl.compiler :refer [is-readable?]]
            [cljs-browser-repl.state :as state]
            [goog.events :as events]
            [cljs-browser-repl.router :as router]
            [clojure.string :as string]
            ))

(defn cljs-browser-repl-raw []
  [:div.cljs-browser-repl
   [top-bar]
   [history {:on-event
             (fn [type payload]
               (case type
                 :input (new-input! (:value payload))
                 :continue (play-notebook!)
                 :visit-file
                 (router/navigate!
                   (string/replace (router/get-token)
                                   #"/file/(.*)"
                                   (str "/file/" payload)))
                 ))}
    @state/history]
   [repl-input {:pre-label (str @state/current-ns)
                :valid-input? is-readable?
                :on-valid-input repl-entry!
                :on-change new-input!
                :value @state/input}]])

(def cljs-browser-repl
  (with-meta
    cljs-browser-repl-raw
    {:component-did-mount
     (fn [this]
       (set! (.-shortcutListener this)
             (fn [e]
               ; Brittle way of managing app key shortcuts :(
               (when-not (= (.. e -target -nodeName) "TEXTAREA")
                 (case (.-keyCode e)
                   ; n
                   78 (play-notebook!)
                   ; i
                   73 (.focus (.querySelector js/document ".repl-input-input"))
                   nil))))
       (events/listen js/window "keyup" (.-shortcutListener this)))
     :component-will-unmount
     (fn [this]
       (events/unlisten js/window "keyup" (.-shortcutListener this)))}
    ))
