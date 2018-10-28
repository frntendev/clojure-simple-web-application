(ns ticket4sale.date)

(def today-date
  (let [today (js.Date.)]
    (let [dd (.getDate today)]
      (let [mm (+ (.getMonth today) 1)]
        (let [yyyy (.getFullYear today)]
          (str yyyy "-" mm "-"
               dd))))))