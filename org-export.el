;; USAGE:
;; $ cat foo.org | emacs --script org-export.el

;; (setq my-file-name (car argv))
;; (setq argv nil)

(add-to-list 'load-path "~/Dropbox/site-lisp/org-8.0/lisp")
(add-to-list 'load-path "~/Dropbox/site-lisp/org-8.0/contrib/lisp" t)
(require 'org)

;;;
;;; begin custmoize org-mode
;;;

;;; add a pathof major-mode of each language after having put path to htmlise through code if You want coloring.
(add-to-list 'load-path "~/Dropbox/dotfiles/emacs/package/htmlize-20130207.2102/")
(add-to-list 'load-path "~/Dropbox/dotfiles/emacs/package/clojure-mode-20131222.444/")
(require 'htmlize)

(setq org-src-fontify-natively t)
(setq org-export-headline-levels 2)

(setq org-export-default-language "ja")
(setq org-export-html-coding-system 'utf-8)
(setq org-export-with-fixed-width nil)
;;; no use "^" and "_"
(setq org-export-with-sub-superscripts nil)
;;; just output "--" and "---"
(setq org-export-with-special-strings nil)
;;; no parse code of "TeX" and "LaTeX"
(setq org-export-with-TeX-macros nil)
(setq org-export-with-LaTeX-fragments nil)

;;(setq org-export-htmlize-output-type 'css)
(setq org-export-with-toc nil)

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
  (org-html-export-as-html nil nil nil t)
  (princ (buffer-string)))
