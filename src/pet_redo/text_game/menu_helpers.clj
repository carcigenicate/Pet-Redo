(ns pet-redo.text-game.menu-helpers
  (:require [clojure.string :as str]
            [helpers.general-helpers :as g]
            [pet-redo.core-logic.game :as pg]))

(defn format-menu [options option-formatter]
  (->> options
       (map (fn [i option]
              (option-formatter i option))
            (range))
       (str/join "\n")))

(defn ask-for-menu-option [menu-options]
  (->>
    (g/ask-for-input "Enter an option:"
                     "That's an invalid option."
                     #(some->> % (g/parse-int) (get menu-options)))
    (g/parse-int)
    (get menu-options)))
