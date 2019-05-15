(ns grhw.core
  (:require [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as s]
            [grhw.parser :as p]
            [grhw.query :as q]))


;; TODO:
;; * change storage of date-of-birth non-string type

(defn- ingest-file
  [filename]
  (with-open [file (io/reader filename)]
    (p/parse file)))

(defn -main
  [& files]
  (let [records (mapcat ingest-file files)]
    (println "GENDER SORT:")
    (doseq [rec (sort-by q/gender-last-name-sort records)]
      (pprint rec))

    (println)
    (println "DOB SORT:")
    (doseq [rec (sort-by q/date-of-birth-sort records)]
      (pprint rec))

    (println)
    (println "LAST NAME SORT:")
    (doseq [rec (sort-by q/last-name-sort (comp - compare) records)]
      (pprint rec))))
