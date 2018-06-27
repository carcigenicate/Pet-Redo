(ns pet-redo.store.item)

; TODO: Add name? Just use description?
(defrecord Item [description price effector-f]
  Object
  (toString [this] (str description ": " price "sat")))

(defn items
  "Accepts triplets of [description price effector-f], and returns a list of items."
  [& triplets]
  (mapv (partial apply ->Item) triplets))