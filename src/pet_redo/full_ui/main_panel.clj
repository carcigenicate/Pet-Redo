(ns pet-redo.full-ui.main-panel
  (:require [seesaw.core :as sc]

            [pet-redo.text-game.text-main :as text-main]
            [pet-redo.full-ui.question-panel :as qp]
            [pet-redo.full-ui.status-panel :as sp]
            [pet-redo.full-ui.death-panel :as dp]
            [pet-redo.full-ui.store-panel :as stp]

            [seesaw.dev :as sd]
            [pet-redo.core-logic.game :as pg]
            [pet-redo.core-logic.pet :as p]
            [pet-redo.core-logic.settings :as s]
            [seesaw.color :as s-col]
            [pet-redo.serialization.serialization :as serial]
            [pet-redo.core-logic.time-helpers :as th]
            [pet-redo.defaults :as default])

  (:import [javax.swing Timer]))

(def status-refresher-delay 500)
(def game-advancer-delay 500)
(def save-delay (* 1000 30))

(defn new-pet-panel []
  (sc/label :text "PET! (Placeholder)",
            :font {:size 100, :name "Arial"}
            :halign :center, :valign :center
            :background :green))

(defn new-main-panel [game-atom]
  (let [pet-panel (new-pet-panel)
        q-panel (qp/new-main-question-panel game-atom)
        stat-panel (sp/new-main-status-panel game-atom)
        store-panel (stp/new-main-store-panel game-atom)

        main-panel (sc/border-panel :center pet-panel
                                    :south q-panel
                                    :north stat-panel
                                    :west store-panel)]

    main-panel))

(defn new-status-refresher [root game-atom refresh-delay]
  (sc/timer (fn [_]
              (when-let [stat-panel (sc/select root [:#status-panel])]
                (let [game @game-atom]
                  (sp/update-status-panel stat-panel game))))
            :initial-delay refresh-delay, :delay refresh-delay))

(defn new-game-callback [root game-atom]
  (sc/config! root
     :content (new-main-panel game-atom)))

(defn new-game-advancer [root game-atom update-delay]
  (let [new-d-panel #(dp/new-death-panel % (partial new-game-callback root game-atom))
        new-game (default/new-game-now)]
    (sc/timer
      (fn [_]
        (swap! game-atom
               #(let [adv (pg/update-game-by-time %)
                      adv-pet (:pet adv)]
                  (if (p/dead? adv-pet)
                    (do
                      (sc/config! root :content (new-d-panel adv))
                      (serial/write-save new-game)
                      new-game)

                    adv))))

      :initial-delay update-delay, :delay update-delay)))

(defn new-saver [game-atom save-delay]
  (sc/timer
    (fn [_]
      (serial/write-save @game-atom)
      (println (str "Saved at " (th/format-date (th/now)) "...")))
    :initial-delay save-delay, :delay save-delay))

(defn stop-when-closing [root-frame & timers]
  (sc/listen root-frame
     :window-closing
     (fn [_]
         (doseq [^Timer t, timers]
           (.stop t))
         (println "Timer cleanup finished..."))))

; TODO: Create a -main to handle loading

(defn new-frame [initial-game]
  (let [game-atom (atom initial-game)

        main-panel (new-main-panel game-atom)

        frame (sc/frame :size [1000 :by 1000]
                        :content main-panel)

        status-refresher (new-status-refresher frame game-atom status-refresher-delay)
        advancer (new-game-advancer frame game-atom game-advancer-delay)
        saver (new-saver game-atom save-delay)]

    (stop-when-closing frame status-refresher advancer saver)

    (sc/listen frame
       :window-closing (fn [_]
                         (serial/write-save @game-atom)))

    frame))

(defn -main [loaded-state]
  (-> loaded-state
      (new-frame)
      (sc/show!)))