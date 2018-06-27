(ns pet-redo.text-game.test-ns
  (:require [helpers.general-helpers :as g]
            [pet-redo.text-game.question-loop :as ql]
            [pet-redo.core-logic.game :as pg]
            [pet-redo.quiz-logic.std-question-templates :as std-t]
            [pet-redo.serialization.serialization :as serial]
            [pet-redo.core-logic.time-helpers :as th]))

(defn start-q-loop []
  (let [g pg/test-game
        r (g/new-rand-gen 99)]
    (ql/question-loop g std-t/std-templates r)
    nil))
