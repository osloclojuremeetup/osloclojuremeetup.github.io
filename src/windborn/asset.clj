(ns windborn.asset
  (:refer-clojure :exclude [load])
  (:require
   [babashka.fs :as fs]
   [nextjournal.beholder :as beholder]
   [ring.util.mime-type])
  (:import
   (java.util Base64$Encoder)
   (java.security MessageDigest)))

(set! *warn-on-reflection* true)

;; Asset logic
(def mime-types
  (-> (into (sorted-map) ring.util.mime-type/default-mime-types)
      (assoc "js" "text/javascript; charset=utf-8")
      (assoc "mjs" "text/javascript; charset=utf-8")
      (assoc "map" "application/json; charset=utf-")))

(defn file->type [f]
  (get mime-types (fs/extension f) "application/octet-stream"))

(def base64-encoder (.withoutPadding (java.util.Base64/getUrlEncoder)))

(defn sha1-str [^bytes bytes]
  (Base64$Encoder/.encodeToString base64-encoder
                                  (.digest
                                   (doto (MessageDigest/getInstance "SHA-1")
                                     (MessageDigest/.update bytes)))))

(defn load [path]
  (let [^bytes bytes (fs/read-all-bytes path)]
    {:status 200
     :headers {"Content-Type" (file->type path)
               "Content-Length" (alength bytes)}
     :body bytes
     :sha1 (sha1-str bytes)
     :last-modified (fs/last-modified-time path)}))

(defn initialize [definitions]
  (into {}
        (map (fn [{:keys [path]}]
               [path (load path)]))
        definitions))

;; Watch logic
(defn watch-handler [!data {:keys [type path]}]
  (when (= type :modify)
    (swap! !data assoc (str (fs/relativize (fs/cwd) (fs/absolutize path)))
           (load path))))

(defn create-file-watcher [definitions !data]
  (apply beholder/watch
         (partial #'watch-handler !data)
         (map :path definitions)))

(defn update-file-watcher [!watcher definitions !data]
  (swap!
   !watcher
   (fn [previous]
     (some-> previous beholder/stop)
     (create-file-watcher definitions !data))))

(defn watch [definitions-var !watcher !data push-assets]
  (add-watch definitions-var `watch
             (fn [_ _ _ definitions]
               (update-file-watcher !watcher definitions !data)
               (doseq [{:keys [path]} definitions]
                 (watch-handler !data {:type :modify
                                       :path path}))))
  (update-file-watcher !watcher (deref definitions-var) !data)
  (reset! !data (initialize @definitions-var))
  (add-watch !data `watch
             (fn [_ _ _ _]
               (push-assets)))
  (push-assets)
  :watching)

(defn unwatch [definitions-var !watcher !data]
  (remove-watch definitions-var `watch)
  (remove-watch !data `watch)
  (some-> @!watcher beholder/stop))

(comment

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Feel like taking it for a spin in a REPL?
  ;;
  ;; First, state setup (you likely want this in your own namespace),
  (do (defonce asset-definitions nil)
      (defonce !asset-watcher (atom nil))
      (defonce !asset-data (atom {}))
      (defn push-assets! []
        (prn "should push" @!asset-data "by now.")))
  ;;
  ;; Kick off watcher,
  (watch #'asset-definitions !asset-watcher !asset-data #'push-assets!)
  ;;
  ;; Change asset definitions,
  (alter-var-root #'asset-definitions
                  (constantly [{:id ::deps.edn
                                :uri "/deps.edn"
                                :path "deps.edn"}]))
  ;; ... and check your stdout.
  ;;
  ;; Finally, unwatch.
  (unwatch #'asset-definitions !asset-watcher !asset-data)
  ;;
  ;; Are your sensibilities tickled by the mix of vars and atoms, and deref-ing
  ;; around the place? Then we are two.
  ;;
  ;; I welcome you to take it for a spin, see what it feels like, and share
  ;; friction, problems, limitations and joy.
  ;;
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

  )
