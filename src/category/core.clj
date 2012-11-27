(ns category.core
  (:use [datomic.api :only [q db] :as d]
        [clojure.pprint :only [pprint]]))

(def uri "datomic:mem://category")

(d/create-database uri)

(def conn (d/connect uri))

(deref (d/transact conn
            [{:db/id (d/tempid :db.part/db)
              :db/ident :category/name
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db.install/_attribute :db.part/db}
             {:db/id (d/tempid :db.part/db)
              :db/ident :category/parentId
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/one
              :db.install/_attribute :db.part/db}]))

(let [id (d/tempid :db.part/user)]
    (deref (d/transact conn
                       [{:db/id id
                         :category/name "Jobs"
                         }
                        {:db/id (d/tempid :db.part/user)
                         :category/name "Part-Time"
                         :category/parentId id
                         }
                        {:db/id (d/tempid :db.part/user)
                         :category/name "Full-Time"
                         :category/parentId id
                         }])))


(pprint (q '[:find ?parent ?name :where
             [?parent :category/name "Jobs"]
             [?child :category/name ?name]
             [?child :category/parentId ?parent]]
           (db conn)))
