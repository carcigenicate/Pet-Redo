(ns pet-redo.core-logic.game
  (:require [pet-redo.core-logic.pet :as p]
            [pet-redo.core-logic.settings :as s]
            [pet-redo.quiz-logic.std-question-templates :as std-t]
            [pet-redo.core-logic.time-helpers :as th]

            [helpers.general-helpers :as g]))

(defrecord Game [pet last-update settings])

(defn new-game-now [max-health max-satiety settings]
  (->Game (p/new-pet-now max-health max-satiety)
          (th/now)
          settings))

(defn simulate-tick [game]
  (let [{:keys [pain-per-tick starve-per-tick heal-per-tick]}
        (-> game :settings :sim-settings)]
    (update game :pet
            p/advance-by-tick pain-per-tick starve-per-tick heal-per-tick)))

(defn simulate-ticks [game n-ticks]
  (reduce (fn [acc-g _]
            (if (p/dead? (:pet acc-g))
              (reduced acc-g)
              (simulate-tick acc-g)))
          game
          (range n-ticks)))

(defn simulate-milliseconds [game ms]
  (let [ms-per-tick (-> game :settings :sim-settings :ms-per-tick)]
    (simulate-ticks game (int (/ ms ms-per-tick)))))

(defn update-game-by-time [game]
  (let [{:keys [last-update]} game
        now (th/now)]

    (-> game
      (simulate-milliseconds (th/ms-elapsed last-update now))
      (assoc :last-update now))))

(def test-settings
  (s/->Game-Settings (s/adjusted-sim-settings 75 80 60 500)
                     (s/->Question-Settings 5 10)))

(def test-game
  (new-game-now 100 100 test-settings))