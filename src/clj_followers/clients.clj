(ns clj-followers.clients)

(def empty-clients {})

(defn- change-for-one-user
  [clients user f]
  (let [its-clients (get clients user {})
        its-new-clients (f its-clients)]
    (assoc clients user its-new-clients)))

(defn handle-client
  [clients client-socket user]
  (change-for-one-user clients user #(merge % {client-socket 1})))

(defn remove-client
  [clients client-socket user]
  (change-for-one-user clients user #(dissoc % client-socket)))
