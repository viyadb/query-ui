(ns query-ui.web
  (:require [clojure.tools.logging :as log]
            [compojure.core :as comp-core]
            [compojure.route :as comp-route]
            [ring.util.response :as response]
            [ring.middleware.params :as ring-params]
            [org.httpkit.server :as http-server]
            [cheshire.core :as json]
            [query-ui.viyadb :as viyadb]))

(defn- send-json [k]
  (-> (response/response (json/generate-string k))
      (response/content-type "application/json")))

(defn- agg-query [config params]
  (let [extract-param (fn [n] (let [p (get params n)] (remove empty? (if (coll? p) p [p]))))]
    (viyadb/agg-query config
                      (extract-param "dim")
                      (extract-param "metric")
                      (map vector (extract-param "fname") (extract-param "fop") (extract-param "fval"))
                      (map vector (extract-param "sort-col")
                           (map #(Boolean/parseBoolean %1) (extract-param "sort-asc")))
                      (let [l (get params "limit")] (if (empty? l) 0 (Integer/parseInt l))))))

(defn app-routes [config]
  (comp-core/routes
    (comp-core/GET "/" []
                   (response/resource-response "query.html" {:root "public"}))
    (comp-core/GET "/meta" []
                   (send-json (viyadb/table-meta config)))
    (comp-core/GET "/query" {params :query-params}
                   (send-json (agg-query config params)))
    (comp-route/resources "/")))

(defn start [config]
  (http-server/run-server
    (ring-params/wrap-params (app-routes config)) {:port (:port config)})
  (log/info (str "Started HTTP server on port " (:port config))))
