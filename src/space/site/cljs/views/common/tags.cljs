(ns space.site.cljs.views.common.tags)

;; @TODO: Make this customisable
;; @TODO: Merge with views/post.cljs
(defn make-tag
  "Make a tag from a tag's ID"
  [id]
  (let [label (case id 
                  0 "Clojure"
                  1 "Reagent"
                  2 "Re-frame"
                  nil)
        colour (case id
                  0 "is-info"
                  1 "is-success"
                  2 "is-danger"
                  nil)]
    (when (and label colour) 
      [:a.tag.is-info 
          { :key (str "tag-" id)
            :class colour
            :href (str "/tags/" label)}
        label])))
