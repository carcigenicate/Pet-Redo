(ns pet-redo.defaults
  (:require [pet-redo.core-logic.settings :as s]
            [pet-redo.core-logic.game :as pg]))

(def new-settings
  (s/->Game-Settings (s/adjusted-sim-settings 80 100 95 500)
                     (s/->Question-Settings 5 5)))

; TODO: Add a "challenge" mode
(def killer-new-settings
  (s/->Game-Settings (s/adjusted-sim-settings 300000 400000 300000 500)
                     (s/->Question-Settings 5 5)))

(defn new-game-now []
  (pg/new-game-now 100 100 new-settings))