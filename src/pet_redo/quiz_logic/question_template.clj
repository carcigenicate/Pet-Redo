(ns pet-redo.quiz-logic.question-template
  (:require [helpers.general-helpers :as g]))

(defrecord Argument-Template [min-n max-n floating?])

(defrecord Question-Template [arg-ranges repr-f eval-f])

(defn int-arg [min-n max-n]
  (->Argument-Template min-n max-n false))

(defn floating-arg [min-n max-n]
  (->Argument-Template min-n max-n true))

(defn repr-for [q-template args]
  ((:repr-f q-template) args))

(defn random-args-for [q-template rand-gen]
  (->> q-template
       :arg-ranges
       (mapv (fn [{:keys [min-n max-n floating?]}]
               (let [rand-f (if floating? g/random-double g/random-int)]
                 (rand-f min-n max-n rand-gen))))))
