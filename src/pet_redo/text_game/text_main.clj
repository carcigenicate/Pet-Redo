(ns pet-redo.text-game.text-main
  (:require [pet-redo.core-logic.game :as pg]
            [pet-redo.serialization.serialization :as serial]
            [pet-redo.text-game.question-loop :as ql]
            [pet-redo.quiz-logic.std-question-templates :as std-t]
            [pet-redo.text-game.menu-helpers :as mh]
            [pet-redo.core-logic.settings :as s]
            [pet-redo.core-logic.pet :as p]
            [pet-redo.text-game.store-menu :as sm]

            [clojure.string :as str]

            [helpers.general-helpers :as g])

  (:gen-class))

(def global-rand-gen (g/new-rand-gen))
(def default-question-template std-t/std-templates)

(def default-new-settings
  (s/->Game-Settings (s/adjusted-sim-settings 75 80 60 500)
                     (s/->Question-Settings 5 5)))

(def default-new-game
  (pg/new-game-now 100 100 default-new-settings))

(defn answer-questions [game]
  (-> game
    (ql/question-loop default-question-template global-rand-gen)))

(defn store [game]
  (sm/main-store-menu game))

(defn exit [game]
  (serial/write-save game)
  nil)

; TODO: Change to a vector and index instead? Use (get menu) to prevent oob.
(def main-menu-options
  [["Answer Questions" answer-questions]
   ["Store" store]
   ["Exit" exit]])

(defn print-fl [& strings]
  (apply print strings)
  (flush))

(defn handle-death [game]
  (let [pet (:pet game)]
    (println "\n\nYour pet died :(")
    (println (str pet))
    (println "They were" (p/days-old (:pet game)) "days old.\n\n"))

  (doto default-new-game
        (serial/write-save)))

(defn main-loop [initial-game]
  (println (str "Days old: " (format "%.2f" (p/days-old (:pet initial-game)))))

  (loop [acc-game (pg/update-game-by-time initial-game)]
    (println (str (:pet acc-game)))
    (println (mh/format-menu main-menu-options #(str % ": " (first %2))))

    (let [[_ menu-f] (mh/ask-for-menu-option main-menu-options)
          updated-game? (some-> acc-game
                                (menu-f)
                                (pg/update-game-by-time)
                                (serial/write-save))]

      (cond
        (nil? updated-game?)
        acc-game

        (p/dead? (:pet updated-game?))
        (recur (handle-death updated-game?))

        :else
        (recur updated-game?)))))


(defn -main []
  (let [state-save (or (serial/read-save?)
                       default-new-game)

        end-game (main-loop state-save)]

    (serial/write-save end-game)
    nil))