(ns frontpage
  (:require
   [assets]
   [clojure.string :as str]
   [db]
   [starfederation.datastar.clojure.api :as d*]))

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

(defn headers []
  (list
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]))

(defn asset-headers []
  (assets/link-all :assets.style/layout
                   :assets.style/props
                   :assets.style/color
                   :assets.style/type))

(defn dev-headers []
  (list
   [:script {:type "module" :src d*/CDN-url}]))

(defn render-body [db]
  (list
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
         (map render-meetup))]))

(defn render-static [db]
  [:html
   [:head
    (headers)
    (asset-headers)]
   [:body {:style {:max-width "1000px"}}
    (render-body db)]])

(defn render-dev [db]
  [:html
   [:head
    (list (headers)
          (asset-headers)
          (dev-headers))]
   [:body {:style {:max-width "1000px"}}
    [:span {:data-init "@get('/sse')" :style {:display "none"}}]
    [:div#morph
     (render-body db)]]])
