(ns clj-followers.followers)

(def empty-followers {})

(defn- change-user-followers
  [followers user f]
  (let [its-followers (get followers user {})
        its-new-followers (f its-followers)]
    (assoc followers user its-new-followers)))

(defmulti apply-event
  "Returns followers with following-related event applied. Ignores all
  other events."
  (fn [followers event]
    (:type event)))

(defmethod apply-event "F"
  [followers {follower :from followed :to}]
  (change-user-followers followers followed #(assoc % follower 1)))

(defmethod apply-event "U"
  [followers {follower :from unfollowed :to}]
  (change-user-followers followers unfollowed #(dissoc % follower)))

(defmethod apply-event :default
  [followers _]
  followers)
