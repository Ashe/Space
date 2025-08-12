(ns space.site.cljs.views.common.posts
  (:require [re-frame.core :as rf]
            [space.site.cljs.views.common.user :as usr]
            [space.site.cljs.views.common.tags :as tags]))

;; Make all elements spaced evenly
(def forum-spacing "10px 0px")

(defn make-post
  "An overview of a post"
  ([p] (make-post p []))
  ([p flags]
  [:div
      { :id (str "post-" (:post-number p))
        :key (:post-number p)
        :style {:margin forum-spacing}}
    [:div.box
      [:article.columns.is-vcentered
        
        ;; Post image
        ;; @TODO: Maybe factor this out to share with post page
        (when-let [img-src (or 
                      (:post-image p) 
                      (when (not (:is-anonymous p)) (:user-image p)))]
          [:div.column.is-narrow
            [:a {:href (str "/posts/" (:post-number p))}
              [:figure.has-text-centered
                [:span.image.is-inline-block
                    {:style
                      { :max-width "128px"
                        :max-height "128px"}}
                  [:img 
                    {:src img-src}]]]]])

        [:div.column
          [:div.content
            [:p 

              ;; Post title
              [:a 
                  {:href (str "/posts/" (:post-number p))}
               [:strong.is-size-4 (:post-title p)]]
              
              ;; User name and handle
              ;; If names shouldn't be displayed, make
              ;; an exception for anonymous posts to
              ;; create distinction between standard 
              ;; and anonymous posts
              (if (:hide-names flags)
                (when (:is-anonymous p) 
                    [:span.icon>i.fas.fa-user-secret])
                [:span [:br] (usr/create-user-link p flags)])

              ;; Post date
              [:small 
                  {:style {:margin-left "5px"}}
                (str " " (:post-date p))] [:br]

              ;; Post summary
              (:post-summary p)]

            ;; Post tags
            [:div.field.is-grouped.is-grouped-multiline
              (map tags/make-tag (:tags p))]
            ]]]]]))
