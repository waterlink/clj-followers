(ns clj-followers.sequenced-queue)

(defn- add-to
  [queue {id-fn :id-fn} value]
  (swap! queue assoc (id-fn value) value))

(defn- raw-remove
  [queue ids]
  (apply (partial dissoc queue) ids))

(defn- remove-from
  [queue last-id _ ids]
  (swap! queue raw-remove ids)
  (reset! last-id (apply max ids)))

(defn- next-ready
  [queue-atom last-id-atom _]
  (let [queue @queue-atom
        last-id @last-id-atom]
    (loop [i (inc last-id) result []]
      (if (contains? queue i)
        (recur (inc i) (conj result (get queue i)))
        result))))

(defn build
  [options start-from]
  (let [value (atom {})
        last-id (atom (dec start-from))]
    {:value value
     :last-id last-id
     :add-to-queue (partial add-to value options)
     :remove-from-queue (partial remove-from value last-id options)
     :next-ready-from-queue (partial next-ready value last-id options)}))
