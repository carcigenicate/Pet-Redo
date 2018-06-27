(ns pet-redo.core-logic.settings)

(def ms-per-day 86400000) ; (* 1000 60 60 24)

(defrecord Simulation-Settings [pain-per-tick heal-per-tick starve-per-tick ms-per-tick])

; TODO: heal-per-right?
(defrecord Question-Settings [pain-per-wrong feed-per-right])

(defrecord Game-Settings [sim-settings q-settings])

(defn new-settings [pain-per-tick heal-per-tick starve-per-tick pain-per-wrong feed-per-right ms-per-tick]
  (->Game-Settings
    (->Simulation-Settings pain-per-tick heal-per-tick starve-per-tick ms-per-tick)
    (->Question-Settings pain-per-wrong feed-per-right)))

(defn adjusted-sim-settings [pain-per-day heal-per-day starve-per-day ms-per-tick]
  (let [ticks-per-day (double (/ ms-per-day ms-per-tick))
        [ppt hpt spt] (map #(/ % ticks-per-day) [pain-per-day
                                                 heal-per-day
                                                 starve-per-day])]
    (->Simulation-Settings ppt hpt spt ms-per-tick)))