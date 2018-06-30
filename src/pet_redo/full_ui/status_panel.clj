(ns pet-redo.full-ui.status-panel
  (:require [seesaw.core :as sc]))

(def stat-font {:size 50, :name "Arial"})

(defn- new-display-panel []
  (let [stat #(sc/flow-panel :items [(sc/label :text (str % ":"), :font stat-font)
                                     (sc/label :text "?", :font stat-font, :id %2)])
        stats [(stat "Health" :pet-health)
               (stat "Satiety" :pet-satiety)
               #_(And more)]]

    (sc/vertical-panel :items stats)))

(defn update-status-panel [status-panel game]
  (let [set-stats (partial mapv (fn [[id v]]
                                  (sc/text! (sc/select status-panel [id])
                                            (str v))))
        {:keys [pet]} game
        {:keys [health satiety]} pet]

    (set-stats [[:#pet-health health]
                [:#pet-satiety satiety]])))

(defn new-main-status-panel [game-atom]
  (let [display-panel (new-display-panel)
        main-panel (sc/vertical-panel :items [display-panel], :id :status-panel)]

    (update-status-panel display-panel @game-atom)

    main-panel))

(defn- new-frame [initial-game]
  (let [game-atom (atom initial-game)

        main-panel (new-main-status-panel game-atom)

        frame (sc/frame :size [1000 :by 1000]
                        :content main-panel)]

    frame))