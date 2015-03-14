(ns clj-followers.event
  (:require [clojure.string :as str]))

(defn- with-parsed-int
  "Tries to parse int. In case of success calls provided function with
  the result. Otherwise returns nil."
  [s fn]
  (if (nil? (re-matches #"^\d+$" s))
    nil
    (fn (Integer. s))))

(defn parse
  "Parses provided string into event hashmap."
  [payload]
  (let [[id type from to] (str/split payload #"\|" 4)]
    (with-parsed-int id
      (fn [id] {:id id :type type :from from :to to :payload payload}))))
