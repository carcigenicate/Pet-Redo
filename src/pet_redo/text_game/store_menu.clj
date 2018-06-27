(ns pet-redo.text-game.store-menu
  (:require [pet-redo.store.items :as is]
            [pet-redo.text-game.menu-helpers :as mh]

            [helpers.general-helpers :as g]
            [pet-redo.store.item :as i]))

(def money-path [:pet :satiety :n])


(defn money [game]
  (get-in game money-path))

(defn unsafe-charge-game [game amount]
  (update-in game money-path - amount))

(defn enough-money? [game amount]
  (>= (money game)
      amount))


(defn main-store-menu [game]
  (let [exit {:description "Exit", :effector-f (constantly nil)}
        menu (conj is/store-items exit)]
    (loop [acc-game game]
      (println (str "Money: " (int (money acc-game)) "sat"))
      (println (mh/format-menu menu
                  #(str % ": " (if (= %2 exit) (:description exit) %2))))

      (let [{:keys [price effector-f]} (mh/ask-for-menu-option menu)
            effected-game? (effector-f acc-game)]
        (cond
          (not effected-game?)
          acc-game

          (not (enough-money? acc-game price))
          (do
            (println "\n>>>>> Not enough money! <<<<<\n")
            (recur acc-game))

          :else
          (recur (unsafe-charge-game effected-game? price)))))))
