(ns ticket4sale.navbar
  (:require [baking-soda.core :as b]
            [reagent.core :as r]))

(defn navbar []
  (r/with-let [expanded? (r/atom true)]
    [b/Navbar {:light true
               :class-name "navbar-dark"
               :expand "md"}
     [b/NavbarBrand {:href "/"} "Ticket4Sale"]
     [b/NavbarToggler {:on-click #(swap! expanded? not)}]
     [b/Collapse {:is-open @expanded? :navbar true}
      [b/Nav {:class-name "mr-auto" :navbar true}]]]))