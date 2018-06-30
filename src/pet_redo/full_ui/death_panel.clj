(ns pet-redo.full-ui.death-panel
  (:require [seesaw.core :as sc]
            [seesaw.color :as s-col]
            [pet-redo.text-game.text-main :as text-main]
            [seesaw.dev :as sd]))

(def death-font {:size 50, :name "Arial"})
(def new-game-font (update death-font :size * 0.8))

(defn new-death-message-panel [dead-game]
  (let [{:keys [health satiety]} (:pet dead-game)

        label #(sc/label :font death-font,
                         :text (str %)
                         :valign :center
                         :halign :center)]

    (sc/flow-panel :items [(label (str "Your pet died :("))
                           (label (str "HP: " health " - " "Sat: " satiety))]
                   :background (s-col/color 255 61 0))))

(defn new-death-panel [dead-game new-game-callback]
  (let [death-message (new-death-message-panel dead-game)
        new-game-btn (sc/button :text "New Game", :font new-game-font)

        main-panel (sc/border-panel :center death-message
                                    :south new-game-btn)]

    (sc/listen new-game-btn
       :action (fn [_] (new-game-callback)))

    main-panel))

