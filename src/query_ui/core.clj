(ns query-ui.core
  (:require [query-ui.web :as web])
  (:gen-class))

(defn -main [& args]
  (web/start {:port 8000
              :viya-url "http://localhost:52341"
              :viya-table "activity"}))
