(ns grhw.http
  (:require [liberator.core :refer [resource]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [routes ANY]]
            [compojure.route :refer [not-found]]
            [cheshire.core :as json]
            [grhw.query :as query]
            [grhw.parser :as parser])
  (:import [java.text SimpleDateFormat]))

(defn format-records
  [recs date-formatter]
  (->> recs
    (map (fn [rec] (update-in rec [:date-of-birth]#(.format date-formatter %))))
    (json/generate-string)))

(defn http-routes
  [recs]
  (let [date-formatter (SimpleDateFormat. parser/date-format)
        state (atom recs)]
    (routes
      (ANY "/records" []
        (resource
          :allowed-methods [:post]
          :available-media-types ["text/plain"]
          :processable? (fn [ctx]
                          (when-let [rec (-> (get-in ctx [:request :body])
                                           slurp
                                           parser/parse-line)]
                            [true {:rec rec}]))
          :post! (fn [ctx]
                   (let [{:keys [rec]} ctx]
                     (swap! state #(conj % rec))))))
      (ANY "/records/gender" []
        (resource
          :allowed-methods [:get]
          :available-media-types ["application/json"]
          :handle-ok (fn [_]
                       (-> @state
                         query/sort-by-gender-last-name
                         (format-records date-formatter)))))
      (ANY "/records/birthdate" []
        (resource
          :allowed-methods [:get]
          :available-media-types ["application/json"]
          :handle-ok (fn [_]
                       (-> @state
                         query/sort-by-date-of-birth
                         (format-records date-formatter)))))
      (ANY "/records/name" []
        (resource
          :allowed-methods [:get]
          :available-media-types ["application/json"]
          :handle-ok (fn [_]
                       (-> @state
                         query/sort-by-last-name
                         (format-records date-formatter)))))
      (not-found "Not found."))))

(defn start
  [recs]
  (let [routes (http-routes recs)]
    (run-jetty (wrap-params routes) {:port 8080})))