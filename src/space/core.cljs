(ns ^:figwheel-hooks my-project.core
  (:require [reagent.core :as r]))

(def times-clicked (r/atom 0))

(defn get-greeting
  "Get the greeting of the page"
  [n]
  (let [evers (repeat n "ever ")]
    (str "My first " (apply str evers) " react component!")))

(defn my-component []
  (let [n-evers @times-clicked]
    [:div
      [:p (get-greeting n-evers)]
      [:input {:type "button"
               :value (str "Clicked " n-evers " times!")
               :on-click (fn [_]
                           (swap! times-clicked inc))}]]))

(defn ^:export main []
  (r/render 
    [my-component]
    (.getElementById js/document "app")))

(main)
