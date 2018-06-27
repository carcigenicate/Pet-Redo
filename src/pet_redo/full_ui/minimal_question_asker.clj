(ns pet-redo.full-ui.minimal-question-asker
  (:require [seesaw.core :as sc]
            [seesaw.dev :as sd]
            [seesaw.graphics :as sg]

            [pet-redo.text-game.text-main :as text-main]
            [helpers.general-helpers :as g]
            [pet-redo.ui-helpers :as ui-h]
            [pet-redo.quiz-logic.question-generator :as qg]
            [pet-redo.quiz-logic.std-question-templates :as std-t])
  (:import (java.awt AWTEvent Component)))

(def question-font {:size 100, :name "Arial"})

(def global-rand-gen (g/new-rand-gen 99))

(def std-q-templates std-t/std-templates)

(defn populate-question-panel [q-panel]
  (let [{:keys [repr] :as question} (qg/generate-question-from-templates
                                         std-q-templates
                                         global-rand-gen)

        q-box (sc/select q-panel [:#question-display])
        submit-btn (sc/select q-panel [:#submit-btn])]

    (sc/config! q-box :text repr)
    (sc/config! submit-btn :user-data question)))

(defn new-answer-box []
  (let [answer-box (sc/text :font question-font, :id :answer-input)]

    (sc/listen answer-box
       :key-pressed (fn [_]
                      (let [ok? #(or (= \-)
                                     (Character/isDigit ^Character %))]
                        (sc/invoke-later
                          (->> answer-box
                               (sc/text)
                               (filter ok?)
                               (apply str)
                               (sc/text! answer-box))))))

    answer-box))

(defn temporarily-change-border [^Component c, duration, new-color]
  (let [style-key :background
        old-color (sc/config c style-key)]
    (sc/invoke-later
      (sc/config! c style-key new-color))

    (sc/timer (fn [_]
                (sc/config! c style-key old-color))
              :initial-delay duration, :repeats? false)))

(defn submit-handler [q-panel, game-atom, ^AWTEvent e]
  (let [src (.getSource e)
        {:keys [repr answer] :as question} (sc/user-data src)
        ans-input (sc/select q-panel [:#answer-input])]

    (when-let [guess (g/parse-int (sc/text ans-input))]
      (let [right? (= guess answer)]
        (swap! game-atom ui-h/effect-game-with-question right?)

        (temporarily-change-border ans-input 250 (if right? :green :red))
        (sc/text! ans-input "")
        (populate-question-panel q-panel)))))

(defn new-question-panel [game-atom]
  (let [q-panel (sc/horizontal-panel)

        q-box (sc/label :font question-font, :id :question-display)
        answer-box (new-answer-box)
        submit-btn (sc/button :text "Submit", :id :submit-btn)]

    (sc/listen submit-btn
      :action (partial submit-handler q-panel game-atom))

    (sc/config! q-panel :items [q-box answer-box submit-btn])

    (populate-question-panel q-panel)

    q-panel))

(defn new-main-panel [game-atom]
  (let [q-panel (new-question-panel game-atom)

        main-panel (sc/border-panel :center q-panel)]

    main-panel))

(defn new-frame [initial-game]
  (let [game-atom (atom initial-game)

        main-panel (new-main-panel game-atom)
        frame (sc/frame :size [1000 :by 500]
                        :content main-panel)]

    frame))