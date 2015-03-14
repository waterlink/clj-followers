(ns clj-followers.ready-handler)

(defn- handle
  [{deliver :deliver apply-event :apply-event} event]
  (apply-event event)
  (deliver event))

(defn build
  [options]
  (partial handle options))
