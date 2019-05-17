(ns grhw.parser
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [grhw.person :as p])
  (:import [java.util.regex Pattern]
           [java.io BufferedReader]
           [java.text SimpleDateFormat]))

(s/def :line/value-character (set (str "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                        "abcdefghijklmnopqrstuvwxyz"
                                        "0123456789"
                                        "!#$%&'*+-/=?.<>[]{}\\~()")))

(s/def :line/whitespace #{\space \tab})

(s/def :line/pipe-delimited
  (s/cat
    :first (s/+ :line/value-character)
    :whitespace (s/* :line/whitespace)
    :pipe #{\|}
    :post-whitespace (s/* :line/whitespace)
    :next :line/value-character
    :rest (s/* char?)))

(s/def :line/comma-delimited
  (s/cat
    :first (s/+ :line/value-character)
    :whitespace (s/* :line/whitespace)
    :comma #{\,}
    :post-whitespace (s/* :line/whitespace)
    :next :line/value-character
    :rest (s/* char?)))

(s/def :line/space-delimited
  (s/cat
    :first (s/+ :line/value-character)
    :whitespace (s/+ :line/whitespace)
    :next :line/value-character
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
        :pipe-delimited #"\|"
        :comma-delimited #"\,"
        :space-delimited #"\s+"))))

(def date-format "MM/dd/yyyy")

(defn post-parse-fns
  []
  (let [date-formatter (SimpleDateFormat. date-format)]
    [identity identity identity identity #(.parse date-formatter %)]))

(defn parse-line
  "Given a line, parses it and returns the resulting Person record."
  [line & {:keys [delimiter]
           :or {delimiter (get-delimiter line)}}]
  {:pre [(string? line)]
   :post [#(s/valid? :person/person %)]}
  (let [post-fns (post-parse-fns)]
    (when (instance? Pattern delimiter)
      (try
        (->>
          (string/split line delimiter)
          (map string/trim)
          (map #(%1 %2) post-fns)
          (apply p/->Person))
        (catch Exception e nil)))))

(defn parse
  "Given a reader, parses each line and returns the resulting vector of 
   Person records."
  [^BufferedReader reader]
  (if-let [source (line-seq reader)]
    (when-let [delim (get-delimiter (first source))]
      (let [date-formatter (SimpleDateFormat. "MM/dd/yyyy")
            formatter [identity identity identity identity #(.parse date-formatter %)]]
        (into []
          (comp
            (remove nil?)
            (map string/trim)
            (map #(parse-line % :delimiter delim :post-fn formatter)))
          source)))
    []))
