(ns grhw.http_test
  (:require [clojure.test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.string :as s]
            [grhw.parser :as parser]
            [grhw.person :refer :all]
            [grhw.query :as query]
            [grhw.http :as http]
            [grhw.parser-test :as pt]
            [ring.mock.request :as mock]
            [cheshire.core :as json])
  (:import [java.text SimpleDateFormat]))

(defn sorted-pair? 
  [[left right] keyfn & {:keys [compfn] :or {compfn compare}}]
    ((complement pos?) (compfn (keyfn left) (keyfn right))))

(defspec test-get-gender-endpoint
  100
  (prop/for-all [recs (gen/vector pt/record-generator)]
    (let [handler (http/http-routes recs)
          response (handler (mock/request :get "/records/gender"))
          queried-recs (-> response :body (json/parse-string keyword))]
      (is (= (:status response) 200))
      (is (= (count recs) (count queried-recs)))
      (is (->> queried-recs
            (partition 2 1)
            (every? #(sorted-pair? % (juxt query/gender-sort-order :last-name))))))))

(defspec test-get-birthdate-endpoint
  100
  (prop/for-all [recs (gen/vector pt/record-generator)]
    (let [handler (http/http-routes recs)
          response (handler (mock/request :get "/records/birthdate"))
          date-formatter (SimpleDateFormat. parser/date-format)
          queried-recs (->> response
                         :body
                         (#(json/parse-string % keyword))
                         (map (fn [rec] (update-in rec [:date-of-birth] #(.parse date-formatter %)))))]
      (is (= (:status response) 200))
      (is (= (count recs) (count queried-recs)))
      (is (->> queried-recs
            (partition 2 1)
            (every? #(sorted-pair? % :date-of-birth)))))))

(defspec test-get-name-endpoint
  100
  (prop/for-all [recs (gen/vector pt/record-generator)]
    (let [handler (http/http-routes recs)
          response (handler (mock/request :get "/records/name"))
          queried-recs (-> response :body (json/parse-string keyword))]
      (is (= (:status response) 200))
      (is (= (count recs) (count queried-recs)))
      (is (->> queried-recs
            (partition 2 1)
            (every? #(sorted-pair? %
                       (comp s/lower-case :last-name)
                       :compfn (comp - compare))))))))