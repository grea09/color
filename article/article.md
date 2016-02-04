---
title: "LOLLIPOP: aLternative Optimization with causaL Link Injection Partial Ordered Planning"
author: Paper ID\#42
tags: [nothing, nothingness]
abstract: [Abstract goes here]

smart: yes
standalone: yes
numbersections: yes
markdown_in_html_blocks: yes

bibliography: latex/zotero.bib
csl: latex/ieee.csl

latex-engine: pdflatex
documentclass: latex/ecai
listings: yes
include-in-header: latex/prelude.tex

filters: pandoc-citeproc

---

# Introduction {-}

Until of the end of the 90s, Partial-Order Planning (POP) was the dominant approach for tackling a planning problem. Its least commitment philosophy was the central interest of this popularity. However, since then  state search planning took hold with more efficient forward search heuristics and fast backtracking.

This search of scalability and performance hides the greatest advantage of POP: flexibility. This advantage allows POP to efficiently build plans with the parallel use of actions. It also means that it can refine broken plans and optimize them further than a regular state-based planner.

In this paper, we explore new ideas to revive POP as an attractive alternative to other totally ordered approach. Some papers like UCPOP [@penberthy_ucpop_1992] and VHPOP [@younes_vhpop_2003] already tried to extend POP into such an interesting alternative. More recent efforts [@coles_forwardchaining_2010; @sapena_combining_2014] are trying to adapt the powerful heuristics from state-based planning to POP's approach.

Our approach is different: we prefer the use of the preprocessed domain causal graph and build an initial partial plan around it in order to already have an almost complete plan right away. This graph is also used during the refinement process for flaw and goal selection that will improve the branching factor of the algorithm. We also use it for consistency checks before applying flaws.

We also wanted to improve the plan quality as our input can be far from the optimal solution. This is the reason why we introduced two new negative flaws: the alternative and the orphan flaws.

In order to present our aLternative Optimization with causaL Link Injection Partial Ordered Planning (LOLLIPOP) system, we need to explain the classical POP framework and its limits.
<!-- TODO Plan introduction when plan will be stable -->

# The Partial Order Planning Framework

In this paper, we decided to build our own planning framework based on PDDL's concepts. 
 This new framework is called WORLD as it is derived from more generalistic world description tools such as RDF and ontologies. It is about equivalent in expressiveness to PDDL 3.1 with object-fluents support.<!-- FIXME: citation needed --> <!-- TODO motivation of why we use this -->

We define our **planning domain** as a tuple $\Delta = \langle T, C, \mathcal{F}, F, O \rangle$ where <!--FIXME The F symbol can be confusing-->

* $T$ are the **types**,
* $C$ is the set of **domain constants**,
* $\mathcal{F}$ is the set of **functions** with their arities and typing signatures,
* $F$ represents the set of **fluents** defined as potential equations over the terms of the domain,
* $O$ is the set of optionally parameterized **operators** with preconditions and effects.

The symbol system is completed with a notion of **term** (either a constant, a variable parameter or a property) and a few relations. We provide types with a relation of **subsumption** noted $t_1 \prec t_2$ with $t_1, t_2 \in T$ meaning that all instances of $t_1$ are also instances of $t_2$.
On terms, we add two relations: the **assignation** (noted $\leftarrow$) and the **potential equality** (noted $\doteq$).

From there we add the definition of a planning problem as the tuple $\Pi = \langle \Delta, C_\Pi , I, G, P\rangle$ where

* $\Delta$ is a planning domain,
* $C_\Pi$ is the set of **problem constant** disjoint from $C$,
* $I$ is the **initial state**,
* $G$ is the **goal**,
* $P$ is a given **partial plan** formed as a tuple $\langle S, L, B\rangle$ with $S$ the set of **steps** (instantiated operators also called actions), $L$ the set of **causal links**, and $B$ the set of **binding constraints**. <!--FIXME cite good excuses ?-->

<!-- TODO
* Introduce the planning system we use and explain some basic notation while citing reference paper
* Put the regular POP algorithm and cite papers that aims at enhancing it while explaining their limitations. -->

# Motivating Examples

<!-- TODO
* Start by explaining why we are doing the present work (cite Ramirez)
* Then introduce the graphical legend and start by explaining an example showing flaws of previous works.
* Introducing the solution in this case-->

# Improving POP's Performance

<!-- TODO
* Explanation of the usefulness heuristics
* Explanation of how domain causal graph is built
* Goal selection algorithm

* Using the causal graph to improve consistency checking (reversing known cycles in causal graph)

* Improve upon flaw selection mechanisms and reducing branching factor with the causal graph.-->

# Improving POP's Quality

<!-- TODO
* Transition to the flaw selection of related subgoals before threats.
* Introduction to negative flaws
* Introduction of Alternative and Orphan using notation and algorithms.
* Explanation of Alternative and Orphans using examples.-->

# Properties of LOLLIPOP

<!-- TODO
* Proof ? of correlation between causal graph and cycle detection
* Proof? reduction of branching in every case with causal graph selection
* Proof? Enhancement of plan quality using negative flaws-->

# Experiments

<!-- TODO
* Present the experimental setup and metrics
* Show various graphs of comparison between SODA and vanilla POP.
* Explanation of results and discussion.-->

# Conclusion {-}

<!-- TODO
* Discussion of results and properties
* Summary of improvements
* Introducing soft solving and online planning.
* Online planning
* plan recognition and constrained planning-->

# References
