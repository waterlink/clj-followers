(ns clj-followers.core
  (:require [clj-followers.endpoint :as endpoint]

            [clj-followers.event :as event]
            [clj-followers.clients :as clients]
            [clj-followers.followers :as followers]
            [clj-followers.recepients :as recepients]

            [clj-followers.client-handler :as client-handler]
            [clj-followers.event-handler :as event-handler]
            [clj-followers.ready-handler :as ready-handler]
            [clj-followers.event-consumer :as event-consumer]

            [clj-followers.sequenced-queue :as sequenced-queue]
            [clj-followers.event-delivery :as event-delivery]))

(def ^:private settings {:source-port 9090 :client-port 9099})

(defn -main
  []

  (let [clients (atom clients/empty-clients)
        add-client (fn [user client] (swap! clients clients/handle-client client user))
        client-handler (client-handler/build {:add-client add-client})

        {add-to-queue :add-to-queue
         remove-from-queue :remove-from-queue
         next-ready-from-queue :next-ready-from-queue} (sequenced-queue/build
                                                        {:id-fn #(:id %)}
                                                        1)

        event-handler (event-handler/build {:parse-event event/parse
                                            :add-to-queue add-to-queue})

        followers (atom followers/empty-followers)
        clients-of (fn [user] (keys (get @clients user {})))
        clients-for (fn [users] (flatten (remove nil? (map clients-of users))))
        recepients-options {:clients-for clients-for
                            :all-users (fn [] (keys @clients))
                            :followers-of (fn [user] (keys (get @followers user {})))}
        recepients-for (fn [event] (recepients/for-event event recepients-options))
        apply-event (fn [event] (swap! followers followers/apply-event event))
        deliver-to (fn [client event] (endpoint/send-message client (:payload event)))
        event-delivery (event-delivery/build {:recepients-for recepients-for
                                              :deliver-to deliver-to})
        ready-handler (ready-handler/build {:deliver event-delivery
                                            :apply-event apply-event})
        event-consumer (event-consumer/build {:next-ready-from-queue next-ready-from-queue
                                              :remove-from-queue remove-from-queue
                                              :handler ready-handler})
        event-consumer-fn (fn [_]
                            (loop []
                              (event-consumer)
                              (recur)))
        event-consumer-agent (agent nil)]
    (set-error-handler! event-consumer-agent
                        (fn [_ error] (println "[event consumer] ERROR:" error)))
    (send-off event-consumer-agent event-consumer-fn)
    (endpoint/serve "client" (:client-port settings) client-handler)
    (endpoint/serve "source" (:source-port settings) event-handler)
    (println "Started...")))
