(ns assets
  (:require
   [state]))

(def assets
  #{{:id :assets.style/layout
     :path "css/layout.css"
     :uri "/css/layout.css"}
    {:id :assets.style/props
     :path "css/props.css"
     :uri "/css/props.css"}
    {:id :assets.style/color
     :path "css/color.css"
     :uri "/css/color.css"}
    {:id :assets.style/type
     :path "css/type.css"
     :uri "/css/type.css"}})

(reset! state/!asset-definitions assets)

(defn by [f]
  (into {}
        (map (juxt f identity))
        assets))

(def by-uri (by :uri))
(def by-id (by :id))

(defn asset->dom-id
  [{:keys [id] :as _asset}]
  (str (namespace id) "." (name id)))

(defn asset->sha1
  [{:keys [path] :as _asset}]
  (get-in @state/!asset-data [path :sha1]))

(defn asset->link
  [{:keys [uri] :as asset}]
  [:link {:id (asset->dom-id asset)
          :href (str uri
                     (when-let [s (asset->sha1 asset)]
                       (str "?sha1=" s)))
          :rel "stylesheet"}])

(defn link-all [asset-id & more-ids]
  (cons (asset->link (by-id asset-id))
        (map (comp asset->link by-id) more-ids)))

(defn load-one [asset]
  (condp = (namespace (:id asset))
    "assets.style"
    [:style {:id (asset->dom-id asset)}
     (slurp (:path asset))]
    nil))

(defn load-all [asset-id & more-ids]
  (cons (load-one (by-id asset-id))
        (map (comp load-one by-id) more-ids)))
