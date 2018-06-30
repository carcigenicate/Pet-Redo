(ns pet-redo.store.helpers)

(def money-path [:pet :satiety :n])

(defn money-of [game]
  (get-in game money-path))

(defn unsafe-charge-game [game amount]
  (update-in game money-path - amount))

(defn enough-money? [game amount]
  (>= (money-of game)
      amount))

(defn effect-game-with [game item]
  ((:effector-f item) game))