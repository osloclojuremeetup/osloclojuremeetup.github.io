(ns components)

(defn find-speaker-website [speaker]
  (or (-> speaker :person/website :site/url)
      (when (:github/id speaker)
        (str "https://github.com/" (:github/id speaker)))))

(defn speaker [speaker]
  (when (:person/name speaker)
    (or (when-let [website (find-speaker-website speaker)]
          [:a {:href website} (:person/name speaker)])

        (:person/name speaker))))

(comment
  (set! *print-namespace-maps* false)
  (require '[datomic.api :as d])
  (def magnar (d/entity @state/!db [:github/id "magnars"]))
  (speaker magnar)

  (-> magnar :person/website :site/url)
  )
