(ns pet-redo.core-logic.time-helpers
  (:require [java-time.core :as jc]
            [java-time.zone :as jz]
            [java-time.format :as jf]))

(def format-str "zz-SSSS-ss-mm-kk-dd-MM-yyyy")

(defn current-timezone []
  (jz/zone-id))

(defn now []
  (jz/zoned-date-time (current-timezone)))

(defn ms-elapsed [start-date end-date]
  (jc/time-between start-date end-date :millis))

(defn format-date [date-obj]
  (jf/format format-str date-obj))

(defn parse-date [^String date-str]
  (jz/zoned-date-time format-str date-str))