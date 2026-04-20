(ns assets-test
  (:require
   [assets]
   [state]
   [clojure.test :refer [deftest is testing]]
   [matcher-combinators.test :refer [match?]]
   [windborn.asset]))

;; Load layout.css into memory
(windborn.asset/watch-handler state/!asset-data
                              {:type :modify
                               :path (:path (assets/by-id :assets.style/layout))})

(deftest asset->dom-id
  (is (= "assets.style.layout"
         (assets/asset->dom-id {:id :assets.style/layout}))))

(deftest link
  (testing "known assets are hashed"
    (is (match? [:link {:id "assets.style.layout"
                        :href "/css/layout.css?sha1=epgLnn7FajrqcWc8lkNEJYL5afc"
                        :rel "stylesheet"}]
                (assets/link (assets/by-id :assets.style/layout)))))

  (testing "unknown assets don't crash"
    (is (match? [:link {:id "assets.style.does-not-exist"
                        :href "/css/bogus.css"
                        :rel "stylesheet"}]
                (assets/link {:id :assets.style/does-not-exist
                              :uri "/css/bogus.css"})))))
