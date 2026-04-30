(ns lifecycle
  (:require
   [preview]
   [site]
   [sse]
   [state]
   [windborn.asset]))

(defn start-assetwatch []
  (windborn.asset/watch state/!asset-definitions state/!asset-watcher state/!asset-data
                        #((resolve 'sse/push-assets!))))

(defn stop-assetwatch []
  (windborn.asset/unwatch state/!asset-definitions state/!asset-watcher state/!asset-data))

(defn start-server []
  (preview/start-server! #'site/inject))

(defn browse {:export true} []
  (start-assetwatch)
  (preview/browse! #'site/inject))

(defn start {:export true} []
  (start-assetwatch)
  (start-server))

(defn stop {:export true} []
  (stop-assetwatch)
  (preview/stop-server!))

(comment
  (browse)
  (stop)
  (start)
  )
