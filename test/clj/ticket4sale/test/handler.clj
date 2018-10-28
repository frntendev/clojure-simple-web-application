(ns ticket4sale.test.handler
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [ticket4sale.handler :refer :all]
            [ticket4sale.middleware.formats :as formats]
            [muuntaja.core :as m]
            [mount.core :as mount]))

(defn parse-json [body]
  (m/decode formats/instance "application/json" body))

(use-fixtures
  :once
  (fn [f]
    (mount/start #'ticket4sale.config/env
                 #'ticket4sale.handler/app)
    (f)))

(deftest test-app
  (testing "main route"
    (let [response (app (request :get "/"))]
      (is (= 200 (:status response)))))

  (testing "not-found route"
    (let [response (app (request :get "/invalid"))]
      (is (= 404 (:status response)))))

  (testing "show test 1"
    (let [response (app (request :get "/api/shows/2018-01-02/2018-11-25"))]
      (is (=

           {:inventory [{:genre "MUSICAL"
                         :shows [{:title "KINKY BOOTS"
                                  :status "Sale not started"
                                  :tickets_left 0
                                  :tickets_available 0}]}]}

           (parse-json (:body response))))))

  (testing "show test 2"
    (let [response (app (request :get "/api/shows/2018-05-01/2019-02-01"))]
      (is (=

           {:inventory [{:genre "MUSICAL"
                         :shows [{:title "THRILLER  LIVE"
                                  :status "Sale not started"
                                  :tickets_left 0
                                  :tickets_available 0}]}]}

           (parse-json (:body response))))))

  (testing "Past date"
    (let [response (app (request :get "/api/shows/2018-05-01/2018-02-01"))]
      (is (=

           {:error true
            :message "In the past",
            :inventory []}
           (parse-json (:body response)))))))
