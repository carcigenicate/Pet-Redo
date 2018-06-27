(ns pet-redo.text-game.question-loop
  (:require [helpers.general-helpers :as g]
            [pet-redo.quiz-logic.question-generator :as qg]
            [pet-redo.core-logic.pet :as p]
            [pet-redo.core-logic.game :as pg]
            [pet-redo.ui-helpers :as ui-h]))

(defn parse-int? [n]
  (try
    (Long/parseLong n)

    (catch NumberFormatException _
      nil)))

(defn- ask-question [q-repr]
  (g/ask-for-input (str "> " q-repr " = ") "" (constantly true)))

(defn question-loop [game question-templates rand-gen]
  (loop [acc-game game]
    (println (str "\n" (:pet acc-game)))

    (let [q (qg/generate-question-from-templates question-templates rand-gen)
          {:keys [repr answer]} q

          raw-guess (ask-question repr)]

      (if-let [parsed-guess (parse-int? raw-guess)]
        (let [right? (= answer parsed-guess)

              {:keys [pet] :as adv-game} (-> acc-game
                                           (ui-h/effect-game-with-question right?)
                                           (pg/update-game-by-time))]
          (if (p/dead? pet)
            adv-game

            (do
              (if right?
                (println "Right!")
                (println (str "Wrong (" answer ") :(")))

              (recur adv-game))))

        acc-game))))

