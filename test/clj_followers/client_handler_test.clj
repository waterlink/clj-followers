(ns clj-followers.client-handler-test
  (:require [clojure.test :refer :all]
            [clj-followers.client-handler :as client-handler]))

(deftest build-test
  (let [clients-atom (atom nil)

        add-client (fn [user client] (reset! clients-atom [:added user client]))
        handler (client-handler/build {:add-client add-client})

        examine-handler (fn
                          [client payload expected-change]
                          (reset! clients-atom nil)
                          (handler client payload)
                          (is (= @clients-atom expected-change)))]

    (testing "it adds client for user specified in payload"
      (examine-handler :a-client "a-user" [:added "a-user" :a-client]))))
