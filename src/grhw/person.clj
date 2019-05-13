(ns grhw.person
  (:require [clojure.spec.alpha :as s]))

(defrecord Person [first-name last-name gender favorite-color date-of-birth])

(s/def :person/identifier (s/and string? #(re-matches #"[^\s\|\,]+" %)))
(s/def :person/first-name :person/identifier)
(s/def :person/last-name :person/identifier)
(s/def :person/gender :person/identifier)
(s/def :person/favorite-color :person/identifier)
(s/def :person/date-of-birth :person/identifier)

(s/def :person/record
  (s/keys :req-un
    [:person/first-name
     :person/last-name
     :person/gender
     :person/favorite-color
     :person/date-of-birth]))
