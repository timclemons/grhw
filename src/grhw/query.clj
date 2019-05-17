(ns grhw.query
  (:require [clojure.string :as s])
  (:import [grhw.person Person]))

(defn gender-sort-order
  [^Person rec]
  (case (s/lower-case (:gender rec))
    "female" 1
    "male" 2
    3))

(defn sort-by-gender-last-name
  [recs]
  (sort-by
    (juxt gender-sort-order :last-name)
    recs))

(defn sort-by-date-of-birth
  [recs]
  (sort-by :date-of-birth recs))

(defn sort-by-last-name
  [recs]
  (sort-by
    (comp s/lower-case :last-name)
    (comp - compare)
    recs))
