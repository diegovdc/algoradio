(ns algoradio.freesound
  (:require ["axios" :as axios]
            [algoradio.axios]
            [clojure.string :as str]))

(defn get-query-params [url]
  (some-> (try (js/URL. url) (catch js/TypeError e nil))
          .-searchParams
          .toString
          (str/split "&")
          (->> (map #(str/split % "="))
               (into {}))))

(defn response->data [response]
  (let [data (-> response
                 (js->clj :keywordize-keys true)
                 :data)]
    data))

(defn get-results-and-next-page
  [data]
  (let [params (get-query-params (data :next))]
    (js/console.log (data :next))
    {:results (map (fn [sound]
                     (let [mp3 (-> sound :previews :preview-hq-mp3)]
                       (-> sound
                           (assoc :mp3 mp3)
                           (dissoc :previews))))
                   (data :results))
     :next-page (if-not params
                  :done
                  (js/Number (get params "page")))}))

(defn get-audios! [app-state query]
;;; TODO notify user when there are more audios to load from a given query
  (let [query* (-> query
                   (str/split " ")
                   (->> (remove empty?)
                        (str/join "+")))
        page (get-in @app-state [:freesounds-pages query] 1)]
    (when (not= :done page)
      (-> (axios/get
           (str "http://localhost:3000/data?query=" query* "&page=" page))
          (.then (fn [res]
                   (let [{:keys [results next-page]}
                         (-> res
                             algoradio.axios/response->data
                             (get-results-and-next-page))]
                     (js/console.log results)
                     (swap! app-state
                            assoc-in
                            [:freesounds-pages query]
                            next-page)
                     (swap! app-state
                            update-in
                            [:freesounds query]
                            concat results))))
          (.then js/console.log )
          (.catch js/console.log)))))
