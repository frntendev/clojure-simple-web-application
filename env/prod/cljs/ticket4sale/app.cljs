(ns ticket4sale.app
  (:require [ticket4sale.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
