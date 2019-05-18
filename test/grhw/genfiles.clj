(ns grhw.genfiles
  (:require [grhw.parser-test :refer :all]
            [grhw.parser :as parser]
            [grhw.person :refer :all]
            [clojure.spec [alpha :as spec]]
            [clojure.test.check [generators :as gen]])
  (:import [java.text SimpleDateFormat]
           [java.util Date]))

(def non-empty-identifier
  (gen/not-empty gen/string-alphanumeric))

(def gender-generator
  (gen/frequency [[5 (gen/return "female")]
                   [5 (gen/return "male")]
                   [1 non-empty-identifier]]))

(def date-of-birth-generator
  (gen/fmap
    (partial apply (partial format "%d/%02d/%d"))
    (gen/tuple
      (gen/choose 1 12)
      (gen/choose 1 31)
      (gen/choose 1870 2018))))

(def person-generator
  (gen/fmap
    (partial apply ->Person)
    (gen/tuple
      non-empty-identifier
      non-empty-identifier
      gender-generator
      non-empty-identifier
      date-of-birth-generator)))

(defn -main
  [target-dir]
  (let [date-formatter (SimpleDateFormat. parser/date-format)
        records (->> (gen/generate (gen/vector person-generator 200))
                  (map (fn [rec]
                         (update rec :date-of-birth #(.parse date-formatter %)))))
        delim (gen/generate (gen/fmap :string delimiter-generator))
        lines (map #(generate-line % delim) records)]
    (print (clojure.string/join "\n" lines))))
