(ns pet-redo.quiz-logic.question-generator
  (:require [helpers.general-helpers :as g]
            [pet-redo.quiz-logic.question :as q]
            [pet-redo.quiz-logic.question-template :as qt]
            [pet-redo.quiz-logic.template-generator :as tg]))

(defn generate-question-from-template [q-template rand-gen]
  (let [args (qt/random-args-for q-template rand-gen)
        {:keys [eval-f]} q-template]
    (q/->Question (qt/repr-for q-template args)
                  (apply eval-f args))))

(defn generate-question-from-templates [q-templates rand-gen]
  (generate-question-from-template
    (g/random-from-collection q-templates rand-gen)
    rand-gen))