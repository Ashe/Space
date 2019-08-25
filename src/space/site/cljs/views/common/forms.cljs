(ns space.site.cljs.views.common.forms
  (:require [reagent.core :as r]))

;; Forward declare
(declare valid-url? md-editor tag-input check-for-tags)

(defn make-text-input
  "Makes a text input with a given atom, tag and set of messages"
  [input-atom input input-type label placeholder icn & [help-okay help-invalid min-length max-length]]
  (let [length (count (or @input-atom ""))
        colour (cond
                  (zero? length) ""
                  (or 
                    (and (> min-length 0) (< length min-length))
                    (and (> max-length 0) (> length max-length))) 
                    "is-danger"
                  :else "is-success")
        icon (cond
                  (zero? length) icn
                  (and 
                      (or (<= min-length 0) (>= length min-length)) 
                      (or (<= max-length 0) (<= length max-length)))
                    "fa-check"
                  :else "fa-exclamation-triangle")]
  [:div.field
    [:label.label label]
    [:div.control.has-icons-right
      [input
        { :type input-type
          :placeholder placeholder
          :on-change #(reset! input-atom (.-value (.-target %)))
          :class colour}]
      [:span.icon.is-small.is-right
        [:i.fas {:class icon}]]]
      (let [has-minimum? (> min-length 0)
            has-maximum? (> max-length 0)
            too-few? (< length min-length)
            too-many? (> length max-length)
            entered? (> length 0)]
        [:p.help {:class colour}
          (if (or 
                (and has-minimum? too-few?) 
                (and has-maximum? too-many?) 
                (not entered?))
              help-invalid 
              help-okay)
          (cond
            (and has-minimum? too-few?) 
              (str " (" (- min-length length) " characters to go)")
            (and has-maximum? (<= length max-length))
              (str " (" (- max-length length) " characters to go)")
            (and has-maximum? too-many?) 
              (str " (" (- length max-length) " characters too many)"))])]))


(defn- make-tag-input 
  "Creates a text editor with Simple MDE integration"
  [label allowed-tags placeholder icon help]
  (let [text-atom (r/atom "")]
    [:div.field
      [:label.label label]
      [check-for-tags text-atom allowed-tags]
      [:div.control.has-icons-right
        [:input.input
          { :type "text"
            :placeholder placeholder
            :on-change 
              #(reset! text-atom (.-value (.-target %)))}]
        [:span.icon.is-small.is-right
          [:i.fas {:class icon}]]]
      [:p.help help]]))

(defn make-md-input
  "Makes a text input that supports markdown"
  [input-atom label icon placeholder]
  [:div.field
    [:label.label label]
    [:div.control.has-icons-right
      [md-editor input-atom 
        { :type "text"
          :placeholder placeholder
          :on-change #(reset! input-atom (.-value (.-target %)))}]
      [:span.icon.is-small.is-right
        [:i.fas {:class icon}]]]])

(defn make-url-input
  "Makes a URL input with a given atom, tag and set of messages"
  [input-atom input label placeholder help help-okay help-invalid]
  (let [length (count (or @input-atom ""))
        is-valid (valid-url? @input-atom)
        colour (cond
                  (zero? length) ""
                  is-valid "is-success"
                  :else "is-danger")
        icon (cond 
              (zero? length) "fa-link"
              is-valid "fa-check" 
              :else "fa-exclamation-triangle")]
  [:div.field
    [:label.label label]
    [:div.control.has-icons-right
      [input
        { :type "text"
          :placeholder placeholder
          :on-change #(reset! input-atom (.-value (.-target %)))
          :class colour}]
      [:span.icon.is-small.is-right
        [:i.fas {:class icon}]]]
        [:p.help {:class colour}
          (cond 
            (zero? length) help
            is-valid help-okay 
            :else help-invalid)]]))

(defn make-checkbox
  "Makes a checkbox"
  [checkbox-atom label]
  [:div.field
    [:div.control
      [:label.checkbox
        [:input 
            { :type "checkbox"
              :value @checkbox-atom
              :on-change #(reset! checkbox-atom (.-checked (.-target %)))
              :style {:margin-right "8px"}}]
        label]]])

(defn- check-for-tags
  "Given a string, check for any supplied tags"
  [text-atom allowed-tags]
  (println @text-atom)
  (let [make-tag 
          (fn [label lvl col]
            [:div.control
              [:div.tags.has-addons
                [:div.tag {:class col} label]
                [:div.tag.is-dark (str lvl)]
                [:div.tag.is-delete]]])]
    [:div#tag-display.field.is-grouped.is-grouped-multiline
        {:style {:float "left"}}
      [make-tag "Clojure" 0 "is-primary"]
      [make-tag "Reagent" 32 "is-warning"]
      [make-tag "Re-frame" 99 "is-danger"]]))

(defn- md-editor 
  "Creates a text editor with Simple MDE integration"
  [input-atom attrs]
  (r/create-class
    { :component-did-mount
        (fn [comp]
          (let [mde (js/SimpleMDE. (clj->js {:element (r/dom-node comp)}))]
            (.on mde.codemirror "change" 
                #(reset! input-atom (.value mde)))
            mde))
      :reagent-render
        (fn []
          [:textarea.textarea attrs])}))

(defn- valid-url?
  "Checks to see if a URL is valid or not"
  [url]
  (let [pattern #"(?i)^(?:(?:https?|ftp)://)(?:\S+(?::\S*)?@)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,}))\.?)(?::\d{2,5})?(?:[/?#]\S*)?$"]
    (re-matches pattern url)))
