(ns space.site.cljs.views.post
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [markdown.core :as md]
            [markdown.transformers :as mdt]
            [space.common.core :as cmn]
            [space.site.cljs.views.loading :as l]
            [space.site.cljs.views.common.user :as usr]
            [space.site.cljs.views.common.tags :as tags]
            [space.site.cljs.events.post :as p]))

(declare md-renderer escape-html show-post show-post-not-found)

(defn post
  "Display the page for a specific forum post"
  [{:keys [route-key path-params query-params]}]
  (let [post-id (cmn/str->num (:post-number path-params))]
    (p/dispatch-fetch-post post-id)
    [:div.container.is-widescreen
      (if-let [p @(rf/subscribe [:post])]
        (if (and p (= (:post-number p) post-id))
          [show-post p]
          [l/loading-screen
              (str "Loading post: " post-id)
              (str "Post " post-id)])
        [show-post-not-found])]))

(defn- show-post
  "Show the requested post"
  [p]
  [:div

    ;; Breadcrumb
    [:nav.breadcrumb
      [:ul
        [:li>a {:href "/"} "Space"]
        [:li>a {:href "/"} "Page ?"]
        [:li.is-active>a (:post-title p)]]]

    [:article.box
      [:div.columns
        
        [:div.column.is-narrow

          ;; User's picture
          ;; @TODO: Decide if it should show your picture if
          ;; you're currently anonymous
          (when-let [user-img-src (:user-image p)]
            [:a {:href (str "/user/" (:username p))}
              [:figure.has-text-centered
                [:span.image.is-128x128.is-inline-block
                  [:img {:src user-img-src}]]]])

          ;; Link to user
          [:div (usr/create-user-link p {:seperate-names true})]

          ;; Post date
          [:p.is-size-7 (:post-date p)]]

        [:div.column

          ;; Post title
          [:h1.title (:post-title p)]

          ;; Post image
          (when-let [post-img-src (:post-image p)]
            [:a {:href post-img-src}
              [:figure.has-text-centered
                [:span.image.is-inline-block
                  [:img {:src post-img-src}]]]])

          ;; Post tags
          [:div.field.is-grouped.is-grouped-multiline
            (map tags/make-tag (:tags p))]

          ;; Body
          [md-renderer (:post-content p)]]]]])

(defn- show-post-not-found
  "Show a post not found screen"
  []
  [:div "Post not found :("])

(defn- md-renderer 
  "Shows markdown content"
  [text]
  (r/create-class
    { :component-did-mount
        (fn [_]
          (.forEach (.querySelectorAll js/document "pre code") 
            (fn [e] 
              (js/console.dir e)
              (js/hljs.highlightBlock e)
              (aset e "style" "background-color" "transparent")
              )))
      :component-did-update
        (fn [_]
          (.forEach (.querySelectorAll js/document "pre code") 
            (fn [e] 
              (js/console.dir e)
              (js/hljs.highlightBlock e)
              (aset e "style" "background-color" "transparent")
              )))
      :reagent-render
        (fn []
          [:div
            {:dangerouslySetInnerHTML
              {:__html  
                (md/md->html 
                    text
                    :replacement-transformers
                    (into [escape-html] mdt/transformer-vector))}}])}))

(def ^:dynamic ^:no-doc *html-mode* :xhtml)

(defn- escape-html
  "Change special characters into HTML character entities."
  [text state]
  [(if-not (or (:code state) (:codeblock state))
    (clojure.string/escape text 
       {\& "&amp;" 
        \< "&lt;" 
        \> "&gt;" 
        \" "&quot;"
        \' "&#39;"}) 
    text) state])
