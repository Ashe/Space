(ns space.site.cljs.design.core
  (:require [reagent.core :as reagent]
            [space.site.cljs.design.navbar :as n]
            [space.site.cljs.design.footer :as f]))

;; Retrieve components from their respective namespaces 
(defn navbar [] [n/navbar])
(defn footer [] [f/footer])
