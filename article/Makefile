FILTERS=pandoc-citeproc
SRC= $(wildcard *.md)
PDF= $(SRC:.md=.pdf)

all:$(PDF)

%.pdf: %.md
	pandoc --filter $(FILTERS) $< -o $@
