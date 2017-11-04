# ================================ #
# Walkthrough of connected example #
# ================================ #

This document is an interactive document. It is a walkthrough of the code
originally used for the mnist-classification example. The code can be
evaluated one at the time throughout the document. And you can change
whatever you want and evaluate that.

# Using this document

To use this document as intended, it should be started using the command
"lein liq" in the examples/mnist-classification folder.

Use arrow keys to navigate, or keys "i", "j", "k", "l" (when the cursor is blue).

Use TAB to switch between navigation mode (blue cursor) and text mode (green
cursor).

Find more keybindings here: https://github.com/mogenslund/liquid/wiki/Cheat-Sheet

To evaluate an expression use "e" key in navigation mode. Praktice by evaluting
the sample expressions below (Type "1" to hightlight the s-expression that will
be valuted and type "1" again to remove the highlight.):

    (range 10) ; Move cursor inside parenthesis an click "e". Observe the output
               ; in the -prompt- window

    (p (map #(* % %) (range 10))) ; Try pressing "e" on "range" or on "p".

    (editor/end-of-line) ; This is an action doing something to the editor itself.

Notice, what is evaluated, depends on the position of the cursor. In some sense
it is the smallest complete s-expression containing the cursor.
So to load a function into memory press "e" while on "defn" or the name of the
function.

:WARNING: Read the instructions carefully, some commands in the document can take
          some time and need to finish, before continuing.

    ;; List of elements like {:class 1, :data (0 1 0 1 1 0 0 1 1 0 1 0 1 1 1 1)}
    (def dataset
      (map #(hash-map :labels (if (= (nth % 0) "0") [1.0 0.0] [0.0 1.0]) 
                      :data (apply vector (for [s (apply str (rest %))] (if (= s \0) 0.0 1.0))))
           (partition 6 (str/split-lines (slurp "connected.data")))))

    (count dataset)
    (doseq [l dataset] (println l))

    (def train-set (take 250 dataset))
    (def test-set (drop 250 dataset))


/home/mogens/proj/cortex/src/cortex/nn/layers.clj

    (def initial-description
      [(layers/input 4 4 1 :id :data) ; size 4x4, output 1
       (layers/convolutional 2 0 1 20)
       (layers/max-pooling 2 0 2)
       (layers/dropout 0.9)
       (layers/relu)
       (layers/convolutional 2 0 1 20)
       (layers/max-pooling 2 0 2)
       (layers/batch-normalization)
       (layers/linear 50)
       (layers/relu :center-loss {:label-indexes {:stream :labels}
                                  :label-inverse-counts {:stream :labels}
                                  :labels {:stream :labels}
                                  :alpha 0.9
                                  :lambda 1e-4})
       (layers/dropout 0.5)
       (layers/linear 2) ; 2 classes 0 and 1
       (layers/softmax :id :labels)])

(def network (network/linear-network initial-description))

/home/mogens/proj/cortex/src/cortex/graph.clj

(first train-set)

(def network 
  (execute/train
    network
    train-set
  ))


(execute/run network test-set)



?????????????????????????
?????????????????????????
?????????????????????????
(first (first aaa)) -> {:data [#object["[F" 0x5beff4b0 "[F@5beff4b0"]], :labels [0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 1.0 0.0]}


                          (experiment-util/create-dataset-from-folder class-mapping
                                                                      :image-aug-fn (:image-aug-fn train-input))


(defn- train-forever
  "Train forever. This function never returns."
  [initial-description train-ds test-ds
   train-args]
  (let [network (network/linear-network initial-description)]
    (apply (partial experiment-train/train-n network
                    train-ds test-ds)
           (-> train-args seq flatten))))


(defn perform-experiment
  "Main entry point:
    - initial-description: A cortex neural net description to train.
    - train-ds: A dataset (sequence of maps) with keys `:data`, `:labels`, used for training.
    - test-ds: A dataset (sequence of maps) with keys `:data`, `:labels`, used for testing.
    - listener: a function which takes 3 arguments: initial-description, train-ds and test-ds
              and returns a function that is executed per epoch. It could be used
              to evaluate status of training (e.g. for early stopping) or to save the network.
    - train-args: a map of optional arguments such as a force-gpu?. See cortex.experiment.train/train-n for the full list of arguments
  "
  ([initial-description train-ds test-ds listener]
   (perform-experiment initial-description train-ds test-ds listener {}))
  ([initial-description train-ds test-ds listener train-args]
   (let [test-fn (listener initial-description train-ds test-ds)]
     (train-forever initial-description train-ds test-ds
                    (assoc train-args :test-fn test-fn)))))