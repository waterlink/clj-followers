(ns clj-followers.clients-test
  (:require [clojure.test :refer :all]
            [clj-followers.clients :as clients]))

(deftest clients-test
  (let [clients {"256" {17 1 19 1 24 1 5 1}
                 "77" {}
                 "95" {81 1}}

        examine-clients (fn
                          [fn client-socket user expected-for-user]
                          (let [actual (fn
                                        clients
                                        client-socket
                                        user)
                                actual-without-user (dissoc actual user)
                                expected-without-user (dissoc clients user)
                                actual-for-user (keys (get actual user))]
                            (is (= actual-without-user expected-without-user))
                            (is (= (sort actual-for-user) (sort expected-for-user)))))]

    (testing "it assigns new client connection for specific user when it is a new client"
      (examine-clients clients/handle-client 29 "256" [17 19 24 5 29])
      (examine-clients clients/handle-client 44 "77" [44])
      (examine-clients clients/handle-client 99 "25" [99]))

    (testing "it removes client connection when it is a remove-client"
      (examine-clients clients/remove-client 24 "256" [17 19 5])
      (examine-clients clients/remove-client 999 "256" [17 19 24 5])
      (examine-clients clients/remove-client 55 "77" [])
      (examine-clients clients/remove-client 99 "25" []))))
