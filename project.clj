(defproject grhw "0.1.0-SNAPSHOT"
  :description "Demo file parser with REST interface."
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [compojure "1.6.1"]
                 [liberator "0.15.3"]]

  :main grhw.core

  :repl-options {:init-ns grhw.core}

  :profiles {:dev {:dependencies [[org.clojure/test.check "0.9.0"]]
                   :plugins [[lein-cloverage "1.1.1"]]
                   }}

  :aliases {"genfiles" ["run" "-m" "grhw.genfiles" "target"]})
