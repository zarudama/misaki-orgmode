;; USAGE:
;; $ cat foo.org | emacs --script org-export.el
;; for debug
;; $ emacs -Q --debug-init -l org-export.el

;; もし8.xのorgを使用したいのなら、ここで8.xへのload-pathを通す。
;;(add-to-list 'load-path "~/.emacs.d/.cask/24.3.1/elpa/org-plus-contrib-20140619/")
;;

(define-key global-map (kbd "C-h") 'delete-backward-char)
(define-key global-map (kbd "M-C-h") 'backward-kill-word)
(define-key global-map (kbd "C-j") nil)

(require 'org)
;;(require 'org-install)
;;(require 'org-html)

;;;
;;; begin custmoize org-mode
;;;

;;; add a pathof major-mode of each language after having put path to htmlise through code if You want coloring.
;;(add-to-list 'load-path "~/Dropbox/dotfiles/emacs/package/htmlize-20130207.2102/")
;;(add-to-list 'load-path "~/Dropbox/dotfiles/emacs/package/clojure-mode-20131222.444/")
;;(require 'clojure-mode)
;;(require 'java-mode)

(add-to-list 'load-path "./")
(require 'htmlize)
;; for 7.9 with htmlize.el
;;(setq htmlize-output-typ 'inline-css)
(setq htmlize-output-type 'css)

;;(setq org-html-htmlize-output-type 'inline-css) 
(setq org-html-htmlize-output-type 'css)
;;(setq org-export-htmlize-output-type 'css)

;; 8.2にはない
;; (setq org-export-allow-bind-keywords t)
;; (setq org-export-with-sub-superscripts nil)

(setq org-src-fontify-natively t)


(setq org-export-with-toc nil)

;; コードブロックも色付け
(setq org-src-fontify-natively t)

(global-font-lock-mode t)
;;(turn-on-font-lock)
;;(font-lock-mode 1)

(setq org-export-headline-levels 2)

(setq org-export-default-language "en")
(setq org-export-html-coding-system 'utf-8)
(setq org-export-with-fixed-width nil)
;;; no use "^" and "_"
(setq org-export-with-sub-superscripts nil)
;;; just output "--" and "---"
(setq org-export-with-special-strings nil)
;;; no parse code of "TeX" and "LaTeX"
(setq org-export-with-TeX-macros nil)
(setq org-export-with-LaTeX-fragments nil)


;;;
;;; end of custmoize org-mode
;;;

;; (find-file my-file-name)
;; ;;(princ (org-export-as-html-batch )) ;; no exist org-ver8.0
;; (princ (org-html-export-as-html nil nil nil t))
(with-temp-buffer
  (condition-case nil
      (let (line)
        (while (setq line (read-string ""))
          (insert line "\n"))
        )
    (error nil))

  (message (org-version))
  (if (string-match "8\\." (org-version))
      (progn
       (message "8.x")

       ;; ver.8.x
       ;; (org-html-export-as-html &optional ASYNC SUBTREEP VISIBLE-ONLY BODY-ONLY EXT-PLIST)
       (org-html-export-as-html nil nil nil t))

    ;; ver.7.9.3f
    ;; (org-export-as-html ARG &optional EXT-PLIST TO-BUFFER BODY-ONLY PUB-DIR)
    ;; Export the outline as a pretty HTML file.
    ;; If there is an active region, export only the region.
    ;; The prefix ARG specifies how many levels of the outline should become
    ;; headlines.  The default is 3.  Lower levels will become bulleted lists.
    ;; EXT-PLIST is a property list with external parameters overriding
    ;; org-mode's default settings, but still inferior to file-local settings.
    ;; When TO-BUFFER is non-nil, create a buffer with that name and export to that buffer.
    ;; If TO-BUFFER is the symbol `string', don't leave any buffer behind but just return the
    ;; resulting HTML as a string.
    ;; When BODY-ONLY is set, don't produce the file header and footer, simply return the content of
    ;; <body>...</body>, without even the body tags themselves.
    ;; When PUB-DIR is set, use this as the publishing directory.
    (progn
      (message "not 8.x")
      (org-export-as-html 3 nil (current-buffer) t nil)
      (message "aaaa")
      )) 
  
  (princ (buffer-string)))
