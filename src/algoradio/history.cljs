(ns algoradio.history)

(defn add-play! [app-state sound audio-id]
  (swap! app-state update ::history conj
         (assoc sound
                :event_type :play
                :audio_id audio-id
                :timestamp (js/Date.now))))

(defn add-stop! [app-state audio-id type]
  (swap! app-state update ::history conj
         {:event_type :stop
          :audio_id audio-id
          :type type
          :timestamp (js/Date.now)}))

(defn add-volume-change! [app-state audio-id volume-level]
  (swap! app-state update ::history conj
         {:event_type :volume
          :audio_id audio-id
          :volume_level volume-level
          :timestamp (js/Date.now)}))

(defn prepare-history
  "Order history by timestamp, ascending,
  and add `:interval` between each event"
  [history]
  (let [history* (reverse history)
        initial-time (:timestamp (first history*))]
    (:history
     (reduce (fn [{:keys [history prev-time]} event]
               (let [current-time (event :timestamp)
                     interval (- current-time prev-time)]
                 {:history (conj history (assoc event :interval interval))
                  :prev-time current-time}))
             {:history [] :prev-time initial-time}
             history*))))

(defn get-history! [app-state]
  (#'prepare-history (get @app-state ::history)))