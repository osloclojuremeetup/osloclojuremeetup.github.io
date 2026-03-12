(ns build
  (:require
   [babashka.fs :as fs]
   [org.httpkit.server :as httpkit]
   [clojure.edn :as edn]
   [datomic.api :as d]
   [replicant.string]
   [clojure.java.browse]
   [db :as db]))

(defn load-meetups []
  (->> (fs/glob "meetups" "*.edn")
       (map fs/file)
       (map slurp)
       (map edn/read-string)))

(defn render-frontpage [db]
  [:html
   [:body
    (->> (db/find-meetups db)
         (map (fn [meetup]
                [:div (:meetup/title meetup)])))]])

(defn create-db []
  (-> (db/create-empty)
      (d/with (load-meetups))
      :db-after))

(def files
  {"index.html" #'render-frontpage})

;; clojure -X build/generate-site
(defn generate-site
  ([] (generate-site {}))
  ([_]
   (fs/create-dirs "build")
   (let [db (create-db)]
     (doseq [[file render] files]
       (spit (fs/file "build" file)
             (str "<!DOCTYPE html>\n"
                  (replicant.string/render (render db))))))))

(defonce !server (atom nil))
(defonce !conns (atom #{}))

(defn pagify "Given file+render, return a seq of uri+render"
  [[file render-fn]]
  (if (= "index.html" file)
    [["/index.html" render-fn]
     ["/" render-fn]]
    [file render-fn]))

(def pages (->> files (mapcat pagify) (into {})))

(def db (create-db))

(defn handler [req]
  (cond (and (= (:request-method req) :get)
             (contains? pages (:uri req)))
        {:status 200
         :headers {"Content-Type" "text/html"}
         :body (str "<!DOCTYPE html>\n"
                    (replicant.string/render ((get pages (:uri req)) db)))}

        :else
        {:status 404}))

(defn stop-server! [] (swap! !server #(do (when % (httpkit/server-stop! %)) nil)))

(defn start-server! []
  (stop-server!)
  (reset! !server (httpkit/run-server #'handler {:legacy-return-value? false
                                                 :port 7799})))

(defn browse! []
  (when-not @!server (start-server!))
  (clojure.java.browse/browse-url "http://localhost:7799"))

(comment

  (generate-site)
  (browse!)

  ((requiring-resolve 'clojure.repl.deps/sync-deps))
  (set! *print-namespace-maps* false)
  ((requiring-resolve 'clj-reload.core/reload) {:only :all})


  )
