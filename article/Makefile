SRC= $(wildcard *.md)
PDF= $(SRC:.md=.pdf)

SVG= $(wildcard graphics/*.svg)
GENERATED_PDF = $(SVG:.svg=.pdf)

ARGS=--latex-engine=xelatex --listings -H latex/prelude.tex
FILTERS=--filter filters/pandoc-svg.py --filter pandoc-fignos --filter pandoc-citeproc --filter filters/code-minipage --filter filters/definition.py --filter filters/algorithm.py --filter filters/proof.py

all:$(PDF)

.PHONY: clean

%.pdf: %.md
	pandoc $(FILTERS) $(ARGS) $< -o $@

clean:
	rm $(GENERATED_PDF)
