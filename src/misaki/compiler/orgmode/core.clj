(ns misaki.compiler.orgmode.core
  "Emacs Org mode Compiler for Misaki"
  (:require
   [misaki.compiler.orgmode.template :refer :all]
   [misaki.util [file     :refer :all]
    [date     :refer :all]
    [string   :refer :all]
    [sequence :refer [get-prev-next sort-alphabetically]]]
   [misaki [config :refer [*config* *base-dir*] :as cnf]
    [core   :as msk]
    [config :as mskcfg]
    [server :as srv]]
   [clojure.string :as str]
   [clj-time.core  :refer [date-time]]
   )
  (:import [java.io File]))

(declare make-base-site-data)

(def DEFAULT_LAYOUT_DIR
  "Default layout directory."
  "layouts/")

(defn layout-file?
  "Check whether specified file is layout file or not."
  [file]
  (if-let [layout-dir (:layout-dir *config*)]
    (str-contains? (.getAbsolutePath file) layout-dir)
    false))

(defn- string->date
  "Convert String to org.joda.time.DateTime."
  [#^String date-string]
  {:pre [(string? date-string)]}
  (let [date-seq (nfirst (re-seq #"(\d{4})[-/](\d{1,2})[-/](\d{1,2})$" date-string))]
    (if (and date-seq (= 3 (count date-seq))
             (every? #(re-matches #"^[0-9]+$" %) date-seq))
      (apply date-time (map #(Integer/parseInt %) date-seq)))))

(defn- org-extension->html-extension
  "Convert orgmode extension(*.org) to html extension(*.html)."
  [s]
  (if (has-extension? :org s)
    (str (remove-last-extension s) ".html")
    s))

(defn- make-post-output-filename
  "Make post output filename from java.io.File."
  [#^File file option]
  {:pre [(file? file)]}
  (let [filename (.getName file)
        option-filename (:output option)
        post-date   (string->date (:date option)) 
        config-filename (render (:post-filename-format *config*)
                                {:year     (year post-date)
                                 :month    (month post-date)
                                 :day      (day post-date)
                                 :filename filename})
        output-name (if option-filename option-filename config-filename)]
    output-name))

(defn make-post-output-url
  "Make output url from java.io.File."
  [#^File file option]
  {:pre [(file? file)]}
  (path (:url-base *config*) (make-post-output-filename file option)))

(def ^{:private true} make-url
  "Make output url from java.io.File."
  (comp org-extension->html-extension cnf/make-output-url))

(def ^{:private true} make-post-url
  "Make output url from java.io.File."
  (comp org-extension->html-extension make-post-output-url))

(def ^{:private true} make-filename
  "Make output filename from java.io.File."
  (comp org-extension->html-extension cnf/make-output-filename))

(def ^{:private true} make-post-filename
  "Make output filename from java.io.File."
  (comp org-extension->html-extension make-post-output-filename))

(defn- load-extension-files
  "Load cuma extension files."
  [file]
  (cond
   (string? file)     (load-file (path *base-dir* file))
   (sequential? file) (doseq [f file] (load-extension-files f))))

(defn get-post-data
  "Get posts data."
  [& {:keys [all?] :or {all? false}}]
  (let [site (make-base-site-data :ignore-post? true)]
    (map #(let [slurp-data (slurp %)
                option (get-template-option slurp-data)
                date (string->date (:date option))]
            (assoc option 
              :file %
              :date date
              ;; :content (render-template % (merge (:site *config*) site)
              ;;                           :allow-layout? false
              ;;                           :skip-runtime-exception? true)
              :url (make-post-url % option)))
         (msk/get-post-files :sort? false :all? all?))))

(defn get-all-tags
  "Get all(unfiltered) tags from post list.
  Return nil if :post-dir option is nil."
  []
  (->> (msk/get-post-files :all? true)
       (mapcat (comp :tags get-template-option slurp))
       (remove nil?)))

(defn get-tags
  "Get tags from post list.

  Add counting by tag name if true is setted to :count-by-name?"
  [& {:keys [count-by-name?] :or {count-by-name? false}}]
  (let [tags (get-all-tags)
        tags (if count-by-name?
               (map #(assoc (first %) :count (count %)) (vals (group-by :name tags)))
               tags)]
    (distinct (sort-alphabetically :name tags))))

(defn post-info-contains-tag?
  "Check whether post information contains tag or not."
  [post-info #^String tag-str]
  {:pre [(map? post-info) (or (nil? tag-str) (string? tag-str))]}
  (let [tags      (get post-info :tags [])
        tag-names (set (map :name tags))]
    (contains? tag-names tag-str)))

(defn get-tagged-post-data
  "Get tagged posts data from (:post-dir *config*) directory."
  [tag-name]
  {:pre [(string? tag-name)]}
  (sort-alphabetically :title
   (filter
    #(post-info-contains-tag? % tag-name)
    (get-post-data :all? true))))

(defn make-arichves
  "Make post list for archive pages."
  [all-posts]
  (let [post-group (group-by #(year (:date %)) all-posts)]
    (for [year (keys post-group)]
      {:year year, :posts (get post-group year)})))

(defn make-base-site-data
  "Make base site data for rendering templates."
  [& {:keys [ignore-post? tag-name]
      :or   {ignore-post? false, tag-name nil}}]
  (let [site (:site *config*)
        date (now)
        with-tag? (not (nil? tag-name))
        all-posts (if-not ignore-post? (get-post-data :all? true))
        all-posts (reverse (sort-by :date all-posts))
        posts (mskcfg/get-page-posts all-posts)
        ]
    (merge site
           {;;:date      (date->string date)
            :root      (:url-base *config*)
            :next-page (:next-page *config*)
            :prev-page (:prev-page *config*)
            :posts     posts ;; page-posts per all-posts
            :all-posts all-posts
            :all-tags  (get-tags :count-by-name? true)
            :recent-posts (take (:recent-posts-num *config*) all-posts)
            :tag-posts (if-not ignore-post? (if with-tag? (get-tagged-post-data tag-name)))
            :tag-name  tag-name
            :archives  (make-arichves all-posts) 
            :date-xml-schema (date->xml-schema date)
            :now-date  (now)})))

(defn output-tag-file
  [tag-name]
  (let [tag-file-name (str (:public-dir *config*) (make-tag-output-filename tag-name))
        base-data (make-base-site-data :tag-name tag-name)
        body (render-tag-template base-data)]
    (write-file tag-file-name body)))

(defn -extension
  "Specifying file extensions function called by misaki.core."
  []
  (list :htm :html :org :xml))

(defn -config
  "Custom configuration function called by misaki.core."
  [{:keys [template-dir] :as config}]

  ;; load extension
  (when-let [ext-file (some-> config :cuma :extension)]
    (load-extension-files ext-file))

  (assoc config
    :layout-dir       (path template-dir (:layout-dir config DEFAULT_LAYOUT_DIR))))

(defn print-map
  [coll]
  (println "print-map")
  (doseq [elm coll]
    (println ";;;;;;;;\n" elm)))

(defn -compile
  "Compile function called by misaki.core."
  [config file]
  (println "   Compiling... "(.getAbsolutePath file))
  (binding [*config* config]
    (cond
     ;; layout template
     (layout-file? file)
     {:status 'skip, :all-compile? true}

     ;; post template
     (cnf/post-file? file)
     (let [site        (make-base-site-data)
           option      (get-template-option (slurp file))
           tags        (:tags option)
           post-date   (string->date (:date option)) 
           [prev next] (get-prev-next #(= file (:file %)) (:all-posts site))
           site        (assoc site :prev prev :next next :post-date post-date)
           res (render-template file site)]
       (if (> (count tags) 0)
         (doseq [{tag-name :name} tags]
           (output-tag-file tag-name)))
       ;; compile neighbor
       (when (= :single (:-compiling config))
         (if prev (msk/compile* {:-compiling :neighbor} (:file prev)))
         (if next (msk/compile* {:-compiling :neighbor} (:file next))))
       {:status true, :filename (make-post-filename file option)
        :body res})

     ;; other templates
     :else
     (let [site (make-base-site-data)]
       ;;(print-map site)
       {:status true, :filename (make-filename file)
        :body (render-template file site)}))))

(defn -main [& args]
  (apply srv/-main args))
