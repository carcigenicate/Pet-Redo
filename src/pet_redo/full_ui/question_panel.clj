(ns pet-redo.full-ui.question-panel
  (:require [seesaw.core :as sc]
            [seesaw.dev :as sd]
            [seesaw.graphics :as sg]

            [pet-redo.text-game.text-main :as text-main]
            [helpers.general-helpers :as g]
            [pet-redo.ui-helpers :as ui-h]
            [pet-redo.quiz-logic.question-generator :as qg]
            [pet-redo.quiz-logic.std-question-templates :as std-t]
            [pet-redo.full-ui.helpers :as fh])
  (:import (java.awt AWTEvent Component)
           (java.awt.event KeyEvent)
           (javax.swing UIManager)))

(def question-font {:size 100, :name "Arial"})

(def global-rand-gen (g/new-rand-gen))

(def std-q-templates std-t/std-templates)

(def default-background-color (UIManager/getColor "Panel.background"))

(defn question-repr [question]
  (str (:repr question) " = "))

(defn populate-question-panel [q-panel]
  (let [question (qg/generate-question-from-templates
                      std-q-templates
                      global-rand-gen)

        q-box (sc/select q-panel [:#question-display])]

    (sc/config! q-box :text (question-repr question),
                      :user-data question)))
#_
(defn temporarily-change-border [^Component c, duration, new-color]
  (let [style-key :background]
    (sc/invoke-later
      (sc/config! c style-key new-color))

    (sc/timer (fn [_]
                (sc/config! c style-key default-background-color))
              :initial-delay duration, :repeats? false)))

(defn submit-handler [q-panel, game-atom]
  (let [q-box (sc/select q-panel [:#question-display])
        {:keys [repr answer] :as question} (sc/user-data q-box)
        ans-input (sc/select q-panel [:#answer-input])]

    (when-let [guess (g/parse-int (sc/text ans-input))]
      (let [right? (= guess answer)]
        (swap! game-atom ui-h/effect-game-with-question right?)

        (fh/maybe-flash-background! ans-input (if right? :green :red) 250)
        (sc/text! ans-input "")
        (populate-question-panel q-panel)))))

(defn new-answer-box [q-panel game-atom]
  (let [answer-box (sc/text :font question-font, :halign :center
                            :id :answer-input)]

    (sc/listen answer-box
       :key-pressed (fn [^KeyEvent e]
                      (if (= (.getKeyChar e) \newline)
                        (submit-handler q-panel game-atom)

                        (let [ok? #(or (= \- %)
                                       (Character/isDigit ^Character %))]
                          (sc/invoke-later
                            (->> answer-box
                                 (sc/text)
                                 (filter ok?)
                                 (apply str)
                                 (sc/text! answer-box)))))))

    answer-box))

(defn new-question-panel [game-atom]
  (let [q-panel (sc/horizontal-panel)

        q-box (sc/label :font question-font, :id :question-display)
        answer-box (new-answer-box q-panel game-atom)
        submit-btn (sc/button :text "Submit", :id :submit-btn)]

    (sc/listen submit-btn
      :action (fn [_] (submit-handler q-panel game-atom)))

    (sc/config! q-panel :items [q-box answer-box submit-btn])

    (populate-question-panel q-panel)

    q-panel))

(defn new-main-question-panel [game-atom]
  (let [q-panel (new-question-panel game-atom)

        #_ (main-panel (sc/vertical-panel :items [q-panel]))]

    q-panel))

(defn new-frame [initial-game]
  (let [game-atom (atom initial-game)

        main-panel (new-main-question-panel game-atom)
        frame (sc/frame :size [1000 :by 500]
                        :content main-panel)]

    frame))