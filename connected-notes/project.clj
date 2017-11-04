(defproject connected-notes "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-beta3"]
                 ;[thinktopic/experiment "0.9.23-SNAPSHOT"]
                 [thinktopic/experiment "0.9.22"]
                 [org.clojure/tools.cli "0.3.5"]
                 ;;If you need cuda 8...
                 [org.bytedeco.javacpp-presets/cuda "8.0-1.2"]
                 ;;If you need cuda 7.5...
                 ;;[org.bytedeco.javacpp-presets/cuda "7.5-1.2"]
                 ]
  :main connected-notes.core
  :jvm-opts ["-Xmx2000m"]
  :profiles {:liq {:dependencies [[mogenslund/liquid "0.8.2"]]
             :main dk.salza.liq.core}}
  :aliases {"liq" ["with-profile" "liq" "run" "--load=.liq"]}

  :clean-targets ^{:protect false} [:target-path
                                    "figwheel_server.log"
                                    "resources/public/out/"
                                    "resources/public/js/app.js"])

