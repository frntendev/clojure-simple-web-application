
(ns ticket4sale.datepicker)

(defn datepicker [value change-handler click-handler]
  [:div.date-picker-container
   [:span.date-title "Show date:"]
   [:input {:on-change change-handler :value value :type "text" :placeholder "YYYY-MM-DD"}]
   [:button {:on-click click-handler} "Submit"]])