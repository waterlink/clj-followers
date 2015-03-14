(ns clj-followers.event-consumer)

(defn- consume
  [{next-ready-from-queue :next-ready-from-queue
    remove-from-queue :remove-from-queue
    handler :handler}]
  (let [events (next-ready-from-queue)
        ids-to-remove (map #(:id %) events)]
    (doall (map handler events))
    (remove-from-queue ids-to-remove)))

(defn build
  [options]
  (partial consume options))
