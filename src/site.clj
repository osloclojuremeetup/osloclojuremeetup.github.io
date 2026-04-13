(ns site
  (:require
   [assets]
   [babashka.fs :as fs]
   [clojure.edn :as edn]
   [clojure.java.browse]
   [datomic.api :as d]
   [db]
   [frontpage]
   [preview]
   [replicant.string]))

(defn load-edn [path]
  (-> (fs/file path)
      slurp
      edn/read-string))

(defn load-meetups []
  (->> (fs/glob "meetups" "*.edn")
       (map load-edn)))

(defn load-speakers []
  (load-edn "speakers.edn"))

(defn create-db []
  (-> (db/create-empty)
      (d/with (concat (load-meetups)
                      (load-speakers)))
      :db-after))

(def html-files
  {"index.html" {:render #'frontpage/render-static
                 :render-dev #'frontpage/render-dev}})

(defn pagify "Given file+render, return a seq of uri+render"
  [[file page]]
  (if (= "index.html" file)
    [["/index.html" page]
     ["/" page]]
    [file page]))

(def pages
  (->> html-files
       (mapcat pagify)
       (into {})))

(def db (create-db))

(defn inject [req]
  (-> req
      (assoc :site/pages pages)
      (assoc :site/uri->asset assets/by-uri)
      (assoc :site/db db)))

;; clojure -X site/build
(defn build
  ([] (build {}))
  ([_]
   (when (fs/exists? "build")
     (fs/delete-tree "build"))
   (fs/create-dirs "build")
   (let [db (create-db)]
     (doseq [[file {:keys [render]}] html-files]
       (spit (fs/file "build" file)
             (str "<!DOCTYPE html>\n"
                  (replicant.string/render (render db)))))
     (doseq [{:keys [path]} assets]
       (let [target (fs/file "build" path)]
         (fs/create-dirs (fs/parent target))
         (fs/copy path target))))))


(comment
  (set! *print-namespace-maps* false)

  (fs/delete-tree "build")
  (build)
  (fs/glob "build" "**/*")

  

  ((requiring-resolve 'clojure.repl.deps/sync-deps))
  ((requiring-resolve 'clj-reload.core/reload) {:only :all})

  (clojure.java.browse/browse-url "https://osloclojuremeetup.github.io")
  )
