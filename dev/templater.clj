(ns templater
  (:require
   [babashka.fs :as fs]
   [clojure.string :as str]
   [clojure.walk :as walk]
   [clojure.set :as set]))

(defn guard [pred f]
  #(cond-> % (pred %) f))

;; -----------------------------------------------------------------------------
;;           TRANSFORM BUNDLE
;; -----------------------------------------------------------------------------

(defn interpolate-data [bundle formatters]
  (walk/postwalk (guard #(and (vector? %)
                              (contains? formatters (first %)))
                        (fn [[k & args]]
                          (apply (formatters k) args)))
                 bundle))

(defn interpolate-strings [bundle replacements]
  (walk/postwalk (guard string?
                        (fn [s]
                          (reduce (fn [s [match replacement]]
                                    (str/replace s match replacement))
                                  s
                                  replacements)))
                 bundle))

(defn transform [bundle xf]
  (into (sorted-map) xf bundle))

;; -----------------------------------------------------------------------------
;;           FROM DISK
;; -----------------------------------------------------------------------------

(defn folder->bundle [root folder]
  (into (sorted-map)
        (comp (remove fs/directory?)
              (filter fs/exists?)
              (map (juxt #(str (fs/relativize root %))
                         slurp)))
        (file-seq (fs/file root folder))))

(defn folders->bundle [root folders]
  (transduce (map (partial folder->bundle root))
             merge
             (sorted-map)
             folders))
#_(folders->bundle "." ["src" "test"])

(defn folder->fileset [root folder]
  (into (sorted-set)
        (comp (remove fs/directory?)
              (filter fs/exists?)
              (map (partial fs/relativize root))
              (map str))
        (file-seq (fs/file root folder))))
#_(folder->fileset "." "test")

(defn folders->fileset [root folders]
  (transduce (map (partial folder->fileset root))
             set/union
             (sorted-map)
             folders))
#_(folders->fileset "." ["src" "test"])

;; -----------------------------------------------------------------------------
;;           TO DISK
;; -----------------------------------------------------------------------------

(defn upsert-file [file-path content]
  (fs/create-dirs (fs/parent file-path))
  (if (fs/exists? file-path)
    (if (= content (slurp file-path))
      [:unchanged (str file-path)]
      (do (spit file-path content)
          [:updated (str file-path)]))
    (do (spit (str file-path) content)
        [:created (str file-path)])))

(defn write [root bundle]
  (when-not (fs/directory? root)
    (throw (ex-info "Cannot write bundle when root does not exist"
                    {:root root})))
  (doall (map (fn [[path content]]
                (upsert-file (fs/file root path) content))
              bundle)))

(defn empty-directory? [f]
  (and (fs/directory? f)
       (empty? (fs/list-dir f))))

(defn purge-empty-upwards
  "Delete dir if empty, and all empty parents of f"
  [dir]
  (let [log (atom [])]
    (loop [current-dir dir]
      (when (empty-directory? current-dir)
        (fs/delete current-dir)
        (swap! log conj [:deleted-directory (str current-dir)])
        (recur (fs/parent current-dir))))
    @log))

(defn delete-file [f]
  (let [did-delete? (fs/delete-if-exists f)]
    [(if did-delete? :deleted :already-deleted) (str f)]))
