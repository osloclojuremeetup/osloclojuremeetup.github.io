(ns site
  (:require
   [babashka.fs :as fs]
   [build]
   [clojure.edn :as edn]
   [datomic.api :as d]
   [db]
   [preview]
   [replicant.string]))

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

(defn pagify "Given file+render, return a seq of uri+render"
  [[file render-fn]]
  (if (= "index.html" file)
    [["/index.html" render-fn]
     ["/" render-fn]]
    [file render-fn]))

(def pages
  (->> files
       (mapcat pagify)
       (into {})))

(def db (create-db))

(defn inject [req]
  (-> req
      (assoc :site/pages pages)
      (assoc :site/db db)))

;; clojure -X site/build
(defn build
  ([] (build {}))
  ([_]
   (fs/create-dirs "build")
   (let [db (create-db)]
     (doseq [[file render] files]
       (spit (fs/file "build" file)
             (str "<!DOCTYPE html>\n"
                  (replicant.string/render (render db))))))))

(comment
  (set! *print-namespace-maps* false)

  (build)

  (preview/start-server! #'inject)
  (preview/browse! #'inject)

  ((requiring-resolve 'clojure.repl.deps/sync-deps))
  ((requiring-resolve 'clj-reload.core/reload) {:only :all})

  )
