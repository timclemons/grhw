(ns grhw.core
  (:require [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as s]
            [grhw.parser :as p]))

(defn- ingest-file
  [filename]
  (with-open [file (io/reader filename)]
    (p/parse file)))

(defn -main
  [& files]
  (let [records (mapcat ingest-file files)]
    (pprint records)))
