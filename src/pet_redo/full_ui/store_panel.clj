(ns pet-redo.full-ui.store-panel
  (:require [pet-redo.store.item :as i]
            [pet-redo.store.items :as is]
            [pet-redo.full-ui.helpers :as fh]
            [pet-redo.defaults :as default]
            [pet-redo.store.helpers :as sh]

            [seesaw.core :as sc]
            [seesaw.dev :as sd])
  (:import (javax.swing JComboBox)))

(def store-font {:size 30, :name "Arial"})

; TODO: Make collapsible somehow?
(defn new-store-panel [items game-atom]
  (let [format-item #(str "<html>"(:description %) "<br>" (:price %) "sat</html>")
        item-entires (map format-item items)
        lookup-map (into {} (map #(vector (format-item %) %) items)) ; TODO: Hack!

        drop-list (sc/combobox :model item-entires, :font store-font)

        buy-btn (sc/button :text "Buy", :font store-font)

        store-panel (sc/border-panel :north drop-list,
                                     :south buy-btn)]

    (sc/listen buy-btn
               :action (fn [_]
                         (swap! game-atom
                           #(let [{:keys [price] :as item} (-> drop-list (sc/selection) (lookup-map))]
                              (if (sh/enough-money? % price)
                                (-> %
                                    (sh/effect-game-with item)
                                    (sh/unsafe-charge-game price))

                                (do
                                  (fh/maybe-flash-background! buy-btn :red 250)
                                  %))))))


    store-panel))

(defn new-main-store-panel [game-atom]
  (let [store-panel (new-store-panel is/store-items game-atom)]
    store-panel))

(defn new-test-frame []
  (let [game-atom (atom (default/new-game-now))
        content (new-main-store-panel game-atom)

        frame (sc/frame :size [1000 :by 1000]
                        :content content)]
    frame))