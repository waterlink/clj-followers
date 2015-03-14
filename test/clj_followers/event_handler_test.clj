(ns clj-followers.event-handler-test
  (:require [clojure.test :refer :all]
            [clj-followers.event-handler :as event-handler]))

(deftest build-test
  (let [empty-queue []
        queue (atom empty-queue)

        parse-event (fn [payload] [:parsed-event-for payload])
        add-to-queue (fn [event] (swap! queue conj event))
        handler (event-handler/build {:parse-event parse-event
                                      :add-to-queue add-to-queue})

        examine-handler (fn
                          [f expected-queue]
                          (reset! queue empty-queue)
                          (f)
                          (is (= @queue expected-queue)))]

    (testing "it adds event to queue when it arrives"
      (examine-handler
       (fn []
         (handler :a-client :an-event-1)
         (handler :a-client :an-event-2)
         (handler :a-client :an-event-3))
       [[:parsed-event-for :an-event-1]
        [:parsed-event-for :an-event-2]
        [:parsed-event-for :an-event-3]]))))
