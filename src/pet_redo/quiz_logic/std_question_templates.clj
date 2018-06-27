(ns pet-redo.quiz-logic.std-question-templates
  (:require [pet-redo.quiz-logic.template-generator :as tg]
            [pet-redo.quiz-logic.question-template :as qt]))

(def ^:private std-factory
  (partial tg/new-binary-template (qt/int-arg 0 10)))

(def std-division-template
  (qt/->Question-Template [(qt/int-arg 0 10) (qt/int-arg 1 10)]
                          (tg/simple-repr-f \/)
                          /))

(def std-templates
  [(std-factory \+ +)
   (std-factory \- -)
   (tg/new-binary-template (qt/int-arg 1 10) \* *)])
