(ns ^:figwheel-hooks space.site.cljs.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [space.site.cljs.events.core]
            [space.site.cljs.route.core :as route]))

;; Mount the application's ui into '<div id="app"/>'
(defn- run-app
  "Use this function to (re)start the site"
  []
  (rf/dispatch-sync [:initialize])
  (reagent/render [route/routed-page]
                  (js/document.getElementById "app")))

;; This is called from JavaScript
(defn ^:export run []
  (run-app))

;; This is called by figwheel-main on hotreload
(defn ^:after-load reload []
  (run-app))
