(ns clj-followers.event-handler)

(defn- handler
  [{parse-event :parse-event add-to-queue :add-to-queue} client payload]
  (-> payload
      (parse-event)
      (add-to-queue)))

(defn build
  [options]
  (partial handler options))
