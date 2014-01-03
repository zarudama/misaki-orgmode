{
 ;; directory setting
 ;;   defailed information: http://liquidz.github.io/misaki/toc/02-directory-structure.html
 :public-dir   "public/"
 :tag-out-dir  "tag/" ;; => {public-dir}/{tag-outdir}/{tag}.html
 :template-dir "template/"
 :post-dir     "posts/"
 :layout-dir   "layouts/"
 :tag-layout   "tag" ;; => tag tamplate. {layout-dir}/{tag-layout}.html
 :post-filename-regexp #"^.+$"
 :post-filename-format "$(year)-$(month)/$(filename)"
 ;;:post-filename-format "article/$(year)/$(month)/$(day)_$(filename)" ;; o-blog format
 :compile-with-post ["index.html" "archives.html" "atom.xml"]
 ;;:url-base "/misaki-orgmode/"
 :url-base "/"
 :posts-per-page 2
 :recent-posts-num 2
 :emacs "/usr/local/bin/emacs"
 :lang "en"

 :site {:site-title    "Misaki orgmode"
        :site-subtitle "Misaki orgmode is static blog generator for org-mode based on Misaki."
        :your-domain   "mikio.github.io"
        :atom          "atom.xml"
        :atom-base     "http://localhost:8080"
        :twitter-id    "mikio_kun"
        :disqus-id     "foomikiokunblog"
        :local {:css   ["css/main.css"]
                :js    ["js/highlight.pack.js"
                        "js/main.js"]}
        :remote {:css ["http://fonts.googleapis.com/css?family=Josefin+Sans"
                       "http://yandex.st/highlightjs/7.3/styles/github.min.css"]}}

 ;; misaki-orgmode configuration
 ;; cuma extension file
 :cuma {:extension "extension.clj"}

 :compiler ["orgmode"] 
 }

