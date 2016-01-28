---
title: "LOLLIPOP : aLternative Optimization with causaL Link Injection Partial Ordered Planning"
author: Paper ID\#42
tags: [nothing, nothingness]
abstract: [Abstract goes here !]
bibliography: latex/zotero.bib
documentclass: latex/ecai
csl: latex/ieee.csl
smart: yes
standalone: yes
numbersections: yes
latex-engine: pdflatex
listings: yes
include-in-header: latex/prelude.tex
markdown_in_html_blocks: yes

---

\bibliographystyle{ecai}

# Introduction {-}

In general artificial intelligence, planning has proven to be …

* Citing existing works to situate the present paper
* Explain the work and the motivations of it

# The Partial Order Planning Framework
* Introduce the planning system we use and explain some basic notation while citing reference paper
* Put the regular POP algorithm and cite papers that aims at enhancing it while explaining their limitations.

# Motivating Examples
* Start by explaining why we are doing the present work (cite Ramirez)
* Then introduce the graphical legend and start by explaining an example showing flaws of previous works.
* Introducing the solution in this case

# Improving POP's Performance

* Explanation of the usefulness heuristics
* Explanation of how domain causal graph is built
* Goal selection algorithm

* Using the causal graph to improve consistency checking (reversing known cycles in causal graph)

* Improve upon flaw selection mechanisms and reducing branching factor with the causal graph.

# Improving POP's Quality

* Transition to the flaw selection of related subgoals before threats.
* Introduction to negative flaws
* Introduction of Alternative and Orphan using notation and algorithms.
* Explanation of Alternative and Orphans using examples.

# Properties of SODA POP

* Proof ? of correlation between causal graph and cycle detection
* Proof? reduction of branching in every case with causal graph selection
* Proof? Enhancement of plan quality using negative flaws

# Experiments

* Present the experimental setup and metrics
* Show various graphs of comparison between SODA and vanilla POP.
* Explanation of results and discussion.

# Conclusion {-}

* Discussion of results and properties
* Summary of improvements
* Introducing soft solving and online planning.
* Online planning
* plan recognition and constrained planning

# References
