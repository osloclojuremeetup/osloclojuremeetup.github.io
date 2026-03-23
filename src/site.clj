(ns site
  (:require
   [babashka.fs :as fs]
   [clojure.edn :as edn]
   [clojure.java.browse]
   [datomic.api :as d]
   [db]
   [preview]
   [replicant.string]
   [clojure.string :as str]))

(defn load-edn [path]
  (-> (fs/file path)
      slurp
      edn/read-string))

(defn load-meetups []
  (->> (fs/glob "meetups" "*.edn")
       (map load-edn)))

(defn load-speakers []
  (load-edn "speakers.edn"))

(defn render-meetup [meetup]
  (list
   [:div (:meetup/date meetup)]
   [:div
    [:div [:strong (:meetup/title meetup)]]
    [:div (:meetup/description meetup)]
    (for [talk (->> (:meetup/agenda meetup)
                    (sort-by :agenda/number))]
      [:div [:strong (:talk/title talk)]
       " ("
       (str/join ", " (keep :person/name (:talk/speakers talk)))
       ")"])]))

(defn render-frontpage [db]
  [:html
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
    [:link {:href "/css/layout.css" :rel "stylesheet"}]]
   [:body {:style {:max-width "1000px"}}
    [:h1 "Oslo Clojure Meetup"]
    [:p "Hei og velkommen til Oslo Clojure Meetup sin nettside!"]
    [:p "Her finner du oversikt over tidligere meetups med tema, talere, dato, og referanser 😊"]
    [:p "Meetups arrangeres gjennom "
     [:a {:href "https://www.meetup.com/clojure-oslo/"} "Clojure/Oslo på Meetup.com"]
     " og annonseres i kanalen #clojure-norway på "
     [:a {:href "http://clojurians.net/"} "Clojurians-slacken"]
     "."]

    [:h2 "Meetups 😊"]
    [:div#meetups
     (->> (db/find-meetups db)
          (map render-meetup))]]])

(defn create-db []
  (-> (db/create-empty)
      (d/with (concat (load-meetups)
                      (load-speakers)))
      :db-after))

(def html-files
  {"index.html" #'render-frontpage})

(defn pagify "Given file+render, return a seq of uri+render"
  [[file render-fn]]
  (if (= "index.html" file)
    [["/index.html" render-fn]
     ["/" render-fn]]
    [file render-fn]))

(def asset-paths
  #{"css/layout.css"})

(defn load-asset [asset-path]
  [(str "/" asset-path) (fs/file asset-path)])

(def uri->asset-file
  (into {} (map load-asset) asset-paths))

(def pages
  (->> html-files
       (mapcat pagify)
       (into {})))

(def db (create-db))

(defn inject [req]
  (-> req
      (assoc :site/pages pages)
      (assoc :site/assets uri->asset-file)
      (assoc :site/db db)))

;; clojure -X site/build
(defn build
  ([] (build {}))
  ([_]
   (when (fs/exists? "build")
     (fs/delete-tree "build"))
   (fs/create-dirs "build")
   (let [db (create-db)]
     (doseq [[file render] html-files]
       (spit (fs/file "build" file)
             (str "<!DOCTYPE html>\n"
                  (replicant.string/render (render db)))))
     (doseq [asset asset-paths]
       (let [target (fs/file "build" asset)]
         (fs/create-dirs (fs/parent target))
         (fs/copy asset target))))))


(comment
  (set! *print-namespace-maps* false)

  (build)

  (preview/start-server! #'inject)
  (preview/browse! #'inject)

  ((requiring-resolve 'clojure.repl.deps/sync-deps))
  ((requiring-resolve 'clj-reload.core/reload) {:only :all})

  (clojure.java.browse/browse-url "https://osloclojuremeetup.github.io")
  )
