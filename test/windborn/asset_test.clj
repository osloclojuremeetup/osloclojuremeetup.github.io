(ns windborn.asset-test
  (:require [windborn.asset :as asset]
            [clojure.test :refer [deftest is]]
            [matcher-combinators.test :refer [match?]]
            [matcher-combinators.matchers :as m]
            [clojure.java.io :as io]
            [babashka.fs :as fs]))

(def datastar-path (str (io/file (io/resource "windborn/asset/datastar-v1.0.0-RC.8.js"))))

(deftest load-asset
  (is (match? {:status 200
               :headers {"Content-Type" "text/javascript; charset=utf-8"
                         "Content-Length" (m/pred (partial < 0))}
               :body (m/pred (comp #{byte/1} type))}
              (asset/load datastar-path))))

(comment
  ;; Are we hashing quickly?
  (time (asset/load datastar-path))
  ;; 0.4 ms

  (def datastar-asset (asset/load datastar-path))
  (time (asset/sha1-str (:body datastar-asset)))
  ;; 0.15 ms

  (time (do (fs/read-all-bytes datastar-path)
            :done))

  :-)
