(ns clj-followers.followers-test
  (:require [clojure.test :refer :all]
            [clj-followers.event :as event]
            [clj-followers.followers :as followers]))

(deftest apply-event-test
  (let [followers-map {"44" {"35" 1 "99" 1 "21" 1}
                       "39" {"35" 1 "44" 1}
                       "22" {"44" 1}
                       "21" {}}

        apply-raw (fn [raw] (-> raw
                                (event/parse)
                                ((partial followers/apply-event followers-map))))

        examine #(is (= %1 %2))
        examine-apply-event (fn
                              ([raw user expected]
                               (let [actual (-> raw (apply-raw) (get user))]
                                 (examine actual expected)))
                              ([raw expected]
                               (let [actual (-> raw (apply-raw))]
                                 (examine actual expected))))]

    (testing "it ignores events not related to following"
      (examine-apply-event "54|B" followers-map)
      (examine-apply-event "73|P|33|94" followers-map)
      (examine-apply-event "401|S|178" followers-map))

    (testing "it creates new follow when event is a Follow"
      (examine-apply-event "91|F|75|31" "31" {"75" 1})
      (examine-apply-event "466|F|79|39" "39" {"35" 1 "44" 1 "79" 1}))

    (testing "it does not create new follow when event is a Follow and follow already exist"
      (examine-apply-event "54|F|44|22" followers-map))

    (testing "it removes a follow when event is an Unfollow"
      (examine-apply-event "98|U|44|22" "22" {})
      (examine-apply-event "152|U|99|44" "44" {"35" 1 "21" 1}))

    (testing "it ignores an Unfollow event when such follow does not exist"
      (examine-apply-event "933|U|39|44" followers-map))))
