(ns pet-redo.core-logic.bounded-resource
  (:require [clojure.edn :as edn]))

(def ^:dynamic *str-repr-prec* 1)

(defn- limit [n]
  (format (str "%." *str-repr-prec* "f") (double n)))

(defrecord Bounded-Resource [n max]
  Object
  (toString [this] (str (limit n) "/" (limit max))))

(defn new-maxed-resource [max]
  (->Bounded-Resource max max))

(defn update-resource [resource f & args]
  (let [{ma :max} resource]
    (update resource :n
            #(-> %
                 (f)
                 (max 0)
                 (min ma)))))
