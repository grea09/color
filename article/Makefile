SRC= $(wildcard *.md)
PDF= $(SRC:.md=.pdf)

SVG= $(wildcard graphics/*.svg)
GENERATED_PDF = $(SVG:.svg=.pdf)

ARGS= --listings -H latex/prelude.tex
FILTERS=--filter pandoc-citeproc
#--filter filters/pandoc-svg.py --filter pandoc-fignos --filter pandoc-citeproc --filter filters/code-minipage --filter filters/definition.py --filter filters/algorithm.py --filter filters/proof.py

all:article.pdf

.PHONY: clean

%.pdf: %.md
	-@rm $@ 2> /dev/null
	pandoc $(FILTERS) $(ARGS) $< -o $@

clean:
	-@rm $(GENERATED_PDF)
	-@rm $(PDF)
