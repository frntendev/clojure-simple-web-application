(ns ticket4sale.core
  (:require [baking-soda.core :as b]
            [reagent.core :as r]
            [goog.events :as events]
            [ticket4sale.datepicker :refer (datepicker)]
            [ticket4sale.navbar :refer (navbar)]
            [ticket4sale.ticketlist :refer (ticket-list)]
            [ticket4sale.date :refer (today-date)]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [ticket4sale.ajax :as ajax]
            [ajax.core :refer [GET POST]]
            [secretary.core :as secretary :include-macros true])
  (:import goog.History))

(defonce session (r/atom {:page :home}))
(def state (r/atom {:show-date today-date}))

(defn handler [response]
  (swap! state assoc :inventory (-> response :inventory vec)))

(defn fetch-shows! [show-date]
  (GET (str "/api/shows/" today-date "/" show-date)
    {:handler handler}))

(defn render-inventory []
  (let [inventory (:inventory @state)]
    (if (= (count inventory) 0) [:div.message "Nothing found"]
        (for [item inventory]
          ^{:key (get item "genre")}
          [ticket-list item]))))

(defn submit-handler []
  (fetch-shows! (:show-date @state)))

(defn input-change-handler [evt]
  (swap! state assoc-in [:show-date] (-> evt .-target .-value)))

(defn home-page []
  [:div.container.home-page
   [datepicker (:show-date @state) input-change-handler submit-handler]
   (render-inventory)])

(def pages
  {:home #'home-page})

(defn page []
  [(pages (:page @session))])

(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (swap! session assoc :page :home))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     HistoryEventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app


(defn mount-components []
  (r/render [#'navbar] (.getElementById js/document "navbar"))
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (ajax/load-interceptors!)
  (fetch-shows! today-date)
  (hook-browser-navigation!)
  (mount-components))