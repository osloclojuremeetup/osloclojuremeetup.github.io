(ns preview
  (:require
   [clojure.java.browse]
   [org.httpkit.server :as httpkit]
   [replicant.string]
   [ring.middleware.content-type]))

(defn handler [req]
  (let [pages (:site/pages req)
        assets (:site/assets req)
        db (:site/db req)]
    (cond (and (= (:request-method req) :get)
               (contains? pages (:uri req)))
          {:status 200
           :headers {"Content-Type" "text/html"}
           :body (str "<!DOCTYPE html>\n"
                      (replicant.string/render ((get pages (:uri req)) db)))}

          (and (= (:request-method req) :get)
               (contains? assets (:uri req)))
          (ring.middleware.content-type/content-type-response
           {:status 200
            :body (get assets (:uri req))}
           req)

          :else
          {:status 404})))

(defonce !server (atom nil))
(defonce !conns (atom #{}))

(defn stop-server! [] (swap! !server #(do (when % (httpkit/server-stop! %)) nil)))

(defn start-server! [inject]
  (stop-server!)
  (reset! !server (httpkit/run-server (comp #'handler inject)
                                      {:legacy-return-value? false
                                       :port 7799})))

(defn browse! [inject]
  (when-not @!server (start-server! inject))
  (clojure.java.browse/browse-url "http://localhost:7799"))
