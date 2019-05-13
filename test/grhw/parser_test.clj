(ns grhw.parser-test
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [grhw.parser :as parser]
            [grhw.person :refer :all]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.spec.alpha :as s]
            [clojure.string :as string])
  (:import [grhw.person Person]
           [java.io StringReader]))


(def delimiters
  {:pipe {:string " | " :regex #"\s*\|\s*"}
   :comma {:string ", " :regex #"\s*\,\s*"}
   :space {:string " " :regex #"\s+"}})

(def delimiter-generator (gen/fmap delimiters (gen/elements (keys delimiters))))

(def record-generator (s/gen :person/record))

(defn generate-line
  [{:keys [first-name last-name gender favorite-color date-of-birth]} delimiter]
    (clojure.string/join
      delimiter
      [first-name last-name gender favorite-color date-of-birth])) 

(defspec test-get-delimiter
  100
  (prop/for-all [rec record-generator
                 delim delimiter-generator]
    (let [line (generate-line rec (:string delim))]
      (= (.pattern (parser/get-delimiter line)) (.pattern (:regex delim))))))

(defspec test-line-parse
  100
  (prop/for-all [rec record-generator
                 delim delimiter-generator]
    (let [line (generate-line rec (:string delim))
          re-delim (parser/get-delimiter line)]
      (= (map->Person rec) (parser/parse-line line :delimiter re-delim)))))

(defspec test-parse
  1000
  (prop/for-all [recs (gen/vector record-generator 0 50)
                 delim delimiter-generator]
    (let [source (->> recs
                     (map #(generate-line % (:string delim)))
                     (string/join "\n")
                     (StringReader.)
                     io/reader)]
      (= (map map->Person recs) (parser/parse source)))))