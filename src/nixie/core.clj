(ns nixie.core
  (:gen-class))

(use '[clojure.java.shell :only [sh]])

(defn set-working-root [dir]
  (def working-root dir))

(defn init [repo dir]
  (sh "git" "clone" "-n" repo)
  (sh "git" "reset" "HEAD" :dir dir))

(defn checkout [file dir]
  (sh "git" "checkout" file :dir dir))

(defn temp-delete [file dir]
  (sh "git" "update-index" "--assume-unchanged" file :dir dir)
  (sh "delete" file :dir dir))

(defn tree-hash [hash dir]
  (subs (:out (sh "git" "cat-file" "-p" hash :dir dir)) 5 45))

(defn get-hash-from-file [file]
  (subs (slurp file) 0 40))

(defn master-hash [dir]
  (subs (:out (sh "cat" ".git\\refs\\heads\\master" :dir dir)) 0 40))

(defn list-file [hash dir]
  (:out (sh "git" "cat-file" "-p" hash :dir dir)))

(defn split-line [line]
  (clojure.string/split line #"[\s\t]"))

(defn list-tree [dir]
  (map split-line (clojure.string/split (list-file (tree-hash (master-hash dir) dir) dir) #"\n")))

(defrecord File-Entry [type hash name])

(defn store-to-file-entry [line]
  (->File-Entry (nth line 1) (nth line 2) (nth line 3)))

(defn file-entries [dir]
  (map store-to-file-entry (list-tree dir)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (sh "test.bat"))


