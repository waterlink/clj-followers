(ns clj-followers.client-handler)

(defn- handler
  [{add-client :add-client} client payload]
  (let [user payload]
    (add-client user client)))

(defn build
  [options]
  (partial handler options))
