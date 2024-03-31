^{:kindly/hide-code true
  :kindly/kind :kind/hiccup}
[:table
 [:tr
  [:td "This is part of the Scicloj "
   [:a {:href "https://scicloj.github.io/clojure-data-scrapbook/"}
    "Clojure Data Scrapbook"]
   "."]
  [:a
   {:href "https://scicloj.github.io/clojure-data-scrapbook/"}
   [:img {:src "https://scicloj.github.io/sci-cloj-logo-transparent.png"
          :alt "SciCloj logo"
          :width "40"
          :align "left"}]]]]

;; # Noj - getting started - from raw data to a blog post

;; This is a getting-started tutorial for [Noj](https://scicloj.github.io/noj/) (scinojure) - a recommended way to use the emerging Clojure data stack.

;; It is considered part of the [Clojure Data Scrapbook](https://scicloj.github.io/clojure-data-scrapbook/), but is maintained as a separate repo to serve as a self-contained small example of a data-analysis report.

;; Video tutorial:

^{:kindly/hide-code true
  :kindly/kind :kind/video}
{:youtube-id "5GluhUmMlpM"}


;; ## Question

;; Can we distinguish weekends from weekdays
;; in terms of the hours in which people tend
;; to use their bikes?

;; ## Setup

(ns index
  (:require [tablecloth.api :as tc]
            [tech.v3.datatype.datetime :as datetime]
            [scicloj.noj.v1.vis.hanami :as hanami]
            [aerial.hanami.templates :as ht]
            [scicloj.kindly.v4.kind :as kind]))

;; ## Reading data

;; You may learn more about the [Cyclistic Bike Share 2023](https://www.kaggle.com/datasets/godofoutcasts/cyclistic-bike-share-2023) dataset in our [Chicago bike trips](https://scicloj.github.io/clojure-data-scrapbook/projects/geography/chicago-bikes/index.html) tutorial.

(defonce raw-trips
  (-> "data/202304_divvy_tripdata.csv.gz"
      (tc/dataset {:key-fn keyword
                   :parser-fn {"started_at"
                               [:local-date-time
                                "yyyy-MM-dd HH:mm:ss"]
                               "ended_at"
                               [:local-date-time
                                "yyyy-MM-dd HH:mm:ss"]}})))

;; ## Processing data

(def processed-trips
  (-> raw-trips
      (tc/add-columns {:hour (fn [ds]
                               (->> ds
                                    :started_at
                                    (datetime/long-temporal-field
                                     :hours)))
                       :day-of-week (fn [ds]
                                      (->> ds
                                           :started_at
                                           (datetime/long-temporal-field
                                            :day-of-week)))})))

;; ## Analysis

(def hours-plot
  (-> processed-trips
      (tc/group-by [:hour])
      (tc/aggregate {:n tc/row-count})
      (tc/order-by [:hour])
      (hanami/plot ht/bar-chart
                   {:X "hour"
                    :Y "n"})))

hours-plot

(kind/pprint hours-plot)

(kind/portal hours-plot)

(-> processed-trips
    (tc/group-by [:day-of-week :hour])
    (tc/aggregate {:n tc/row-count})
    (tc/group-by [:day-of-week])
    (hanami/plot ht/bar-chart
                 {:X "hour"
                  :Y "n"})
    (tc/order-by [:day-of-week]))

;; ## Conclusion

;; Yes. Weekends are different from
;; weekdays in terms of the hours
;; in which people tend to use
;; their bikes.
