(defproject grhw "0.1.0-SNAPSHOT"
  :description "Demo file parser with REST interface."
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [cheshire "5.8.1"]
                 [compojure "1.6.1"]
                 [liberator "0.15.3"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-jetty-adapter "1.7.1"]
                 [ring/ring-spec "0.0.4"]]

  :plugins [[lein-ring "0.12.5"]]

  :main grhw.core

  :repl-options {:init-ns grhw.core}

  :profiles {:dev {:dependencies [[org.clojure/test.check "0.10.0-alpha4"]
                                  [ring/ring-mock "0.4.0"]
                                  [javax.servlet/servlet-api "2.5"]]
                   :plugins [[lein-cloverage "1.1.1"]]
                   }}

  :aliases {"genfiles" ["run" "-m" "grhw.genfiles" "target"]})
