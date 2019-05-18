(ns grhw.parser-test
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [grhw.parser :as parser]
            [grhw.person :refer :all]
            [grhw.query :as query]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.spec.alpha :as s]
            [clojure.string :as string])
  (:import [grhw.person Person]
           [java.io StringReader]
           [java.util Date]
           [java.text SimpleDateFormat]))

(def delimiters
  {:pipe {:string " | " :regex #"\|"}
   :comma {:string ", " :regex #"\,"}
   :space {:string " " :regex #"\s+"}})

(def delimiter-generator (gen/fmap delimiters (gen/elements (keys delimiters))))

(def record-generator (s/gen :person/record))

(def date-formatter (SimpleDateFormat. parser/date-format))

(defn generate-line
  [{:keys [first-name last-name gender favorite-color date-of-birth]} delimiter]
    (let [dob (.format date-formatter date-of-birth)]
      (clojure.string/join
        delimiter
        [first-name last-name gender favorite-color dob])))

(defn date->string
  [rec]
  (update rec :date-of-birth #(.format date-formatter %)))

(defspec test-get-delimiter
  100
  (prop/for-all [rec record-generator
                 delim delimiter-generator]
    (let [line (generate-line rec (:string delim))]
      (is (=
        (.pattern (parser/get-delimiter line))
        (.pattern (:regex delim)))))))

(defspec test-line-parse
  100
  (prop/for-all [rec record-generator
                 delim delimiter-generator]
    (let [line (generate-line rec (:string delim))
          re-delim (parser/get-delimiter line)]
      (is (=
        (-> rec (date->string) (map->Person))
        (->
         (parser/parse-line line :delimiter re-delim)
         (date->string)))))))

(defspec test-parse
  100
  (prop/for-all [recs (gen/vector record-generator 0 50)
                 delim delimiter-generator]
    (let [source (->> recs
                     (map #(generate-line % (:string delim)))
                     (string/join "\n")
                     (StringReader.)
                     io/reader)]
      (is (=
        (sequence (comp (map date->string) (map map->Person)) recs)
        (->> (parser/parse source) (map date->string)))))))
