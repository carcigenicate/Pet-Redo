(ns pet-redo.full-ui.helpers
  (:require [seesaw.core :as sc]))

(defn flash-property
  "Alters the property at prop-key to flash-value for duration-many milliseconds,
    then reverts the property back to the original value.

  Will break if a new flash is started before a previous one ends."
  [component prop-key flash-value duration component-done-callback]
  (let [old-value (sc/config component prop-key)]
    (sc/config! component prop-key flash-value)

    (sc/timer (fn [_]
                (sc/config! component prop-key old-value)
                (component-done-callback))
              :initial-delay duration, :repeats? false)))

(let [flashing?-atom (atom #{})]
  (defn maybe-flash-background!
    "Alters the property at prop-key to flash-value for duration-many milliseconds,
      then reverts the property back to the original value.

    Does nothing if the element is already being flashed.

    Returns whether or not a flash was triggered."
    [component flash-color duration]
    (when-not (@flashing?-atom component)
      (swap! flashing?-atom conj component)

      (flash-property component :background flash-color duration
                      #(swap! flashing?-atom disj component))

      true)))

#_
(defn flash-property [component prop-key flash-value duration]
  (let [old-value (sc/config component prop-key)]
    (sc/config! component prop-key flash-value)

    (sc/timer (fn [_]
                (sc/config! component prop-key old-value))
              :initial-delay duration, :repeats? false)))