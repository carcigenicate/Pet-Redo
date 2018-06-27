(ns pet-redo.core-logic.helpers)

(defn clamp [n min-n max-n]
  (-> n
      (min max-n)
      (max min-n)))