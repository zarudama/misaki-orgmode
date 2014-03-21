(define-key global-map (kbd "C-h") 'delete-backward-char)
(define-key global-map (kbd "M-C-h") 'backward-kill-word)
(define-key global-map (kbd "C-j") nil)

;; USAGE:
;; $ cat foo.org | emacs --script org-export.el

;; (setq my-file-name (car argv))
;; (setq argv nil)

(add-to-list 'load-path "~/Dropbox/site-lisp/org-8.0/lisp")
(add-to-list 'load-path "~/Dropbox/site-lisp/org-8.0/contrib/lisp" t)
;;(require 'org)
(require 'org-install)
(require 'org-html)

;;;
;;; begin custmoize org-mode
;;;

;;; add a pathof major-mode of each language after having put path to htmlise through code if You want coloring.
(add-to-list 'load-path "~/Dropbox/dotfiles/emacs/package/htmlize-20130207.2102/")
(add-to-list 'load-path "~/Dropbox/dotfiles/emacs/package/clojure-mode-20131222.444/")
(require 'clojure-mode)
(require 'htmlize)
;;(setq org-html-htmlize-output-type 'inline-css) 
;;(setq org-html-htmlize-output-type 'css)

;;(global-font-lock-mode t)
;;(turn-on-font-lock)
(font-lock-mode 1)
;; (setq org-src-fontify-natively t)

;; (setq org-export-headline-levels 2)

;; (setq org-export-default-language "en")
;; (setq org-export-html-coding-system 'utf-8)
;; (setq org-export-with-fixed-width nil)
;; ;;; no use "^" and "_"
;; (setq org-export-with-sub-superscripts nil)
;; ;;; just output "--" and "---"
;; (setq org-export-with-special-strings nil)
;; ;;; no parse code of "TeX" and "LaTeX"
;; (setq org-export-with-TeX-macros nil)
;; (setq org-export-with-LaTeX-fragments nil)

;; ;;(setq org-export-htmlize-output-type 'css)
;; (setq org-export-with-toc nil)

