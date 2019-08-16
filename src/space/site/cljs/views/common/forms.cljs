(ns space.site.cljs.views.common.forms)

;; Forward declare
(declare valid-url?)

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
      (cond
        (< length min-length) 
          (when (pos? min-length)[:p.help
              {:class colour}
            help-invalid " (" (str (- min-length length)) " characters to go)"])
        (or (<= max-length 0) (< length max-length))
          [:p.help.is-success
            help-okay (when (pos? max-length) (str " (" (str (- max-length length)) " characters left)"))]
        (> max-length 0)
          (when (pos? max-length) [:p.help.is-danger
            help-invalid " (" (str (- length max-length)) " characters too many)"]))]))

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

(defn valid-url?
  "Checks to see if a URL is valid or not"
  [url]
  (let [pattern #"(?i)^(?:(?:https?|ftp)://)(?:\S+(?::\S*)?@)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,}))\.?)(?::\d{2,5})?(?:[/?#]\S*)?$"]
    (re-matches pattern url)))
