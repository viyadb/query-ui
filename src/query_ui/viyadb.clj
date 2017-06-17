(ns query-ui.viyadb
  (:require [clojure.tools.logging :as log]
            [org.httpkit.client :as http-client]
            [cheshire.core :as json])
  (:import [java.io BufferedReader StringReader]))

(defn table-meta [config]
  (let [uri (str (:viya-url config) "/tables/" (:viya-table config) "/meta")]
    (json/parse-string (:body @(http-client/get uri)) keyword)))

(defn- parse-tsv [tsv]
  (when tsv
    (for [l (line-seq (BufferedReader. (StringReader. tsv)))]
      (clojure.string/split l #"\t"))))

(defn agg-query [config dims metrics filters sortby]
  (let [uri (str (:viya-url config) "/query")
        query {:type "aggregate"
               :table (:viya-table config)
               :select (map #(assoc {} :column %1) (concat dims metrics))
               :filter {:op "and"
                        :filters (map #(zipmap [:column :op :value] %1) filters)}
               :sort (map #(zipmap [:column :ascending] %1) sortby)}]
    (log/info query)
    (-> @(http-client/post uri {:body (json/generate-string query)})
        :body
        parse-tsv)))
