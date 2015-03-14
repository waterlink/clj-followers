(ns clj-followers.core
  (:require [clj-followers.endpoint :as endpoint]))

(def ^:private settings {:source-port 9090 :client-port 9099})

(defn- client-handler
  [client payload]
  (when-not (nil? payload)
    (println (str "got client: " payload))))

(defn- source-handler
  [client payload]
  (when-not (nil? payload)
    (println (str "got event: " payload))))

(defn -main
  []
  (endpoint/serve "client" (:client-port settings) client-handler)
  (endpoint/serve "source" (:source-port settings) source-handler)
  (println "Started..."))
