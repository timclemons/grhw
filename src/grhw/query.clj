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

(defn date-of-birth-sort
  [^Person {:keys [date-of-birth]}]
    (let [[m d y] (->> (s/split date-of-birth #"/") (map #(Integer/parseInt %)))]
      [y m d]))

(defn last-name-sort
  [^Person {:keys [last-name]}]
  (s/lower-case last-name))
