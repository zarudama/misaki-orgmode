;; USAGE:
;; $ cat foo.org | emacs --script org-export.el
;; for debug
;; $ emacs -Q --debug-init -l org-export.el

(require 'cask "~/.cask/cask.el")
(cask-initialize "~/dev/misaki-orgmode/")

(define-key global-map (kbd "C-h") 'delete-backward-char)
(define-key global-map (kbd "M-C-h") 'backward-kill-word)
(define-key global-map (kbd "C-j") nil)

(require 'org)
(require 'htmlize)

;;;
;;; begin custmoize org-mode
;;;

(setq org-export-with-toc nil)
(setq org-export-headline-levels 2)

;;(setq org-html-htmlize-output-type 'inline-css) 
(setq org-html-htmlize-output-type 'css)
(setq org-src-fontify-natively t)
(global-font-lock-mode t)

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

  ;; ver.8.x
  ;; (org-html-export-as-html &optional ASYNC SUBTREEP VISIBLE-ONLY BODY-ONLY EXT-PLIST)
  (org-html-export-as-html nil nil nil t)
  
  (princ (buffer-string)))
