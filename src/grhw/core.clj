(ns grhw.core
  (:require [clojure.string :as s]))

(defn -main
  [& files]
  (println (str "Files: " (s/join " " files))))
