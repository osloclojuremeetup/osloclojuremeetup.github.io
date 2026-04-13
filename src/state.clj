(ns ^:clj-reload/no-reload state)

;; SSE connections
(defonce !sessions (atom nil))

;; Asset watch
(defonce !asset-definitions (atom nil))
(defonce !asset-watcher (atom nil))
(defonce !asset-data (atom {}))
