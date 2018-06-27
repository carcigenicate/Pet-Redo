(ns pet-redo.quiz-logic.template-generator
  (:require [pet-redo.quiz-logic.question-template :as qt]))

(defn simple-repr-f [op-symbol]
  #(apply str (interpose (str " " op-symbol " ") %)))

(defn new-simple-template [arg-template n-args op-symbol eval-f]
  (qt/->Question-Template (repeat n-args arg-template)
                          (simple-repr-f op-symbol)
                          eval-f))

(defn new-binary-template [arg-template op-symbol eval-f]
  (new-simple-template arg-template 2 op-symbol eval-f))