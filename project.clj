(defproject pet-redo "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [seesaw "1.4.5"]
                 [helpers "1"]
                 [clojure.java-time "0.3.2"]
                 [criterium "0.4.4"]]
  :main ^:skip-aot pet-redo.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
