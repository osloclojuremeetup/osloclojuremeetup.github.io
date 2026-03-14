(ns site-test
  (:require [site :as site]
            [clojure.test :refer [deftest is]]))

(defonce db (site/create-db))

(deftest render-frontpage
  (is (contains? (->> (site/render-frontpage db)
                      (tree-seq coll? identity)
                      set)
                 "Oslo Socially Functional Reboot: Functional Core / Imperative Shell")))
