(ns clj-followers.recepients)

(defmulti for-event
  "Returns recepients that should receive this event as a
  notification."
  (fn [event options]
    (:type event)))

(defmethod for-event "F"
  [{followed :to} {clients-for :clients-for}]
  (clients-for [followed]))

(defmethod for-event "B"
  [_ {clients-for :clients-for all-users :all-users}]
  (clients-for (all-users)))

(defmethod for-event "P"
  [{message-recepient :to} {clients-for :clients-for}]
  (clients-for [message-recepient]))

(defmethod for-event "S"
  [{sender :from} {clients-for :clients-for followers-of :followers-of}]
  (clients-for (followers-of sender)))

(defmethod for-event :default
  [_ _]
  [])
