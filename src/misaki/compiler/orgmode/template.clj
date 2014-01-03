(ns misaki.compiler.orgmode.template
  "Orgmode Template Parser for Misaki"
  (:require
   [misaki.util [file   :refer :all]
    [date   :refer :all]
    [string :refer :all]]
   [misaki.config       :refer [*config*]]
   [cuma.core           :as cuma]
   [clojure.string      :as str]
   [clojure.java.shell  :as shell]
   ))

(defn make-tag-output-filename
  "Make tag output filename from tag name."
  [#^String tag-name]
  {:pre [(string? tag-name)]}
  (path (:tag-out-dir *config*) (str tag-name ".html")))

(defn- make-tag-url
  "Make tag url form tag string."
  [#^String tag-name]
  {:pre [(string? tag-name)]}

  (path (:url-base *config*)
        (make-tag-output-filename tag-name)))

(defn- parse-tag-string
  "Parse tag string to tag list.

      \"aa,bb\"
      ;=> [{:name \"aa\", :url TAG-PAGE-URL}
      ;    {:name \"bb\", :url TAG_PAGE-URL}]
  "
  [#^String tags]
  (if (or (nil? tags) (str/blank? tags)) ()
      (for [tag (distinct (str/split tags #"[\s\t,]+"))]
        {:name tag
         :url  (make-tag-url tag)})))

(defn parse-option-line
  [line]
  (re-seq #"^[;#]+\s*@([\w?]+)\s+(.+)$" line))

(defn get-template-option
  "Get template option from slurped template file."
  [slurped-data]
  (if (string? slurped-data)
    (let [lines  (map str/trimr (str/split-lines slurped-data))
          params (remove nil? (map parse-option-line lines))
          option (into {} (for [[[_ k v]] params] [(keyword k) v]))]
      (assoc option :tags (-> option :tags parse-tag-string)))
    {}))

(defn remove-option-lines
  "Remove option lines from slurped template file."
  [slurped-data]
  (let [lines  (str/split-lines slurped-data)       ;(map str/trim (str/split-lines slurped-data))
        ]
    (str/join "\n" (remove #(parse-option-line %) lines))))

(defn remove-useless-html-lines
  "Remove useless empty lines in HTML."
  [s]
  (-> s
      (str/replace #"(<[^/]+?>)[\r\n]*" (fn [[_ tag]] tag))
      (str/replace #"[\r\n]*(</.+?>)" (fn [[_ tag]] tag))))

(defn load-layout
  "Load layout file and return slurped data."
  [layout-name]
  (slurp (path (:layout-dir *config*) (str layout-name ".html"))))

;; @layoutで指定されているlayoutファイルをそれぞれ読み込んでseq(["tmpl-string" {tmpl-option}]...)を返す。
(defn get-templates
  "Get slurped template file containing layout files."
  [slurped-data]
  (letfn [(split-option-body [s] ((juxt remove-option-lines get-template-option) s))]
    (take-while
     #(not (nil? %))
     (iterate (fn [[_ tmpl-option]]
                (if-let [layout-name (:layout tmpl-option)]
                  (split-option-body (load-layout layout-name))))
              (split-option-body slurped-data)))))

(defn- read-org [body]
  "convert org-mode-file into html.
   From standard input to standard output."
  (let [content (:out (shell/sh
                       (:emacs *config*)
                       "--script"
                       "org-export.el"
                       :in body))]
    content))

(defn- print-map
  [coll]
  (println "print-map")
  (doseq [elm coll]
    (println ";;;;;;;;\n" elm)))

;; orgmode-flag?( ) => orgmode
;; orgmode-flag?(T) => orgmode
;; orgmode-flag?(F) => html
(defn- render* [[body option :as template] data]
  (let [md-flag (:orgmode? option "noopt")
        orgmode-process? (if (= "false" md-flag) false true)]
    (if orgmode-process?
      (do
        ;;(print-map data)
        (cuma/render (read-org body) data))
      (cuma/render body data))))

(defn render-tag-template
  [base-data & {:keys [allow-layout? skip-runtime-exception?]
                :or   {allow-layout? true, skip-runtime-exception? false}}]
  (let [tmpls (get-templates (load-layout (:tag-layout *config*)))
        htmls (map first tmpls)
        data  (merge base-data (reduce merge (reverse (map second tmpls))))]
    (if allow-layout?
      (reduce
       (fn [result-html tmpl-html]
         (if tmpl-html
           (render* tmpl-html (merge data {:content (str/trim result-html)}))
           result-html))
       (render* (first tmpls) data)
       (rest tmpls))
      (render* (first tmpls) data))))

(defn render-template
  "Render java.io.File as HTML."
  [file base-data & {:keys [allow-layout? skip-runtime-exception?]
                     :or   {allow-layout? true, skip-runtime-exception? false}}]
  (let [tmpls (get-templates (slurp file)) 
        htmls (map first tmpls)
        data  (merge base-data (reduce merge (reverse (map second tmpls))))]
    (if allow-layout?
      (reduce
       (fn [result-html tmpl-html]
         (if tmpl-html
           (render* tmpl-html (merge data {:content (str/trim result-html)}))
           result-html))
       (render* (first tmpls) data)
       (rest tmpls))
      (render* (first tmpls) data))))

