(ns ticket4sale.ticketlist)

(defn render-ticket-row [shows genre]
  (for [item shows]
    ^{:key (:title item)}
    [:div.show-container.grid-5
     [:span (:title item)]
     [:span (:tickets_left item)]
     [:span.ticket-available (:tickets_available item)]
     [:span.ticket-status (:status item)]
     [:span (if (= genre "MUSICAL") 70 (if (= genre "COMEDY") 50 (if (= genre "DRAMA") 40)))]]))

(defn ticket-list [item]
  (let [genre (get item "genre")]
    [:div.container
     [:p.genre genre]
     [:div.titles.grid-5
      [:span "Title"]
      [:span "Tickets Left"]
      [:span "Tickets Available"]
      [:span "Status"]
      [:span "Price"]]
     (render-ticket-row (get item "shows") genre)]))