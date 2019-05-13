(ns grhw.parser-test
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [grhw.parser :refer :all]
            [grhw.person :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.pprint :refer [pprint]])
  (:import [grhw.person Person]))


(def delimiters
  {:pipe {:string " | " :regex #"\s*\|\s*"}
   :comma {:string ", " :regex #"\s*\,\s*"}
   :space {:string " " :regex #"\s+"}})

(def delimiter-generator (s/gen (set (keys delimiters))))

(def record-generator (s/gen :person/record))

(defn generate-line
  [{:keys [first-name last-name gender favorite-color date-of-birth]} delimiter]
    (clojure.string/join
      delimiter
      [first-name last-name gender favorite-color date-of-birth])) 

(defspec test-get-delimiter
  100
  (prop/for-all [rec record-generator
                 delim (gen/fmap delimiters delimiter-generator)]
    (let [line (generate-line rec (:string delim))]
      (= (.pattern (get-delimiter line)) (.pattern (:regex delim))))))

(defspec test-line-parse
  100
  (prop/for-all [rec record-generator
                 delim (gen/fmap delimiters delimiter-generator)]
    (let [line (generate-line rec (:string delim))
          re-delim (get-delimiter line)]
      (= (map->Person rec) (parse-line line :delimiter re-delim)))))
