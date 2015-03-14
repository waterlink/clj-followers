(ns clj-followers.event-delivery)

(defn- handle
  [{recepients-for :recepients-for deliver-to :deliver-to} event]
  (let [recepients (recepients-for event)]
    (doall (map #(deliver-to % event) recepients))))

(defn build
  [options]
  (partial handle options))
