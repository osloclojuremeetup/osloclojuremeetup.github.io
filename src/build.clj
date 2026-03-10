(ns build
  (:require
   [babashka.fs :as fs]
   [clojure.edn :as edn]
   [datomic.api :as d]
   [replicant.string]
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

;; clojure -X build/generate-site
(defn generate-site
  ([] (generate-site {}))
  ([_]
   (fs/create-dirs "build")
   (spit "build/index.html"
         (str "<!DOCTYPE html>\n"
              (replicant.string/render
               (render-frontpage (create-db)))))))

(comment

  (generate-site)

  ((requiring-resolve 'clojure.repl.deps/sync-deps))
  (set! *print-namespace-maps* false)

  ((requiring-resolve 'clj-reload.core/reload) {:only :all})

  )
