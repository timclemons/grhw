(ns grhw.core
  (:require [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as s]
            [grhw.parser :as p]
            [grhw.query :as q]
            [grhw.http :as http]))

(defn- ingest-file
  [filename]
  (with-open [file (io/reader filename)]
    (p/parse file)))

(defn -main
  [& files]
  (let [records (mapcat ingest-file files)]
    (println "GENDER SORT:")
    (pprint (q/sort-by-gender-last-name records))

    (println)
    (println "DOB SORT:")
    (pprint (q/sort-by-date-of-birth records))

    (println)
    (println "LAST NAME SORT:")
    (pprint (q/sort-by-last-name records))

    (http/start records)))
