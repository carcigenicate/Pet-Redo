(ns pet-redo.core-logic.pet
  (:require [pet-redo.core-logic.bounded-resource :as br]
            [pet-redo.core-logic.time-helpers :as th]))

(defrecord Pet [health satiety birth-date]
  Object
  (toString [this] (str "<[HP: " health " - Sat: " satiety "]>")))

(defn new-pet-now [max-health max-satiety]
  (->Pet (br/new-maxed-resource max-health)
         (br/new-maxed-resource max-satiety)
         (th/now)))

(defn heal [pet by]
  (update pet :health
          br/update-resource #(+ % by)))

(defn hurt [pet by]
  (heal pet (- by)))

(defn feed [pet by]
  (update pet :satiety
          br/update-resource #(+ % by)))

(defn starve [pet by]
  (feed pet (- by)))

(defn starving? [pet]
  (-> pet :satiety :n (zero?)))

(defn dead? [pet]
  (-> pet :health :n (zero?)))

(defn days-old [pet]
  (-> (th/ms-elapsed (:birth-date pet) (th/now))
      (/ 1000 60 60 24)
      (double)))

(defn advance-by-tick [pet pain-per-tick starve-per-tick heal-per-tick]
  (cond
    (starving? pet) (hurt pet pain-per-tick)
    (dead? pet) pet
    :else (-> pet
            (starve starve-per-tick)
            (heal heal-per-tick))))

