(ns clj-followers.event-test
  (:require [clojure.test :refer :all]
            [clj-followers.event :as event]))

(deftest parse-test
  (let [examine-parse (fn [payload expected]
                        (let [actual (event/parse payload)]
                          (is (= actual expected))))]

    (testing "it parses event with all data properly"
      (examine-parse "553|F|60|50"
                     {:id 553 :type "F" :from "60" :to "50" :payload "553|F|60|50"}))

    (testing "it parses event with missing :to properly"
      (examine-parse "95|S|60"
                     {:id 95 :type "S" :from "60" :to nil :payload "95|S|60"}))

    (testing "it parses event with missing :to and :from properly"
      (examine-parse "44|B"
                     {:id 44 :type "B" :from nil :to nil :payload "44|B"}))

    (testing "it can't parse event with invalid id"
      (examine-parse "3AB4C5|F|33|55" nil))))
