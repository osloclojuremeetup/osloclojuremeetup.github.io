(ns ^:clj-reload/no-reload state)

;; Server & SSE sessions
(defonce !server (atom nil))
(defonce !sessions (atom nil))

;; Dev reloads
(defonce !asset-watcher (atom nil))
(defonce !db (atom nil))
