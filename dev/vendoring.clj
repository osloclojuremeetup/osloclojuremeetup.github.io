(ns vendoring
  (:require [templater]
            [clojure.string :as str]))

(defn javascript-entry? [[f _]]
  (str/ends-with? f ".js"))

(comment
  (def windborn-root "../../teodorlu/windborn.asset")

  (templater/folders->fileset windborn-root ["src" "test"])
  (templater/folder->fileset windborn-root "src")

  (def windborn-bundle
    (templater/folders->bundle windborn-root ["src" "test"]))

  (templater/write "." windborn-bundle)

  )
