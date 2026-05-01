(ns ^:clj-reload/no-reload state
  (:require [clojure.string :as str]))

;; Server & SSE sessions
(defonce !server (atom nil))
(defonce !sessions (atom nil))

;; Asset watch
(defonce !asset-definitions (atom nil))
(defonce !asset-watcher (atom nil))
(defonce !asset-data (atom {}))

;; Dev reloads
(defonce !asset-headers (atom nil))
(defonce !db (atom nil))

(comment
  ;; Inspect checksums
  (->> @!asset-data
       (mapv (juxt first (comp #(subs % 0 5) :sha1 second))))

  )
