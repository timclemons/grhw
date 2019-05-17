(ns grhw.person
  (:require [clojure.spec.alpha :as s]
            [clojure.test.check.generators :as gen])
  (:import [java.util Date]))

(defrecord Person [first-name last-name gender favorite-color date-of-birth])

(s/def :person/identifier
  (s/and string? seq #(re-matches #"[^\s\|\,]+" %)))

(s/def :person/first-name :person/identifier)
(s/def :person/last-name :person/identifier)
(s/def :person/gender
  (s/with-gen :person/identifier
    (fn [] (gen/frequency [[5 (gen/return "female")]
                           [5 (gen/return "male")]
                           [1 (s/gen :person/identifier)]]))))

(s/def :person/favorite-color :person/identifier)
(s/def :person/date-of-birth
  (s/with-gen #(instance? Date %)
    (fn [] (gen/fmap #(Date. %) (s/gen pos-int?)))))

(s/def :person/record
  (s/keys :req-un
    [:person/first-name
     :person/last-name
     :person/gender
     :person/favorite-color
     :person/date-of-birth]))
