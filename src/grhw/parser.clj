(ns grhw.parser
  (:require [clojure.spec.alpha :as s]
            [grhw.person :as p])
  (:import [java.util.regex Pattern]
           [java.io BufferedReader]))

(s/def :line/legal-characters (set (str "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                        "abcdefghijklmnopqrstuvwxyz"
                                        "0123456789"
                                        "!#$%&'*+-/=?.<>[]{}\\~()")))

(s/def :line/whitespace #{\space \tab})

(s/def :line/pipe-delimited
  (s/cat
    :first (s/+ :line/legal-characters)
    :whitespace (s/* :line/whitespace)
    :pipe #{\|}
    :post-whitespace (s/* :line/whitespace)
    :next :line/legal-characters
    :rest (s/* char?)))

(s/def :line/comma-delimited
  (s/cat
    :first (s/+ :line/legal-characters)
    :whitespace (s/* :line/whitespace)
    :comma #{\,}
    :post-whitespace (s/* :line/whitespace)
    :next :line/legal-characters
    :rest (s/* char?)))

(s/def :line/space-delimited
  (s/cat
    :first (s/+ :line/legal-characters)
    :whitespace (s/+ :line/whitespace)
    :next :line/legal-characters
    :rest (s/+ char?)))

(s/def :line/line
  (s/or :pipe-delimited :line/pipe-delimited
        :comma-delimited :line/comma-delimited
        :space-delimited :line/space-delimited))

(defn get-delimiter
  "Given a line, returns a regular expression which detects
   the delimiter. Returns nil if there's no legitimate match."
  [line]
  {:pre [(string? line)]}
  (when (s/valid? :line/line (seq line))
    (let [[type & _] (s/conform :line/line (seq line))]
      (case type
        :pipe-delimited #"\s*\|\s*"
        :comma-delimited #"\s*\,\s*"
        :space-delimited #"\s+"))))

(defn parse-line
  "Given a line, parses it and returns the Person record."
  [line & {:keys [delimiter] :or {delimiter #"\s+"}}]
  {:pre [(string? line)]
   :post [#(s/valid? :person/person %)]}
  (->>
    (clojure.string/split line delimiter)
    (apply p/->Person)))

;; TODO: document
(defn parse-stream
  [^BufferedReader reader]
  ;; produce a line-seq

  ;; determine which delimiter is used

  ;; 
  nil
  )