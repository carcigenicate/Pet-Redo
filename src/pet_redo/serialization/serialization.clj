(ns pet-redo.serialization.serialization
  (:require [clojure.edn :as edn]
            [pet-redo.core-logic.time-helpers :as th]
            [pet-redo.core-logic.settings :as s]
            [pet-redo.core-logic.game :as pg]
            [pet-redo.core-logic.pet :as p]

            [clojure.java.io :as io]
            [pet-redo.core-logic.bounded-resource :as br])

  (:import (java.io FileNotFoundException)))

(def ^:dynamic *save-path* "./saves/")
(def default-save-name "pet.pet")

(defn save-path []
  (str *save-path* default-save-name))

(defn- replace-map [nested-map transformation-map]
  ((fn rec [map']
     (->> map'
          (reduce-kv (fn [acc-m k v]
                       (assoc! acc-m k
                               (if-let [replace-f (transformation-map k)]
                                 (replace-f v)
                                 (if (map? v)
                                   (rec v)
                                   v))))
                     (transient {}))
          (persistent!)))
   nested-map))

(defn nested-map->Pet [map-pet]
  (-> map-pet
      (update :health br/map->Bounded-Resource)
      (update :satiety br/map->Bounded-Resource)
      (p/map->Pet)))

(defn nested-settings->Game-Settings [map-settings]
  (-> map-settings
      (update :sim-settings s/map->Simulation-Settings)
      (update :q-settings s/map->Question-Settings)
      (s/map->Game-Settings)))

(defn parse-save [save-str]
  (let [{:keys [pet last-update settings]}
        (-> save-str
            (edn/read-string)
            (replace-map {:last-update th/parse-date
                          :birth-date th/parse-date}))]

    (pg/->Game
      (nested-map->Pet pet)
      last-update
      (nested-settings->Game-Settings settings))))

(defn format-save [game]
  (-> game
      (replace-map {:last-update th/format-date
                    :birth-date th/format-date})
      (pr-str)))

(defn write-save [game]
  (let [path (save-path)]
    (io/make-parents path)

    (->> game
         (format-save)
         (spit path)))

  game)

(defn read-save? []
  (try
    (->> (save-path)
         (slurp)
         (parse-save))

    (catch FileNotFoundException _
      nil)))
#_
(defn write-save-background []
  ())