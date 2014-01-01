{
 ;; directory setting
 ;;   defailed information: http://liquidz.github.io/misaki/toc/02-directory-structure.html
 :public-dir   "public/"
 :tag-out-dir  "tag/" ;; => {public-dir}/{tag-outdir}/{tag}.html
 :template-dir "template/"
 :post-dir     "posts/"
 :layout-dir   "layouts/"
 :tag-layout   "tag" ;; => tag tamplate. {layout-dir}/{tag-layout}.html
 :post-filename-regexp #"(\d{4})-(\d{1,2})-(\d{1,2})[-_](.+)$"
 :post-filename-format "$(year)-$(month)/$(filename)"
 :compile-with-post ["index.html"]
 ;;:url-base "/misaki-orgmode/"
 :url-base "/"
 :posts-per-page 2
 :emacs "/usr/local/bin/emacs"

 :site {:site-title "misaki orgmode"
        :site-subtitle "Misaki orgmode is static blog generator for org-mode based on Misaki."
        :your-domain "mikio.github.io"
        :atom       "atom.xml"
        :atom-base  "http://localhost:8080"
        :twitter-id "mikio_kun"
        :disqus-id  "foofoomikiokunblog"
        :recent-posts-num 2
        :local {:css ["css/main.css"]
                :js  ["js/highlight.pack.js"
                      "js/main.js"]}
        :remote {:css ["http://fonts.googleapis.com/css?family=Josefin+Sans"
                       "http://yandex.st/highlightjs/7.3/styles/github.min.css"]}}

 ;; misaki-orgmode configuration
 ;; cuma extension file
 :cuma {:extension "extension.clj"}

 ;;:compiler ["orgmode" "copy"] 
 :compiler ["orgmode"] 
 }

