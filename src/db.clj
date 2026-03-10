(ns db
  (:require
   [datomic.api :as d]))

(def schema
  [{:db/ident :meetup/date
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/identity}
   {:db/ident :meetup/title
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :meetup/description
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :github/id
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/identity}
   {:db/ident :person/name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :site/title
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :site/url
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/identity}
   {:db/ident :meetup/speakers
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many}
   {:db/ident :person/website
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one}])


(defn create-empty []
  (let [uri (str "datomic:mem://" (random-uuid))
        _ (d/create-database uri)
        conn (d/connect uri)]
    @(d/transact conn schema)
    (d/db conn)))

(defn find-meetups [db]
  (->> (d/q '[:find ?e
              :where [?e :meetup/date]]
            db)
       (map (fn [[eid]] (d/entity db eid)))))
