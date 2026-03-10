(ns build-test
  (:require [build :as build]
            [clojure.test :refer [deftest is]]))

(defonce db (build/create-db))

(deftest render-frontpage
  (is (contains? (->> (build/render-frontpage db)
                      (tree-seq coll? identity)
                      set)
                 "Oslo Socially Functional Reboot: Functional Core / Imperative Shell")))
