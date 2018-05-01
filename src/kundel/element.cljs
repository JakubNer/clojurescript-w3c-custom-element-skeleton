(ns kundel.element
  (:require
    [goog.object :as go]
    [reagent.core :as r]
    [kundel.component :as c]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; w3c custom element registration and callback handlers.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Registration occurs by calling the exported 'register' below.

;; The registered element will have the following name:
(def element-name "my-element")

;; The registered element works with the following attributes.
;;
;; NOTE:
;;   A reagent atom is created for each of the component's attributes.
;;
;;   The reagent component render function gets 'element' and 'attrs'
;;   properties.  The 'attrs' property contains the ratom with these
;;   element properties.  To read the value in the reagent component:
;;
;;      @(get attrs "some-text")
;;
;;   Note that each 'attrs' has to have a corresponding 'fns' entry below.
;;
;; Modify these to suite your element:
(def attrs {"some-text" (r/atom nil)})

;; Custom translation functions for each attribute.
;; %1 is original property value, %2 is the new value.
;; Examples:
;;    #(do %2)              ;; just replaces old value.
;;    #(.parse js/JSON %2)  ;; par33ses JSON into JS object
;;    #(= "true" %2)        ;; parses boolean
;; This list's keys must match the 'attrs' list.
(def fns {"some-text" #(do %2)})

;; events:  "goto" :: event detail is page title to go to.




;; NO NEED TO MODIFY ANYTHING BELOW

(defn ^:export created [this]
  (doseq [keyval attrs]
    (swap! (val keyval) (get fns (key keyval)) (.getAttribute this (key keyval))))
  (r/render [c/render this attrs] this))      ;; attach reagent component

(defn attached [this]) ;; not wired into reagent component

(defn detached [this]) ;; not wired into reagent component

(defn ^:export changed [this property-name old-value new-value]
  (swap! (get attrs property-name) (get fns property-name) (.getAttribute this property-name)))

;; register the w3c custom element.
(defn ^:export register []
  (when (.-registerElement js/document)
    (let [proto (.create js/Object (.-prototype js/HTMLElement))
          proto' (go/create "createdCallback" #(this-as this (created this))
                            "attachedCallback" #(this-as this (attached this))
                            "detachedCallback" #(this-as this (detached this))
                            "attributeChangedCallback" #(this-as this (changed this %1 %2 %3)))]
      (go/extend proto proto')
      (.registerElement js/document element-name #js{"prototype" proto}))))
