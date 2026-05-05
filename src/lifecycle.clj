(ns lifecycle
  (:require
   [lifecycle.preview :as preview]
   [lifecycle.assetwatch :as assetwatch]
   [assets]
   [site]
   [sse]
   [state]))

(defn browse {:export true} []
  (assetwatch/start assets/assets)
  (preview/browse! #'site/inject))

(defn start {:export true} []
  (assetwatch/stop)
  (preview/start-server! #'site/inject))

(defn stop {:export true} []
  (preview/stop-server!))

(comment
  (browse)
  (stop)
  (start)
  )
