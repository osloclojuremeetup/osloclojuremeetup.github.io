(ns site-test
  (:require
   [clojure.test :refer [deftest is]]
   [site :as site]))

(deftest render-frontpage
  (is (contains? (->> (site/create-db)
                      site/render-frontpage
                      (tree-seq coll? identity)
                      set)
                 "Oslo Socially Functional Reboot: Functional Core / Imperative Shell")))
