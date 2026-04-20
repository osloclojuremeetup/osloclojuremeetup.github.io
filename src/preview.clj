(ns preview
  (:require
   [babashka.fs :as fs]
   [clojure.java.browse]
   [org.httpkit.server :as httpkit]
   [replicant.string]
   [ring.middleware.content-type]
   [sse]
   [state]))

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
          (get @state/!asset-data (get-in (:site/uri->asset req) [uri :path]))

          :else
          {:status 404})))

(defn stop-server! [] (swap! state/!server #(do (when % (httpkit/server-stop! %)) nil)))

(defn start-server! [inject]
  (stop-server!)
  (reset! state/!server (httpkit/run-server (comp #'handler inject)
                                      {:legacy-return-value? false
                                       :port 7799})))

(defn browse! [inject]
  (when-not @state/!server (start-server! inject))
  (clojure.java.browse/browse-url "http://localhost:7799"))
