(ns clj-followers.event-consumer-test
  (:require [clojure.test :refer :all]
            [clj-followers.event-consumer :as event-consumer]))

(deftest consume-test
  (let [ready (atom [])
        removed (atom [])
        handled (atom [])

        next-ready-from-queue (fn [] @ready)
        remove-from-queue (fn [ids] (swap! removed concat ids))
        handler (fn [event] (swap! handled conj (:id event)))
        consume (event-consumer/build
                 {:next-ready-from-queue next-ready-from-queue
                  :remove-from-queue remove-from-queue
                  :handler handler})]

    (testing "consuming some events"
      (reset! ready [{:id 5} {:id 6} {:id 7} {:id 8}])
      (consume)
      (is (= @removed [5 6 7 8]))
      (is (= @handled [5 6 7 8]))

      (reset! ready [{:id 15} {:id 16}])
      (consume)
      (is (= @removed [5 6 7 8 15 16]))
      (is (= @handled [5 6 7 8 15 16])))))
