(ns irrelevant.airplane)

(def myList (list 1 2 4 7 89 12 34 45 56))

(def N-THREADS 4)
(def element_processing_retries (atom 0))

(def list-collection
  "Each element is made into a ref"
  (map ref myList))

(defn partition-list  [threads list]
  "partition list into required number of partitions which is equal
  to the number of threads"
  (let [partitions (partition-all
                     (Math/ceil (/ (count list) threads))  list)]
    partitions))
(defn increase-element [element]
  (ref-set element inc))

(defn process-list [list]
 "Process `members of list` one by one."
 (doseq [sub-list (partition-list N-THREADS list)]
   (future (doseq [element sub-list]
             #_(Do stuff with element)
             (Thread/sleep 1000)))))


(defn main []
  (let [f1 (future (println "Returned!" (process-list myList)))]
    (time @f1)))
