(ns clj-followers.endpoint
  (:require [clojure.java.io :as io])
  (:import [java.net ServerSocket]))

(defn- receive-message
  "Reads a message from given socket."
  [reader]
  (.readLine (io/reader reader)))

(defn- send-message
  "Sends a message to given socket."
  [socket message]
  (if-not (nil? message)
    (let [formatted-message (str message "\r\n")]
      (.write (io/writer socket) message))))

(defn- handle-message
  [client-socket message handler]
  (handler client-socket message))

(defn- handle-connection
  [client-socket reader handler]
  (loop []
    (let [message (receive-message reader)]
      (when-not (nil? message)
        (handle-message client-socket message handler)
        (recur)))))

(defn- handle-socket
  [name server-socket handler]
  (loop []
    (let [client-socket (.accept server-socket)
          reader (io/reader client-socket)]
      (future
        (handle-connection client-socket reader handler)
        (println (str "[" name "] closing"))
        (.close client-socket)))
    (recur)))

(defn serve
  [name port handler]
  (future
    (with-open [server-socket (ServerSocket. port)]
      (handle-socket name server-socket handler))))
