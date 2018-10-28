(ns ticket4sale.routes.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]
            [clojure.data.json :as json]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response]]
            [clj-time.core :as t]
            [clj-time.coerce :as c]
            [clj-time.format :as f]))

(def all-records (json/read-str (slurp "src/clj/ticket4sale/routes/data.json")
                                :key-fn keyword))

(def custom-formatter (f/formatter "yyyy-MM-dd"))
(defn parsed-date [date] (f/parse custom-formatter date))

;Difference between two dates in days
(defn get-day-interval [from-date to-date]
  (t/in-days
   (t/interval
    from-date
    to-date)))

;Getting ticket availability status
(defn get-ticket-status [opening-date show-date query-date]
  (if (t/after?  (parsed-date query-date)  (t/plus (parsed-date opening-date) (t/days 95))) "In the past"
      (if
       (<=
        (get-day-interval (parsed-date query-date) (parsed-date show-date))  5) "Sold out"
       (if
        (<=
         (get-day-interval (parsed-date query-date) (parsed-date show-date))  25) "Open for sale" "Sale not started"))))

;Getting the tickets using given show-time date
(defn filtered-by-time [query-date show-date data]
  (filter #(or
            (t/equal? (parsed-date show-date)  (parsed-date (get % :opening-date)))
            (t/after? (parsed-date show-date)  (parsed-date (get % :opening-date))))
          (filter #(t/before? (parsed-date show-date)  (t/plus (parsed-date (get % :opening-date)) (t/days 100))) data)))

; Getting tickets details in terms of availability and count
(defn get-ticket-details [opening-date show-date query-date]
  (if (= (get-ticket-status opening-date show-date query-date) "Open for sale")
    (if (<= (get-day-interval  (parsed-date opening-date)  (parsed-date show-date)) 60)
      {:tickets_left 200
       :tickets_available 10}
      {:tickets_left 100
       :tickets_available 5})  {:tickets_left 0
                                :tickets_available 0}))

; Parsing data.json object and creating the new format object
(defn parse-object [query-date show-date data]
  (if (t/before? (parsed-date show-date) (parsed-date query-date)) {:error true :message "In the past" :inventory []}
      {:inventory (for [[k xs] (group-by :genre (filtered-by-time query-date show-date data))]
                    {"genre" k
                     "shows" (for [x xs]
                               (merge
                                {:title (get x :title)
                                 :status (get-ticket-status (get x :opening-date) show-date query-date)}
                                (get-ticket-details (get x :opening-date) show-date query-date)))})}))

; Swagger route definition
(defapi service-routes
  {:swagger {:ui "/swagger-ui"
             :spec "/swagger.json"
             :data {:info {:version "1.0.0"
                           :title "Ticket4Sale API"
                           :description "Ticket4Sale Services"}}}}

  (context "/api" []
    :tags ["tickets"]
    (GET "/shows/:query-date/:show-date" [query-date show-date]
      wrap-json-response (response (parse-object query-date show-date

                                                 all-records)))))
