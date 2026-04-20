(ns dev)

(defn reload []
  (require 'sse)
  (require 'frontpage)
  (sse/push-hiccup! [:div#morph (frontpage/render-body site/db)]))
