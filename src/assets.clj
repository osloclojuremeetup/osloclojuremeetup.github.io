(ns assets
  (:require state))

(def assets
  #{{:id :assets.style/layout
     :path "css/layout.css"
     :uri "/css/layout.css"}})

(defn by [f]
  (into {}
        (map (juxt f identity))
        assets))

(def by-uri (by :uri))

(reset! state/!asset-definitions assets)


