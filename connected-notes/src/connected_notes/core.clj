(ns connected-notes.core
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [cortex.datasets.mnist :as mnist]
            [mikera.image.core :as i]
            [think.image.image :as image]
            [think.image.patch :as patch]
            [think.image.data-augmentation :as image-aug]
            [cortex.nn.layers :as layers]
            [clojure.core.matrix.macros :refer [c-for]]
            [clojure.core.matrix :as m]
            [cortex.experiment.classification :as classification]
            [cortex.experiment.train :as train]
            [cortex.nn.network :as network]
            [cortex.nn.execute :as execute]
            [cortex.util :as util]
            [cortex.experiment.util :as experiment-util])
  (:import [java.io File]))




(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(defn generate-data
  []
  (doseq [x (range 1000)]
    (println
      (str "-\n"
           (rand-int 2) (rand-int 2) (rand-int 2) (rand-int 2) "\n"
           (rand-int 2) (rand-int 2) (rand-int 2) (rand-int 2) "\n"
           (rand-int 2) (rand-int 2) (rand-int 2) (rand-int 2) "\n"
           (rand-int 2) (rand-int 2) (rand-int 2) (rand-int 2) "\n"))))



   (def dataset
      (map #(hash-map :labels (if (= (nth % 0) "0") [1.0 0.0] [0.0 1.0]) 
                      :data (for [s (apply str (rest %))] (if (= s \0) 0 1)))
           (partition 6 (str/split-lines (slurp "connected.data")))))

    (count dataset)
    (doseq [l dataset] (println l))

    (def train-set (take 250 dataset))
    (def test-set (drop 250 dataset))


    (def initial-description
      [(layers/input 4 4 1 :id :data) ; size 4x4, output 1
       (layers/convolutional 2 0 1 20)
       (layers/max-pooling 2 0 2)
       ;(layers/dropout 0.9)
       ;(layers/relu)
       ;(layers/convolutional 3 0 1 50)
       (layers/max-pooling 2 0 2)
       (layers/batch-normalization)
       (layers/linear 50)
       (layers/relu :center-loss {:label-indexes {:stream :labels}
                                  :label-inverse-counts {:stream :labels}
                                  :labels {:stream :labels}
                                  :alpha 0.9
                                  :lambda 1e-4})
       ;(layers/dropout 0.5)
       (layers/linear 2) ; 2 classes 0 and 1
       (layers/softmax :id :labels)])

(def network (network/linear-network initial-description))

(execute/train
  network
  train-set
;  :batch-size 4
;  :datatype :float
;  :batch-transfer-parallelism 1
)
