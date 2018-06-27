(ns pet-redo.ui-helpers
  (:require [pet-redo.core-logic.pet :as p]))

(defn effect-game-with-question [game right?]
  (let [{:keys [pain-per-wrong feed-per-right]}
        (-> game :settings :q-settings)]
    (update game :pet
            #(if right?
               (p/feed % feed-per-right)
               (p/hurt % pain-per-wrong)))))