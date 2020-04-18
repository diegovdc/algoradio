(ns algoradio.core
  (:require
   [algoradio.state :refer [app-state]]
   [reagent.core :as reagent]
   [algoradio.freesound :as freesound]))

(defn hello-world []
  [:div
   [:h1 (:text @app-state)]
   [:h3 "Edit this and watch it change!"]])

(defn start []
  (reagent/render-component [hello-world]
                            (. js/document (getElementById "app"))))

(defn say-hello [name] (str "Hello " name))

(defn set-text! [text] (swap! app-state assoc :text text) nil)

(set! (.. js/window -sayHello) say-hello)
(set! (.. js/window -resetText) set-text!)

(freesound/get-audios! app-state "river")

(-> @app-state :freesounds)

(defn ^:export init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (start))

(defn stop []
  ;; stop is called before any code is reloaded
  ;; this is controlled by :before-load in the config
  (js/console.log "stop"))
