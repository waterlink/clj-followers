(ns clj-followers.event-delivery-test
  (:require [clojure.test :refer :all]
            [clj-followers.event-delivery :as event-delivery]))

(deftest event-delivery-test
  (let [recepients (atom {})
        delivered (atom {})

        recepients-for (fn [event] (get @recepients (:id event) []))
        deliver-to (fn [client event]
                     (let [its-events (get @delivered client [])
                           its-new-events (conj its-events (:id event))]
                       (swap! delivered assoc client its-new-events)))

        handler (event-delivery/build {:recepients-for recepients-for
                                       :deliver-to deliver-to})]

    (testing "deliver some events"
      (reset! recepients {55 [1 2 3 4 5]
                          78 [99 78 44 91]
                          99 [13]
                          155 [13 15]
                          41 []})

      (handler {:id 999})
      (is (= @delivered {}))

      (handler {:id 41})
      (is (= @delivered {}))

      (handler {:id 99})
      (is (= @delivered {13 [99]}))

      (handler {:id 155})
      (is (= @delivered {13 [99 155]
                         15 [155]}))

      (handler {:id 55})
      (is (= @delivered {13 [99 155]
                         15 [155]
                         1 [55]
                         2 [55]
                         3 [55]
                         4 [55]
                         5 [55]})))))
