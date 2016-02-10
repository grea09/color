#!/usr/bin/env python

"""
Pandoc filter to convert divs with class="proof" to LaTeX
proof environments in LaTeX output, and to numbered proof
in HTML output.
"""

from pandocfilters import toJSONFilter, RawBlock, Div

theoremcount = 0


def latex(x):
    return RawBlock('latex', x)


def html(x):
    return RawBlock('html', x)


def definitions(key, value, format, meta):
    if key == 'Div':
        [[ident, classes, kvs], contents] = value
        if "proof" in classes:
            if format == "latex":
                if ident == "":
                    label = ""
                else:
                    label = '\\label{' + ident + '}'
                name = ""
                if len(kvs) > 0:
                    [[name, value_]] = kvs
                if name == "name":
                    value = '[' + value_ + ']'
                else:
                    value = ""
                return([latex('\\begin{proof}' + value + label)] + contents +
                       [latex('\\end{proof}')])
            elif format == "html" or format == "html5":
                global theoremcount
                theoremcount = theoremcount + 1
                newcontents = [html('<dt>Proof ' + str(theoremcount) + '</dt>'),
                               html('<dd>')] + contents + [html('</dd>\n</dl>')]
                return Div([ident, classes, kvs], newcontents)

if __name__ == "__main__":
    toJSONFilter(definitions)
