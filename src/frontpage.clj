(ns frontpage
  (:require
   [assets]
   [db]
   [sse]
   [starfederation.datastar.clojure.api :as d*]
   [state]
   [components]))

(defn render-meetups [meetups]
  (->> meetups
       (map (fn [meetup]
              (list
               [:div.date [:span (:meetup/date meetup)]]
               [:div
                [:div.meetup-title [:strong (:meetup/title meetup)]]
                ;; [:div (:meetup/description meetup)]
                (for [talk (->> (:meetup/agenda meetup)
                                (sort-by :agenda/number))]
                  [:div.talk
                   [:div [:span (:talk/title talk)]
                    [:span.dot " • "]
                    [:span.speaker (interpose ", " (keep components/speaker (:talk/speakers talk)))]]
                   (for [ref (:talk/references talk)]
                     [:div.reference
                      [:a {:href (:reference/url ref)}
                       (:reference/label ref)]])])])))))

(defn render-lunches [lunches]
  (list
   [:div.date (->> (map :lunch/date lunches)
                   (interpose [:br]))]
   [:div
    [:div.lunch-title [:strong (if (= 1 (count lunches))
                                 "Lunch"
                                 "Lunches")]]]))

(def event-renderers
  {:event.type/meetup #'render-meetups
   :event.type/lunch #'render-lunches})

(defn render-events [event-type events]
  (or (when-let [render (get event-renderers event-type)]
        (render events))
      (throw (ex-info "Unknown event type"
                      {:event/type event-type}))))

(defn headers []
  (list
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
   [:link {:rel "preconnect" :href "https://fonts.googleapis.com"}]
   [:link {:rel "preconnect" :href "https://fonts.gstatic.com" :crossorigin ""}]
   [:link {:rel "stylesheet" :href "https://fonts.googleapis.com/css2?family=Roboto&family=Roboto+Mono&display=swap"}]))

(defn asset-headers []
  (assets/load-all :assets.style/layout
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

   [:h2 "Events"]
   [:div#meetups
    (->> (db/find-events db)
         (partition-by :event/type)
         (map (fn [events] (render-events (:event/type (first events))
                                          events))))]))

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
          [:link {:id "autocss" :href "/autocss" :rel "stylesheet"}]
          (dev-headers))]
   [:body {:style {:max-width "1000px"}}
    [:span {:data-init "@get('/sse')" :style {:display "none"}}]
    [:div#morph
     (render-body db)]]])

(when-let [db @state/!db]
  (sse/push-hiccup! [:div#morph (render-body db)]))
