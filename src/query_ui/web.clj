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

(defn- vectorize [e]
  (if (coll? e) e [e]))

(defn- agg-query [config params]
  (viyadb/agg-query config
                    (vectorize (get params "dim"))
                    (vectorize (get params "metric"))
                    (map vector
                         (vectorize (get params "fname"))
                         (vectorize (get params "fop"))
                         (vectorize (get params "fval")))))

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
