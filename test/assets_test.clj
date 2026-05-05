(ns assets-test
  (:require
   [assets]
   [state]
   [clojure.test :refer [deftest is]]
   [matcher-combinators.test :refer [match?]]
   [matcher-combinators.matchers :as m]
   [windborn.asset]))

(deftest asset->dom-id
  (is (= "assets.style.layout"
         (assets/asset->dom-id {:id :assets.style/layout}))))

(deftest load-one
  (is (match? [:style
               {:id "assets.style.layout"
                :innerHTML (m/pred string?)}]
              (assets/load-one (assets/by-id :assets.style/layout)))))
