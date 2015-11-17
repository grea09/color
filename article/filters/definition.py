#!/usr/bin/env python

"""
Pandoc filter to convert divs with class="definition" to LaTeX
theorem environments in LaTeX output, and to numbered theorems
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
        if "definition" in classes:
            if format == "latex":
                if ident == "":
                    label = ""
                else:
                    label = '\\label{' + ident + '}'
                name = ""
                if len(kvs) > 0:
                    [[name, value_]] = kvs
                [[name, value_]] = kvs
                if name == "name":
                    value = '[' + value_ + ']'
                else:
                    value = ""
                return([latex('\\begin{definition}' + value + label)] + contents +
                       [latex('\\end{definition}')])
            elif format == "html" or format == "html5":
                global theoremcount
                theoremcount = theoremcount + 1
                newcontents = [html('<dt>Definition ' + str(theoremcount) + '</dt>'),
                               html('<dd>')] + contents + [html('</dd>\n</dl>')]
                return Div([ident, classes, kvs], newcontents)

if __name__ == "__main__":
    toJSONFilter(definitions)
