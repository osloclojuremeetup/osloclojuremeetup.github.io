(ns dev)

(defn reload []
  (require 'sse)
  (require 'frontpage)
  (require 'site)
  (sse/push-hiccup! [:div#morph (frontpage/render-body site/db)]))
