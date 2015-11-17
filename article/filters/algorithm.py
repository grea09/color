#!/usr/bin/env python

"""
Pandoc filter to convert divs with class="algorithm" to LaTeX
algorithm environments in LaTeX output, and to numbered algorithm
in HTML output.
"""

from pandocfilters import toJSONFilter, RawBlock, Div

theoremcount = 0


def latex(x):
    return RawBlock('latex', x)


def html(x):
    return RawBlock('html', x)


def algorithms(key, value, format, meta):
    if key == 'Div':
        [[ident, classes, kvs], contents] = value
        if "algorithm" in classes:
            if format == "latex":
                if ident == "":
                    label = ""
                else:
                    label = '\\label{' + ident + '}'
                name = ""
                if len(kvs) > 0:
                    [[name, value_]] = kvs
                [[name, value]] = kvs
                if name == "caption":
                    caption = '\\caption{' + value + '}'
                else:
                    caption = ""
                return([latex('\\begin{algorithm}' + caption + label + '\\begin{algorithmic}')] + contents +
                       [latex('\\end{algorithmic}\\end{algorithm}')])
            elif format == "html" or format == "html5":
                global theoremcount
                theoremcount = theoremcount + 1
                newcontents = [html('<dt>Algorithm ' + str(theoremcount) + '</dt>'),
                               html('<dd>')] + contents + [html('</dd>\n</dl>')]
                return Div([ident, classes, kvs], newcontents)

if __name__ == "__main__":
    toJSONFilter(algorithms)
