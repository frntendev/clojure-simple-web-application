(ns user
  (:require [ticket4sale.config :refer [env]]
            [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [mount.core :as mount]
            [ticket4sale.figwheel :refer [start-fw stop-fw cljs]]
            [ticket4sale.core :refer [start-app]]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(defn start []
  (mount/start-without #'ticket4sale.core/repl-server))

(defn stop []
  (mount/stop-except #'ticket4sale.core/repl-server))

(defn restart []
  (stop)
  (start))


