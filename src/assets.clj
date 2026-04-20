(ns assets
  (:require
   [state]))

(def assets
  #{{:id :assets.style/layout
     :path "css/layout.css"
     :uri "/css/layout.css"}})

(defn by [f]
  (into {}
        (map (juxt f identity))
        assets))

(def by-uri (by :uri))
(def by-id (by :id))

(defn asset->dom-id
  [{:keys [id] :as _asset}]
  (str (namespace id) "." (name id)))

(defn sha1
  [{:keys [path] :as _asset}]
  (get-in @state/!asset-data [path :sha1]))

(defn link
  [{:keys [uri] :as asset}]
  [:link {:id (asset->dom-id asset)
          :href (str uri
                     (when-let [s (sha1 asset)]
                       (str "?sha1=" s)))
          :rel "stylesheet"}])

