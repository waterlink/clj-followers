(ns clj-followers.sequenced-queue-test
  (:require [clojure.test :refer :all]
            [clj-followers.sequenced-queue :as sequenced-queue]))

(deftest queue-test
  (let [id-fn (fn [value] (:id value))
        new-queue (fn [] (sequenced-queue/build {:id-fn id-fn} 1))

        examine-queue (fn
                        [f expected-queue]
                        (let [queue (new-queue)]
                          (f queue)
                          (is (= @(:value queue) expected-queue))))]

    (testing "adding and consuming elements"
      (examine-queue
       (fn [{add :add-to-queue
             remove :remove-from-queue
             next :next-ready-from-queue}]

         (add {:id 5})
         (is (= (next) []))

         (add {:id 3})
         (is (= (next) []))

         (add {:id 1})
         (is (= (next) [{:id 1}]))

         (add {:id 2})
         (is (= (next) [{:id 1} {:id 2} {:id 3}]))

         (remove [1 2])
         (is (= (next) [{:id 3}]))

         (add {:id 7})
         (add {:id 4})
         (is (= (next) [{:id 3} {:id 4} {:id 5}])))

       {3 {:id 3} 4 {:id 4} 5 {:id 5} 7 {:id 7}}))))
