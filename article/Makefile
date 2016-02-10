SRC= $(wildcard *.md)
PDF= $(SRC:.md=.pdf)

SVG= $(wildcard graphics/*.svg)
GENERATED_PDF = $(SVG:.svg=.pdf)


ARGS= ---quiet
#ARGS= --listings -H latex/prelude.tex
#FILTERS=--filter filters/pandoc-crossref --filter pandoc-citeproc --filter filters/definition.py --filter filters/algorithm.py
#--filter filters/pandoc-svg.py --filter pandoc-fignos --filter pandoc-citeproc --filter filters/code-minipage --filter filters/proof.py

PANDOC= panzer

all:article.pdf

.PHONY: clean continious

%.pdf: %.md
	-@rm $@ 2> /dev/null
	$(PANDOC) $(FILTERS) $(ARGS) $< -o $@

clean:
	-@rm $(GENERATED_PDF)
	-@rm $(PDF)

continious:
	-@bin/inotifyrun $(PANDOC) $(FILTERS) $(ARGS) article.md -o article.pdf
