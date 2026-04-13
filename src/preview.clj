(ns preview
  (:require
   [clojure.java.browse]
   [org.httpkit.server :as httpkit]
   [replicant.string]
   [ring.middleware.content-type]
   [sse]
   [babashka.fs :as fs]))

(defn handler [req]
  (let [uri (:uri req)
        pages (:site/pages req)
        db (:site/db req)]
    (cond (and (= (:request-method req) :get)
               (contains? pages (:uri req)))
          {:status 200
           :headers {"Content-Type" "text/html"}
           :body (str "<!DOCTYPE html>\n"
                      (replicant.string/render ((get-in pages [(:uri req) :render-dev]) db)))}

          (and (= (:request-method req) :get)
               (= "/sse" (:uri req)))
          (sse/handler req)

          (and (= (:request-method req) :get)
               (contains? (:site/uri->asset req) uri))
          (ring.middleware.content-type/content-type-response
           {:status 200
            :body (fs/file (get-in req [:site/uri->asset uri :path]))}
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
