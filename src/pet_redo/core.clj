(ns pet-redo.core
  (:require [pet-redo.text-game.text-main :as text-main]
            [pet-redo.full-ui.main-panel :as ui-main]
            [pet-redo.defaults :as default]
            [pet-redo.serialization.serialization :as serial])
  (:gen-class))

(defn -main [& [interface]]
  (let [loaded (or (serial/read-save?)
                   (default/new-game-now))
        inter (or interface "u")]

    (if (= (first inter) \u)
      (ui-main/-main loaded)
      (text-main/-main loaded))))
