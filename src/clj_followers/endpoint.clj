(ns clj-followers.endpoint
  (:require [clojure.java.io :as io])
  (:import [java.net ServerSocket]))

(defn- receive-message
  "Reads a message from given socket."
  [reader]
  (.readLine (io/reader reader)))

(defn send-message
  "Sends a message to given socket."
  [socket message]
  (when-not (nil? message)
    (println "Send: " message)
    (let [formatted-message (str message "\r\n")
          writer (io/writer socket)]
      (.write writer formatted-message)
      (.flush writer))))

(defn- worker-error-handler
  [name worker error]
  (println "[" name "] ERROR: " error "; closing connection")
  (.close @worker))

(defn- handle-message
  "Handles one message from socket."
  [client-socket message handler]
  (handler client-socket message))

(defn- handle-connection
  "Handles one connection."
  [client-socket reader handler]
  (loop []
    (let [message (receive-message reader)]
      (if-not (nil? message)
        (handle-message client-socket message handler)
        (Thread/sleep 10))
      (recur)))
  client-socket)

(defn- handle-socket
  "Accepts connections on server socket."
  [name server-socket handler]
  (loop []
    (let [client-socket (.accept server-socket)
          reader (io/reader client-socket)
          worker (agent client-socket)]
      (.setKeepAlive client-socket true)
      (set-error-handler! worker worker-error-handler)
      (send-off worker handle-connection reader handler))
    (recur)))

(defn serve
  "Runs server on specific port."
  [name port handler]
  (future
    (with-open [server-socket (ServerSocket. port)]
      (handle-socket name server-socket handler))))
