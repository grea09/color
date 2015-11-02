FILTERS=--filter pandoc-svg.py --filter pandoc-fignos --filter pandoc-citeproc
SRC= $(wildcard *.md)
PDF= $(SRC:.md=.pdf)

SVG= $(wildcard *.svg)
BAD_PDF = $(SVG:.svg=.pdf)

all:$(PDF)

.PHONY: clean

%.pdf: %.md
	pandoc $(FILTERS) $< -o $@

clean: $(BAD_PDF)
	rm $<
