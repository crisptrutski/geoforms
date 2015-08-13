(ns geoforms.forms
  (:require [json-html.core :refer [edn->hiccup]]
            [reagent.core :as reagent :refer [atom]]
            [reagent-forms.core :refer [bind-fields init-field value-of]]))


;; Example taken from https://github.com/reagent-project/reagent-forms/tree/master/forms-example


;;; DATA

;; should be in firebase

(defonce app-cms
  (atom
   {:title "Support local ideas!"
    :subtitle ""
    :instructions-district "1. Choose your district"
    :instructions-vote     "2. Check ideas you want to support!"
    :instructions-add      "3. Add your won ideas"
    :instructions-sign     "4. Sign your choices"}))

(def app-ideas
  (atom
   [{:id "1"
     :timestamp "2013-08-10 11:20:22"
     :districts ["Brooklyn"]
     :idea "New skatepark"
     :desc "Would be really nice to have a new skatepark"
     :links ["http://lapresse.ca/article-2"]
     :supporters [""]
     :category "Other..."}
    {:id "2"
     :timestamp "2013-08-10 11:20:24"
     :districts ["Manhattan"]
     :idea "More police to prevent steeling"
     :desc ""
     :links ["http://lapresse.ca/article-1"]
     :supporters [""]
     :category "Security"}
    {:id "3"
     :timestamp "2013-08-10 11:20:26"
     :districts ["Manhattan"]
     :idea "Create a park near fifth avenue"
     :desc "Would be awesome to have a park on broadway near fith avenue."
     :links ["http://lapresse.ca/article-56"]
     :supporters ["email@email.com" "email2@email.com" "john@hotmail.com"]
     :category "Green"}]))

(def app-users
  (atom
   [{:created "2013-08-10 11:20:22"
     :fullname "Leon Talbot"
     :email "email@email.com"
     :zip-code "G21 2C5" ;; this is canadian zip code.
     :age "32"
     :annual-revenue ""
     :subscribe-idea-alerts true
     :subscribe-volonteer-idea-alerts false
     :subscribe-district-alerts true
     :comments ""}
    {:created "2013-08-10 11:20:22"
     :fullname "John Talbot"
     :email "john@hotmail.com"
     :zip-code "G21 2C3"
     :age "33"
     :annual-revenue ""
     :subscribe-idea-alerts true
     :subscribe-volonteer-idea-alerts false
     :subscribe-district-alerts true
     :comments ""}
    {:created "2013-08-10 11:20:22"
     :fullname "Marc Talbot"
     :email "email2@email.com"
     :zip-code "G21 2C2"
     :age "34"
     :annual-revenue ""
     :subscribe-idea-alerts true
     :subscribe-volonteer-idea-alerts false
     :subscribe-district-alerts true
     :comments "better UX please. Thanks."}]))

(def app-cats
  (atom
   ["Shops" "Security" "Green" "Other..."]))

(def app-districts
  (atom ["Manhattan" "Brooklyn" "Queens" "Other"]))


;;; STATE

(def app-state
  (atom
   {:selected-districts @app-districts
    :added-ideas nil
    :added-user  nil}))


;;; FNS

(defn row [label input]
  [:div.row
   [:div.col-md-2 [:label label]]
   [:div.col-md-5 input]])

(defn radio [label name value]
  [:div.radio
   [:label
    [:input {:field :radio :name name :value value}]
    label]])

(defn input [label type id]
  (row label [:input.form-control {:field type :id id}]))

(defn friend-source [text]
  (filter
   #(-> % (.toLowerCase %) (.indexOf text) (> -1))
   ["Alice" "Alan" "Bob" "Beth" "Jim" "Jane" "Kim" "Rob" "Zoe"]))

(def form-template
  [:div

   [:h3 "1. Choose your district"]
   [:div.btn-group {:field :multi-select :id :every.position}
    (for [d @app-districts]
      [:button.btn.btn-default {:key (keyword d)} d])]


   [:h3 "2. Check ideas you want to support!"]

   [:div.checkbox
    [:label
     [:input.form-control {:field :checkbox :id :i-123}]
     "A new park near fifth avenue in Times Square"]]

   [:h3 "3. Add your won ideas"]

   [:h3 "4. Sign your choices"]

   (input "first name" :text :person.first-name)
   [:div.row
    [:div.col-md-2]
    [:div.col-md-5
     [:div.alert.alert-danger
      {:field :alert :id :errors.first-name}]]]

   (input "last name" :text :person.last-name)
   [:div.row
    [:div.col-md-2]
    [:div.col-md-5
     [:div.alert.alert-success
      {:field :alert :id :person.last-name :event empty?}
      "last name is empty!"]]]

   [:div.form-group
    [:label "age"]
    [:select.form-control {:field :list :id :person.age}
     [:option {:key :17-} "17-"]
     [:option {:key :18-24} "18-24"]
     [:option {:key :25-29} "25-29"]
     [:option {:key :30-34} "30-34"]
     [:option {:key :35-39} "35-39"]
     [:option {:key :40-44} "40-44"]
     [:option {:key :45-49} "45-49"]
     [:option {:key :50-54} "50-54"]
     [:option {:key :55-59} "55-59"]
     [:option {:key :60-64} "60-64"]
     [:option {:key :65-69} "65-69"]
     [:option {:key :70-74} "70-74"]
     [:option {:key :75-79} "75-79"]
     [:option {:key :80-84} "80-84"]
     [:option {:key :85+} "85+"]]]

   (input "email" :email :person.email)
   [:div.row
    [:div.col-md-2]
    [:div.col-md-5
     [:div.alert.alert-danger
      {:field :alert :id :errors.email :event empty?}
      "email is empty!"]]]
   (row
    "comments"
    [:textarea.form-control
     {:field :textarea :id :comments}])

   [:hr]
   (input "kg" :numeric :weight-kg)
   (input "lb" :numeric :weight-lb)

   [:hr]

   (row "Best friend"
        [:div {:field           :typeahead
               :id              :ta
               :data-source     friend-source
               :input-class     "form-control"
               :list-class      "typeahead-list"
               :item-class      "typeahead-item"
               :highlight-class "highlighted"}])
   [:br]

   (row "isn't data binding lovely?"
        [:input {:field :checkbox :id :databinding.lovely}])
   [:label
    {:field :label
     :preamble "The level of awesome: "
     :placeholder "N/A"
     :id :awesomeness}]

   [:input {:field :range :min 1 :max 10 :id :awesomeness}]

   [:h3 "option list"]
   [:div.form-group
    [:label "pick an option"]
    [:select.form-control {:field :list :id :many.options}
     [:option {:key :foo} "foo"]
     [:option {:key :bar} "bar"]
     [:option {:key :baz} "baz"]]]

   (radio
    "Option one is this and that—be sure to include why it's great"
    :foo :a)
   (radio
    "Option two can be something else and selecting it will deselect option one"
    :foo :b)

   [:h3 "multi-select buttons"]
   [:div.btn-group {:field :multi-select :id :every.position}
    [:button.btn.btn-default {:key :left} "Left"]
    [:button.btn.btn-default {:key :middle} "Middle"]
    [:button.btn.btn-default {:key :right} "Right"]]

   [:h3 "single-select buttons"]
   [:div.btn-group {:field :single-select :id :unique.position}
    [:button.btn.btn-default {:key :left} "Left"]
    [:button.btn.btn-default {:key :middle} "Middle"]
    [:button.btn.btn-default {:key :right} "Right"]]

   [:h3 "single-select list"]
   [:div.list-group {:field :single-select :id :pick-one}
    [:div.list-group-item {:key :foo} "foo"]
    [:div.list-group-item {:key :bar} "bar"]
    [:div.list-group-item {:key :baz} "baz"]]

   [:h3 "multi-select list"]
   [:ul.list-group {:field :multi-select :id :pick-a-few}
    [:li.list-group-item {:key :foo} "foo"]
    [:li.list-group-item {:key :bar} "bar"]
    [:li.list-group-item {:key :baz} "baz"]]])

(defn page []
  (let [doc (atom {:person {:first-name "John"
                            :age 35
                            :email "foo@bar.baz"}
                   :weight 100
                   :height 200
                   :bmi 0.5
                   :comments "some interesting comments\non this subject"
                   :radioselection :b
                   :position [:left :right]
                   :pick-one :bar
                   :unique {:position :middle}
                   :pick-a-few [:bar :baz]
                   :many {:options :bar}})]
    (fn []
      [:div
       [:div.page-header [:h1 (:title @app-cms)]]

       [bind-fields
        form-template
        doc
        (fn [[id] value {:keys [weight-lb weight-kg] :as document}]
           (cond
            (= id :weight-lb)
            (assoc document :weight-kg (/ value 2.2046))
            (= id :weight-kg)
            (assoc document :weight-lb (* value 2.2046))
            :else nil))
        (fn [[id] value {:keys [height weight] :as document}]
          (when (and (some #{id} [:height :weight]) weight height)
            (assoc document :bmi (/ weight (* height height)))))]

       [:button.btn.btn-default
         {:on-click
          #(if (empty? (get-in @doc [:person :first-name]))
             (swap! doc assoc-in [:errors :first-name]"first name is empty"))}
         "save"]

       [:hr]
       [:h1 "Document State"]
       [edn->hiccup @doc]])))
