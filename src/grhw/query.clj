(ns grhw.query
  (:require [clojure.string :as s])
  (:import [grhw.person Person]))


(defn gender-sort-order
  [^Person rec]
  (case (:gender rec)
    "female" 1
    "male" 2
    3))

(def gender-last-name-sort
  (juxt gender-sort-order :last-name))

(def date-of-birth-sort :date-of-birth)

(defn last-name-sort
  [^Person {:keys [last-name]}]
  (s/lower-case last-name))
