(ns assets-test
  (:require
   [assets]
   [state]
   [clojure.test :refer [deftest is testing]]
   [matcher-combinators.test :refer [match?]]
   [matcher-combinators.matchers :as m]
   [windborn.asset]
   [clojure.string :as str]))

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
                        :href (m/pred #(str/starts-with? % "/css/layout.css?sha1="))
                        :rel "stylesheet"}]
                (assets/asset->link (assets/by-id :assets.style/layout)))))

  (testing "unknown assets don't crash"
    (is (match? [:link {:id "assets.style.does-not-exist"
                        :href "/css/bogus.css"
                        :rel "stylesheet"}]
                (assets/asset->link {:id :assets.style/does-not-exist
                                     :uri "/css/bogus.css"})))))
