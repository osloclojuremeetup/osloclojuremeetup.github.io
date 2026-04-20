(ns lifecycle
  (:require
   [preview]
   [site]
   [sse]
   [state]
   [windborn.asset]))

(defn start-assetwatch []
  (windborn.asset/watch state/!asset-definitions state/!asset-watcher state/!asset-data #'sse/push-assets!))

(defn stop-assetwatch []
  (windborn.asset/unwatch state/!asset-definitions state/!asset-watcher state/!asset-data))

(defn start-server []
  (preview/start-server! #'site/inject))

(defn browse! []
  (start-assetwatch)
  (preview/browse! #'site/inject))

(defn start []
  (start-assetwatch)
  (start-server))

(defn stop []
  (stop-assetwatch))

(comment
  (browse!)
  )
