(ns sse
  (:require
   [frontpage]
   [replicant.string]
   [starfederation.datastar.clojure.adapter.http-kit
    :refer [->sse-response on-close on-open]]
   [starfederation.datastar.clojure.api :as d*]
   [state]
   [windborn.asset]))

(defn session-open [id {:keys [sse]}]
  (swap! state/!sessions assoc id {:sse sse}))

(defn session-close [id]
  (swap! state/!sessions dissoc id))

(defn handler [req]
  (let [id (gensym)]
    (->sse-response req
                    {on-open (fn [sse] (session-open id {:sse sse}))
                     on-close (fn [_ _] (session-close id))})))

(defn push-all! [html-str]
  (doseq [{:keys [sse]} (vals @state/!sessions)]
    (d*/patch-elements! sse html-str)))

(defn push-hiccup! [hiccup]
  (push-all! (str (replicant.string/render hiccup))))

(defn push-assets! []
  (push-hiccup! (frontpage/asset-headers)))

(comment
  (push-hiccup! [:h1#morph "🥳🎉🤩"])

  :-)
