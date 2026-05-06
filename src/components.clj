(ns components)

(defn speaker [speaker]
  (cond (and (:person/name speaker)
             (:github/id speaker))
        [:a {:href (str "https://github.com/" (:github/id speaker))}
         (:person/name speaker)]

        (:person/name speaker)
        (:person/name speaker)))
