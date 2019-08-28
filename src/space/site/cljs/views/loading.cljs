(ns space.site.cljs.views.loading)

;; @TODO: Make this screen cute? 
(defn loading-screen
  "Show a loading screen while waiting for content"
  [msg breadcrumb-label] 
  [:div
    [:nav.breadcrumb
      [:ul
        [:li>a {:href "/"} "Space"]
        [:li.is-active>a breadcrumb-label]]]
    [:p msg]])
