(ns ^:clj-reload/no-reload state)

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
