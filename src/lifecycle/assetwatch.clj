(ns lifecycle.assetwatch
  (:require
   [sse]
   [state]
   [nextjournal.beholder :as beholder]
   [babashka.fs :as fs]))

(defonce !event (atom nil))

(defn start [asset-definitions]
  (swap! state/!asset-watcher
         (fn [old]
           (some-> old beholder/stop)
           (apply beholder/watch
                  (fn [event]
                    (let [path-str (str (fs/relativize
                                         (fs/absolutize ".")
                                         (fs/file (:path event))))]
                      ((resolve `sse/push-asset!) path-str)))
                  (map :path asset-definitions)))))

(defn stop []
  (swap! state/!asset-watcher #(some-> % beholder/stop)))

(comment
  (start assets/assets)
  (stop)

  @!event

  (def p (:path @!event))

  (fs/relativize (fs/absolutize ".") (fs/file p))
  ;; => #path "css/layout.css"

  )
