(ns clj-followers.followers)

(def empty-followers {})

(defmulti apply-event
  "Returns followers with following-related event applied. Ignores all
  other events."
  (fn [followers event]
    (:type event)))

(defmethod apply-event "F"
  [followers {follower :from followed :to}]
  (let [its-followers (get followers followed {})
        its-new-followers (assoc its-followers follower 1)]
    (assoc followers followed its-new-followers)))

(defmethod apply-event "U"
  [followers {follower :from unfollowed :to}]
  (let [its-followers (get followers unfollowed {})
        its-new-followers (dissoc its-followers follower)]
    (assoc followers unfollowed its-new-followers)))

(defmethod apply-event :default
  [followers _]
  followers)
