(ns pet-redo.store.items
  (:require [pet-redo.store.item :as i]))

(defn inc-max-stat [game stat-key amount]
  (update-in game [:pet stat-key :max] + amount))

(def store-items
  (i/items
    ["Increase Satiety" 90 #(inc-max-stat % :satiety 5)]

    ["Increase Health" 90 #(inc-max-stat % :health 5)]

    ["Decrease Wrong Answer Penalty" 100
     #(update-in % [:settings :q-settings :pain-per-wrong] * 0.8)]))