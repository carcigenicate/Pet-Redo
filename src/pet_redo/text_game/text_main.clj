(ns pet-redo.text-game.text-main
  (:require [pet-redo.core-logic.game :as pg]
            [pet-redo.serialization.serialization :as serial]
            [pet-redo.text-game.question-loop :as ql]
            [helpers.general-helpers :as g]
            [pet-redo.quiz-logic.std-question-templates :as std-t]

            [clojure.string :as str]
            [pet-redo.core-logic.settings :as s]))

(def global-rand-gen (g/new-rand-gen 99))
(def default-question-template std-t/std-templates)

(def default-new-settings
  (s/->Game-Settings (s/adjusted-sim-settings 75 80 60 500)
                     (s/->Question-Settings 5 10)))

(def default-new-game
  (pg/new-game-now 100 100 default-new-settings))

(defn answer-questions [game]
  (-> game
    (ql/question-loop default-question-template global-rand-gen)))

(defn store [game]
  game)

(defn exit [game]
  (serial/write-save game)
  nil)

(def main-menu-options
  {0 ["Answer Questions" answer-questions]
   1 ["Store" store]
   2 ["Exit" exit]})

(defn format-menu [options]
  (->> options
       (map (fn [[option [title _]]]
              (str option ": " title)))
       (str/join "\n")))

(defn print-fl [& strings]
  (apply print strings)
  (flush))

(defn ask-for-menu-option []
  (->
    (g/ask-for-input "Enter an option:"
                     "That's an invalid option."
                     #(some-> % (g/parse-int) (main-menu-options)))
    (g/parse-int)))

(defn main-loop [initial-game]
  (loop [acc-game (pg/update-game-by-time initial-game)]
    (println (format-menu main-menu-options))
    (let [[_ menu-f] (main-menu-options (ask-for-menu-option))
          updated-game (some-> acc-game
                               (menu-f)
                               (pg/update-game-by-time)
                               (serial/write-save))]
      (if updated-game
        (recur updated-game)
        acc-game))))


(defn -main []
  (let [state-save (or (serial/read-save?)
                       default-new-game)

        end-game (main-loop state-save)]

    (serial/write-save end-game)
    nil))