(ns clj-followers.recepients-test
  (:require [clojure.test :refer :all]
            [clj-followers.event :as event]
            [clj-followers.recepients :as recepients]))

(deftest for-event-test
  (let [clients {"35" [50 17 62 44]
                 "44" []
                 "21" [13]}
        followers {"44" ["35" "99" "21"]
                   "39" ["35" "44"]
                   "22" ["44"]
                   "21" []}

        get-clients-for (fn [id] (remove nil? (flatten (map clients id))))
        get-followers-of (fn [id] (followers id))
        get-users (fn [] (keys clients))

        examine-recepients (fn [raw-event expected]
                             (let [actual (-> raw-event
                                              (event/parse)
                                              (recepients/for-event
                                               {:clients-for get-clients-for
                                                :all-users get-users
                                                :followers-of get-followers-of}))]
                               (is (= (sort actual) (sort expected)))))]

    (testing "it returns clients of followed person when event is a Follow"
      (examine-recepients "277|F|21|35" [50 17 62 44])
      (examine-recepients "89|F|35|21" [13])
      (examine-recepients "43|F|35|44" [])
      (examine-recepients "41|F|35|999" []))

    (testing "it returns nothing when event is an Unfollow"
      (examine-recepients "277|U|21|35" [])
      (examine-recepients "89|U|35|21" [])
      (examine-recepients "43|U|35|44" [])
      (examine-recepients "41|U|35|999" []))

    (testing "it returns all clients when event is a Broadcast"
      (examine-recepients "384|B" [50 17 62 44 13]))

    (testing "it returns clients of target person when event is a Private Message"
      (examine-recepients "277|P|21|35" [50 17 62 44])
      (examine-recepients "89|P|35|21" [13])
      (examine-recepients "43|P|35|44" [])
      (examine-recepients "41|P|35|999" []))

    (testing "it returns clients of followers of sender user when event is a Status Update"
      (examine-recepients "64|S|44" [50 17 62 44 13])
      (examine-recepients "91|S|39" [50 17 62 44])
      (examine-recepients "31|S|22" [])
      (examine-recepients "122|S|21" [])
      (examine-recepients "43|S|999" []))))
