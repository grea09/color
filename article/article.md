---
title: "SODA POP: SOft solving and Defect Aware Partial Ordering Planning"
author: 
  Paper ID\#232
tags: [nothing, nothingness]
abstract: |
  As of recent years, automated planning domain has mainly focused on performances and advances in state space planning to improve scalability. That orientation shadows other less efficient ways of doing like Partial Ordering Planning (POP) that has the advantage to be much more flexible. This approach generates plans that can be refined easily and can be used to achieve greater resilience, repairing capabilities and soft resolution that applications based on online plan recognition or decision-making can benefit from. This paper presents a set of algorithms, named solving and Defect Aware Partial Ordering Planning (SODA POP), that aims at targeting these objectives and maintain a high plan quality. Our algorithms create proper offline plans for goals and use an effective defect fixing algorithm to repair input plans even if these plans are corrupted. SODA POP can also use healer actions and links in order to always return a valid plan even in cases where the problem is impossible to solve using problem derivation. Some relevant properties of these algorithms are analyzed in this article and experimental results show interesting performances for online planning and repairing.
  
bibliography: latex/zotero.bib
documentclass: article
csl: latex/ieee.csl
smart: yes
standalone: yes
numbersections: yes
latex-engine: pdflatex
markdown_in_html_blocks: yes

---
<!-- TODO discuss order of authors-->
<!-- FIXME il n'y a plus de numerotation de section -->

# Introduction {-}
<!-- The Soft Ordering and Defect Aware Partial Ordering Planning algorithm (SODA POP)
This name is PERFECT ! -->

For some time, Partial Order Planning (POP) has been the most popular approach to planning resolution. This kind of algorithms are based on *least commitment strategy* on plan step ordering that allow actions to be flexibly interleaved during execution [@weld_introduction_1994]. Thus, the way the search is made using flexible partial plan as a search space allowed for more versatility for a wide variety of uses. As of more recent years, new state space search approach and heuristics [@richter_lama_2011] have been demonstrated to be more efficient than POP planners due to the simplicity and relevance of state representations opposed to partial plans [@ghallab_automated_2004]. This have made the search of performance the main axis of research for planning.

While this goal is justified, it shadows other problems that some practical applications cause. For example, the flexibility of Plan Space Planning (PSP) is an obvious advantage for applications needing online planning: plans can be repaired on the fly as new information and goals are considered. The idea of using POP for online planning and repairing plans instead of replanning from scratch is not new [@van_der_krogt_plan_2005], but has never been paired with the resilience that some other cognitive applications may need, especially when dealing with sensors noise in input data.
This resilience makes fixing errors easier than with an external algorithm as the plan logic allows for context driven decision on the way to repair the issues. For example, if an action becomes unrelevant or incoherent, the flaw in the partial plan makes the issue explicit and therefore easier to fix. Applications might sometimes provide plans that can contain errors and imperfections that will decrease significantly the efficiency of the computation and the quality of the output plan.
Additionnaly, these plans may become totally unsolvable. This problem is to our knowledge not treated in planning of all forms (state planning, PSP, and even constraint satisfaction planning) as usually the aim is to find a solution relative to the original plan (which makes sense). However, as we proceed a mechanism of *problem derivation* may be required. This will allow soft solving of any problem regardless of its resolvability.

One of the applications that needs these improvements is plan recognition with the particular use of off-the-shelf planners to infer the pursued goal of an agent where online planning and resilience is particularly important [@ramirez_plan_2009]. This method adds dummy actions that need to be satisfied by modifying the goal, to ensure that the observed plan is selected by the planner. A system that is able to repair plan will ease such an application. Another application is decision-making in dynamic environments. Indeed, having a plan that details all steps and explicits the ones that are not possible, can help decide on the plan of actions to take.
These problems call for new ways to improve the answer of a planner. These improvements must provide relevant plan information pointing out exactly what needs to be done in order to make a planning problem solvable, even in the case of obviously inconsistent input data. This paper aims to solve this while preserving the advantages of PSP algorithms (flexibility, easy fixing of plans, soundness and completeness). We propose a new set of  auxiliary algorithms that combined forms our SOft solving and Defect Aware Partial Ordering Planning (SODA POP) algorithm that targets those issues and allows making POP algorithm more resilient, efficient and relevant. This is done by pre-emptively computing proper plans for goals, by solving new kinds of defects that input plans may exhibit, and by healing compromised plan by deriving the initial problem with forged actions to allow resolution.
To explain this work we first introduce a few notions, notations and specificities of existing POP algorithms. Then we present and illustrate their limitations, categorizing the different defects arising from the resilience problem and explaining our auxiliary algorithms, their uses and properties. We compare the performance, resilience and quality of POP and our solution.

# Definitions
<!-- TODO Scénario --> 
<!--In order to present our work and explain examples a way of representation for schema in figure @fig:legend and notations for mathematical representation.--> 
First, we introduce some definitions and notations for mathematical representation to present our model.

## Classical planning

<div class="definition" name="Fluents">
A fluent is a property of the world. We note $\lnot f$ the complementary fluent of $f$ meaning that $f$ is true when $\lnot f$ is false and vice-versa.
</div>

It is often represented by first order logical propositions but in this paper we choose to focus on the algorithm and to represent fluents as simple literals (fully instantiated) using $\mathbb{Q}^*$, the set of relative integers without $0$, as the fluent domain. We use negative integers to represent opposite fluents.

<div class="definition" name="State">
A state is defined as a set of fluents. States can be additively combined. We note $s_1 + s_2 = \left( s_1 \cup s_2 \right) - \left\{ f \middle| f \in s_1 \land \lnot f \in s_2  \right\}$ such operation. It is the union of the fluents with an erasure of the complementary ones.
</div>

<div class="definition" name="Action">
An action is a state operator. It is represented as a tuple $a = \langle pre, eff \rangle$ with $pre$ and $eff$ being sets of fluents, respectively the preconditions and the effects of $a$. An action can be used only in a state that verifies its preconditions. We note $s \models a \Leftrightarrow pre(a) \subseteq s$ the verification of an action $a$ by a state $s$.
</div>

An action $a$ can be functionally applied to a state $s$ as follows:
$$a:= \substack{ \left\{ s \models a \middle| s \in S\right\} \to S\\
    a(s) \mapsto s + eff(a)}$$ 
with $S$ being the set of all states. This means that an action adds all its effects to the state of the world on application (by removing complementary fluents if needed).
We distinguish between two specific kinds of actions: actions with no preconditions are synonymous to a state and those with empty effect are called a goal.

## Plan Space Planning

### Problem
We define a partial plan satisfaction problem as a tuple noted 
$P = \langle A, I, G, p \rangle$ with:

* $I$ and $G$ being the pseudo actions representing respectively the initial state and the goal.
* $p$ being a partial plan to refine.
* $A$ the set of all actions.

<div class="definition" name="Partial Plan">
A *partial plan* is a tuple $p = \langle A_p, L\rangle$ where $A_p$ is a set of steps (actions) and $L$ is a set of causal links of the form $a_i \xrightarrow{f} a_j$, such that $\{ a_i, a_j \} \subseteq A_p \land f \in eff(a_i) \cap pre(a_j)$ or said otherwise, this causal link means that the fluent $f$ is provided by an effect of $a_i$ to a precondition of $a_j$. We include the ordering constraints of PSP in the causal links. An ordering constraint is noted $a_i \to a_j$ and means that the plan means $a_i$ as a step that is prior to $a_j$ without specific cause (usually because of threat resolution). We note the order relation over $A$ for an action $a_i$ that is prior to the action $a_j$ following all the causal links and ordering constraints $a_i \succ a_j$. We can consider it as a graph formed by step as vertices and causal links as edges.
</div>

The figure @fig:legend details how the elements of partial plans are represented in the rest of this paper.

![Global legend for partial plans](graphics/legend.svg) {#fig:legend}

In this figure the fluent $6$ is a precondition of the action $a$ and also an effect. The causal link is providing the fluent $6$. The dotted line is an ordering constraint specifying which action should be placed before another. $I$ and $G$ are respectively the initial and goal step.

### Flaws

When refining a partial plan, we need to fix flaws. Those could be present in the input or created by the refining process. Flaws can either be unsatisfied subgoals or threats to causal links.

<div class="definition" name="Subgoal">
A subgoal $s$ is a precondition of an action $a_s \in A_p$ with $s \in pre(a_s)$ 
that isn't satisfied by any causal link. We can note a subgoal as:
$$a_i \xrightarrow{s} a_s \notin L \mid \{ a_i, a_s \} \subseteq A_p $$
</div>

<div class="definition" name="Threat">
A step $a_t$ is said to threaten a causal link $a_i \xrightarrow{t} a_j$ if and only if 
$$\neg t \in eff(a_t) \land a_i \succ a_t \succ a_j \models L$$ 
</div>
In other words, the action has a complementary effect that can be inserted between two actions needing this fluent while being consistent with the ordering constraint in $L$.

<div class="definition" name="Resolvers">
Resolvers are a set of actions and causal links $r = \langle A_r , L_r \rangle$ that fixes flaws. 

* A resolver for a subgoal is an action $a_r \in A$ that has $s$ as an effect $s \in eff(a_r)$ inserted along with a causal link noted $a_r \xrightarrow{s} a_s$. 
* The usual resolvers for a threat are either $a_t \to a_i$ or $a_j \to a_t$ which are called respectively promotion and demotion links.
</div>

<div class="definition" name="Solution">
The solution of a PSP problem is a valid partial plan that respects the specification of said problem (only using actions in $A$ and having the correct initial and goal step).
</div>

<div class="definition" name="Consistency">
A partial plan is consistent if it contains no ordering cycles. The directed graph formed by step as vertices and causal links as edges isn't cyclical. This is important to guarantee the soundness.
</div>

<div class="definition" name="Flat Plan">
We can instantiate one or several flat plans from a partial plan. A flat plan is an ordered sequence of actions $\pi = [ a_1, a_2 \ldots a_n]$ that acts like a action $\pi = \langle pre_\pi, eff_\pi \rangle$ and can be applied to a state $s$ using functional composition operation $\pi := \bigcirc_{i=1}^n a_n$.
</div>

We call a flat plan valid if and only if it can be functionally applied on an empty state. We note that this is different from classic state planning because in our case the initial state is the first action that is already included in the plan.


<div class="definition" name="Validity">
A partial plan is valid if and only if it is consistent and if all flat plans that can be generated are valid.
</div>


# Classical POP
Partial Order Planning (POP) is a popular implementation of the general PSP algorithm. It refines a partial plan by trying to fix its flaws and is proven to be sound and complete [@erol_umcp:_1994].


<div id="pop" class="algorithm" caption="Classical Partial Order Planning">
\footnotesize
\Function{pop}{Queue of Flaws $agenda$, Problem $P$}
    \State \Call{populate}{$agenda$, $P$} \Comment{Only on first call}
    \If{$agenda = \emptyset$}
        \State \Return Success \Comment{Stop all recursion}
    \EndIf
    \State Flaw $f\gets agenda.popFromQueue$ \Comment{Stop all recursion}
    \Comment{First element of the queue}
    \State Resolvers $R \gets$ \Call{resolvers}{$f$, $P$} 
    \State \Comment{Ordered resolvers to try}
    \ForAll{$r \in R$} \Comment{Non deterministic choice operator}
        \State \Call{apply}{$r$, $P.p$} \Comment{Apply resolver to partial plan}
        \If{\Call{consistent}{$P.p$}} \Comment{$P.p$ is the partial plan}
            \State \Call{pop}{$agenda \cup$ \protect\Call{relatedFlaws}{$f$}, $P$}
            \State \Comment{Finding new flaws introduced by the resolver}
        \Else 
            \State \Call{revert}{$r$, $P.p$} \Comment{Undo resolver insertion}
        \EndIf
    \EndFor
    \State \Return Failure
    \State \Comment{Revert to last non deterministic choice of resolver}
\EndFunction
</div>

<!-- ![A simple problem that will be used throughout this paper](graphics/problem.svg) {#fig:problem} -->

Algorithm 1 presents the base algorithm for a planer in the plan space. POP implementation uses an agenda of flaws that is efficiently updated after each refinement of the plan. A flaw is selected for resolution and we use a non deterministic choice operator to pick a resolver for the flaw. The resolver is inserted in the plan and we recursively call the algorithm on the new plan. On failure, we return to the last non deterministic choice to pick another resolver. The algorithm ends when the agenda is empty or when there is no more resolver to pick for a given flaw. <!-- FIXME ## Example -->
Before continuing, we present a simple example of classical POP execution with the following example of a problem.<!--problem represented in figure @fig:problem. We did not represent empty preconditions or effects to improve readability.-->

* An initial state $I = \langle \emptyset , \{ 1, 2 \} \rangle$ and a goal $G = \langle \{ 3, 4, -5, 6 \}, \emptyset \rangle$ encoded as dummy steps.
* $a \langle \{1\}, \{3,5\} \rangle$, $b \langle \{2\}, \{4\} \rangle$ and  $c \langle \{5\}, \{6\} \rangle$ are simple actions that are useful to achieve the goal.
* $n \langle \{-8, 7\}, \{-7, 8\} \rangle$ and $l \langle \{-7, 8\}, \{-8, 7\} \rangle$ are looping actions that are meant to cause cycles. 
* $t \langle \{4\}, \{-5\} \rangle$ is meant to be threatening to the plan's integrity and will generate threats. 
* $u \langle \{4\}, \emptyset \rangle$, $v \langle \{4\}, \{4\} \rangle$, $w \langle \{9\}, \{3\} \rangle$, $x \langle \{7\}, \{-2,2\} \rangle$ are actions that will cause problems. [(cf. Defects section)](#defects).

<!-- FIXME ## Limitations -->

This standard way of doing has seen multiple improvements over expressiveness like with UCPOP [@penberthy_ucpop:_1992], Hierarchical Tasks Network to add more user control over sub-plans [@bechon_hipop:_2014], cognition with defeasible reasoning [@garcia_defeasible_2008], or speed with multiple ways to implement the popular fast forward method from state planning [@coles_forward-chaining_2010]. However, all these variants do not treat the problem of online planning, resilience and soft solving. Some other closer works like [@van_der_krogt_plan_2005] treat the problem of online planning by identifying and removing trees of the partial plan. This is done with a heuristic that choses an unrefinemment strategies that causes a heavy replanning of the problem even if only one action needs removal. This is a significant problem when trying, for example, to adapt a plan with minimal changes due to replanning. Indeed, all these problems can affect POP's performance and quality. <!-- FIXME pas clair -->

![Standard POP result to the problem](graphics/pop.svg) {#fig:pop}

This example has been crafted to illustrate problems with standard POP implementations. We give a possible resulting plan of standard POP in figure @fig:pop. There are some issues as for how the plan has been built. The action $v$ is being used even if it is useless since $b$ already provided fluent $4$. We can also notice that despite being contradictory the action $x$ raised no alarm. As for ordering constraints, we can clearly see that the link $a \to t$ is redundant with the path $a \xrightarrow{5} c \to t$ that already ensures that constraint by transitivity. Also, some problems arise during execution with the selection of $w$ that causes a dead-end.

Of course the flaw selection mechanism of certain variants can prevent that to happen in that case. But often flaw selection mechanisms are more speed oriented and will do little if a toxic action seems to fit better than a more coherent but complex one.<!-- TODO citer au moins une ref qui utilise un tel mecanisme de selection de menace!-->
All these issues are caused by what we call *defects* as they are not regular PSP flaws but they still cause problems with execution and results. We will address these defects and propose a way to fix them in [the next section](#defects).

# Auxiliary algorithms to POP

In order to improve POP algorithms' resilience, online performance and plan quality, we propose a set of auxiliary algorithms that provides POP with a clean and efficiently populated initial plan. The complete algorithm will be presented in [the next section](#soda) as a combination of all auxiliary algorithms and classical POP.

## Proper plan generation

<div id="properplan" class="algorithm" caption="Proper plan generation for a given goal $g$">
\footnotesize
\Function{properPlan}{Goal $g$, Actions $A$}
    \State Partial Plan $p \gets \emptyset$
    \State Actions $relevants \gets$ \Call{satisfy}{$g$, $A$, $p$}
    \State \Comment{Satisfy the goal with all participating actions and links}
    \State Queue of Actions $open \gets relevants$
    \While{$open \neq \emptyset$}
        \State Action $a\gets open.popFromQueue$
        \State Actions $candidates \gets$ \Call{satisfy}{$a$, $A$, $p$}
        \ForAll{$candidate \in candidates$}
            \If{$candidate \notin relevants$}
                \State $open.pushToQueue(candidate)$
            \EndIf
        \EndFor
    \EndWhile
\EndFunction
</div>

![Proper plan of the example goal](graphics/proper.svg) {#fig:proper}

As in online planning goals can be known in advance, we propose a new mechanism that generates proper plans for goals. We take advantage of the fact that this step can be done offline to improve the performance of online planning. This offline execution prevents us to access the details of the initial state of the world as it will be defined at runtime. We define for that the concept of *participating action*. An action $a \in A$ participates in a goal $G$ if and only if $a$ has an effect $f$ that is needed to accomplish $G$ or that is needed to accomplish another participating action's preconditions. <!-- Q:Les participating action sont des actions de l'espace d'action A ? A:Oui --> A proper plan is a partial plan that contains all participating actions as steps and causal links that bind them with the step they are participating in. This proper plan is independent from the initial step because we might not have the initial step at the time of the proper plan generation.
Once applied on the previous example, proper plan generation returns the partial plan presented in figure @fig:proper. This partial plan doesn't have initial state because of its offline nature. It also shows several cycles and obvious problems. However, it has all the steps of the correct final plan.
This algorithm helps the POP algorithm by prefetching all the actions subgoals might need. However, this result needs to be cleaned first to be helpful.

## Defect resolution {#defects}

<div id="defectresolution" class="algorithm" caption="Defect resolution">
\footnotesize
\Function{clean}{Problem $P$}
    \State \Call{assert}{$pre(P.I) = \emptyset$}
    \State \Call{assert}{$eff(P.G) = \emptyset$}
    \State $P.p.A_p \gets P.p.A_p \cup \{P.I, P.G\}$
    \State $defects \gets$ \Call{findDefects}{$P$}
    \State \Call{fix}{$defects$, $P$}
\EndFunction

\Function{fix}{Defects $defects$, Problem $P$}
    \ForAll{Defect $d \in defects$}
        \State $defect.$\Call{fix}{$P$} \Comment{Fixing is proper to the defect}
    \EndFor
\EndFunction

\Function{findDefects}{Problem $P$}
    \State \Return \Call{illegal}{$P$} $+$ \Call{interfering}{$P$}
    \State \Comment{Concatenate the found illegal and interfering defects}
\EndFunction
</div>

When the POP algorithm is used to refine a given plan (that was not generated with POP or that was altered), a set of new defects can be present in it interfering in the resolution and sometimes making it impossible to solve. We emphasize that these *defects are not regular POP flaws* but new problems that classical POP can't solve. The aim of this auxiliary algorithm is to clean the plans from such defects in order to improve 
resilience and plan quality.

<div class="definition" name="Plan Quality">
Plan quality is an indicator that is measured by the number of defects in a partial plan. There are two specific indicators for a plan quality: 

* Action quality is the number of defects related to actions divided by the number of actions.
$$quality_{action}(p) =  1 - \left( |ActionDefects(p)| \over |p.A_p| \right)$$
* Link quality is the number of defects related to links divided by the number of links.
</div>
From there it is obvious that plan quality will improve over POP since SODA POP algorithm is guaranteed to remove all defects in a plan.
There are two kinds of defects: the illegal defects that violate base hypothesis of POP and the interference defects that can lead to excessive computational time and to poor plan quality.

### Illegal defects
These defects are usually hypothesized out by existing algorithms. They are illegal use of partial plan representation and shouldn't happen under regular circumstances. They may appear if the input is generated by an approximate cognitive model that doesn't ensure consistency of the output or by unchecked corrupted data. These defects will most of the time simply break regular POP algorithms or at least make the performances decrease significantly. Illegal defects and solutions to fix them are presented in the following section. The first of these defects is pre-existing cycles in the input plan.

<div class="definition" name="Cycles">
A cycle is the corresponding notion as in graph theory. Let's consider the graph of a partial plan. A cycle in this graph corresponds to a cycle of actions in the partial plan.
</div>
A plan cannot contain cycles as it makes it impossible to complete. Cycles are usually detected as they are inserted in a plan but poor input can potentially contain them and break the POP algorithm as it cannot undo cycles.
We use strongly connected component detection algorithm to detect cycles. Upon cycle detection, the algorithm can remove arbitrarily a link in the cycle to break it. In our example the actions $n$ and $l$ are meant to cause such cycles.
In a plan some actions can be illegal for POP. Those are the actions that are inconsistent. 
<div class="definition" name="Inconsistent actions">
An action $a$ is contradictory if and only if 
$$\exists f \{f, \lnot f \} \in eff(a) \lor \{f, \lnot f \} \in pre(a)$$
</div>
We remove only one of those effects or preconditions based on the usage of the action in the rest of the plan. If none of those are used, we choose to remove both. In our previous example, the action $x$ is one of these inconsistent actions with fluent $2$ and $-2$ in its effects.

<div class="definition" name="Toxic actions">
Toxic actions are those that have effects that are already in their preconditions or empty effects. They are defined as:
$$a | pre(a) \cap eff(a) \neq \emptyset \lor eff(a) = \emptyset$$
</div>

Toxic actions can damage a plan as well as make the execution of POP algorithm much longer than necessary. This is fixed by removing the toxic fluents ($pre(a) \nsubseteq eff(a)$) by updating the effects with $eff(a) = eff(a)-pre(a)$. If the effects become empty, the action is removed altogether from plan steps and $A$. In our previous example the actions $u$ and $v$ are toxic as the fluent $4$ is in the effects and preconditions of $v$, and $u$ has empty effects. In this case, these actions will be removed by the algorithm as they don't have any other effects.

The defects can be related to incorrect links. The first of which are liar links.
<div class="definition" name="Liar links">
A liar link is a link that doesn't hold a fluent in the preconditions or effect of its source and target. We can note:
$$a_i \xrightarrow{f} a_j | f \notin eff(a_i) \cap pre(a_j)$$
</div>
A liar link can be either already present in the data or created by the removal of an effect of an inconsistent or toxic action (with the causal link still remaining).

We call lies, fluents that is held by links without being in the connected actions . To resolve the problem we remove all lies and add all savior fluents, i.e. a fluent in $eff(a_i) \cap pre(a_j)$ that isn't already held by the link. We delete the link all together if it doesn't link any fluent.

### Interference defects
This kind of defects is not as problematic as the illegal ones: they won't make the plan unsolvable but they can still cause performance and quality drops. These defects can happen in POP generated plans to some extends. Interference defects and solutions to fix them are presented in the following.

<div class="definition" name="Redundant links">
A redundant link is an ordering constraint (causal link without fluents) with another equivalent path of longer length int the graph form of the partial plan. It means that an ordering constraint $a_i \to a_j$ is redundant if and only if it exists another path from $a_i$ to $a_j$ of length greater to $1$.
</div>
Since POP relies on those additional links, this part focuses on removing the ones that were generated for threat removal purpose to simplify the plan. For example, in figure @fig:pop we can see that the ordering constraint from $a$ to $t$ is redundant with the path $a \xrightarrow{5} c \to t$ in that, it isn't needed to resolve the threat. Therefore, the algorithm would remove it. This reduces the number of edges in the plan and therefore simplifies it.

Causal links can be found to compete with one another. 
<div class="definition" name="Competing causal links">
A competing link $a_i \xrightarrow{f} a_k$ competes with another link $a_j \xrightarrow{f} a_k$ if it provides the same fluent to the same action.
</div>
The main difference with redundant links is that competing links are not related to threat resolution generated ordering constraint but confronts two causal links that can provide the same fluent. Another difference is that it cannot happen in classical POP algorithm. This kind of defects is making the plan more complex and can also hide useless actions which would potentially cause more flaws to handle than necessary.
In order to remove the least interesting link in competing causal links, we need to compare their respective providing actions, to chose the most useful ones.
<div class="definition" name="Usefulness of an action">
The usefulness of an action is the number of participating links (outgoing causal links) divided by the number of needing links (incoming causal links). If there are no needing links, the usefulness is simply the number of participating links.

In the special cases if the action is either an initial step or a goal, the usefulness is $+\infty$. The usefulness of savior action is $0$.
</div>

We choose the link with the source action having the highest usefulness. This replaces unnecessary savior actions and allows reducing the usefulness of the least action in order to eventually make it an orphan and prune it from the plan.

![Example of choice for cutting competing links based on usefulness of actions](graphics/competing.svg) {#fig:competing}

On the left example of figure @fig:competing, the action $a_j$ participates much more than the action $a_i$ and therefore the link to be removed would be $a_i \xrightarrow{f} a_k$. On the right example, the actions don't have different outgoing links but the action $a_j$ is here much needier than its competitor. Therefore, the link to be removed is the link $a_j \xrightarrow{f} a_k$.

Actions can sometimes have no use in a plan as they don't contribute to it. 
<div class="definition" name="Orphan actions">
Orphan actions are actions without any links or actions with no outgoing path to the goal (meaning that it doesn't participate in the plan). That also concerns actions that would become orphans if another orphan action is removed.
</div>

<!-- FIXME il y a des decalages a gauche du texte attention a mettre a droit les choses en plus
 -->
<div id="orphanaction_find" class="algorithm" caption="Orphan actions finding">
\footnotesize
\Function{$OrphanAction.$find}{Problem $P$}
    \State Actions $orphan \gets \emptyset$
    \State int $oldSize$
    \Repeat
        \State $oldSize \gets |orphan|$
        \ForAll{Action $step \in P.p.A_p$}
            \If{$step \in orphan$}
                \State continue
            \EndIf
            \State int $towardOrphan \gets 0$
            \State Causal Links $outgoing \gets$ 
            \State \Call{outgoingEdgesOf}{$step$, $P.p$}
            \ForAll{Causal Link $link \in outgoing$}
                \If{$link.target \in orphan$}
                    \State $towardOrphan ++$
                \EndIf
            \EndFor
            \If{$|outgoing| = towardOrphan \land step \notin \{ P.I, P.G\}$}
                \State $orphan \gets orphan \cup \{step\}$
            \EndIf
        \EndFor
    \Until{$oldSize \neq |orphan|$}
    \State \Return $orphan$
\EndFunction
</div>

In order to fix this, we derive the idea of the backward removal tree of [@van_der_krogt_plan_2005]. Algorithm 4, removes an action if all its outgoing causal links are leading to useless actions. It iterates over all actions until no new useless action is discovered. All useless actions are then removed from the plan.

<div class="definition" name="Competing actions">
In the same way, links can be competing toward a common needer, sometimes some actions that are more suited to achieve a goal than others. These actions are taken into account if they have effects that are carried by the link, if they are more useful than the source of the existing link and if they wouldn't cause a cycle.
</div>
<!-- FIXME rappelle moi demain de cette definition il faut la reformuler
on verra ca ensemble  -->

In such a case the link is removed and another one is formed by the better suited action.

## Soft resolution
<!-- TODO examples-->
<div id="softresolution" class="algorithm" caption="Soft resolution healing">
\footnotesize
\Function{heal}{Problem $P$}
    \State int $minViolation \gets \infty$
    \State Plan $best \gets P.p$
    \State Flaw $annoyer$
    \ForAll{$\langle flaw, plan \rangle \in P.partialSolutions$}
        \State int $currentViolation \gets$ \Call{violation}{$plan$, $P.G$}
        \If{$currentViolation < minViolation$}
            \State $best \gets plan$
            \State $annoyer \gets flaw$
            \State $minViolation \gets currentViolation$
        \EndIf
    \EndFor
    \State $P.p \gets best$
    \State $P.partialSolutions \gets \emptyset$
    \ForAll{Resolver $resolver \in$ \Call{healers}{$annoyer$}}
        \State \Call{apply}{$resolver$, $P.p$}
    \EndFor
\EndFunction
</div>

This auxiliary algorithm is meant to deal with failure. It heals the plan to make the failure recoverable for the next iteration of POP. Of course it can't fix the plan by keeping the problem as it is. This obviously breaks some properties as the algorithm no longer adheres to the specification of the input, but in exchange it will always issue a valid plan whatever happens. [(cf. Hypersoundness section)](#hypersoundness).

Soft failure is useful when the precision and validity of the output is not the most important criteria we look for. In some cases (like in recognition processes) it is more useful to have an output even if it is not exact than no output at all. That is why we propose a soft failing mechanism for the POP algorithm.
Following, we define first some notions, then we explain the healing algorithm.

<div class="definition" name="Needer">
A needer is an action that needs a resolution related to a flaw. We define different types of needers according to the type of the flaw.

* For a subgoal $a_s \xrightarrow{s} a_n$ the needer is the action $a_n$ that has an unsatisfied precondition provided by an eventual action $a_s$ in the current partial plan.

* For a threat $a_t$ of a link $a_p \xrightarrow{t} a_n$ the needer is the target $a_n$ of the threatened causal link.
</div>

<div class="definition" name="Proper fluents"> 
A proper fluent of a flaw is the one that caused the flaw. For a subgoal $a_n \xrightarrow{s} a_s$, it is the unsatisfied precondition $s$. For a threat $a_t$ of a causal link $a_p \xrightarrow{t} a_n$, it is the fluent $t$ held by the threatened causal link.
</div>

<div class="definition" name="Savior">
The savior of a flaw is the forged action $a_s = \langle \emptyset, \{p\} \rangle | a_s \notin A$ with $p$ being the proper fluent of the flaw.
</div>

The concept of healer is made to target rogue flaws that caused total failure. 
<div class="definition" name="Healers"> 
A healer is a resolver built using the savior of the flaw in order to provide the missing fluents to it. The general formula of a healer is $a_s \xrightarrow{p} a_n$ with $a_s$ being the savior of the flaw.
</div>

For threats, we need an additional healer specified as an ordering constraint from the threatening action to the savior $a_t \to a_s$ to ensure that the savior acts after the threat and therefore provides the proper fluent for the needer.


<div class="definition" name="Violation degree"> 
The violation degree $v(p)$ of a plan $p$ is an indicator of the violation regarding the initial problem.
We note $v(p) = |flaws(p)| + |saviors(A_p)|$. Said otherwise, it is the sum of the number of flaws and the number of saviors in the plan.
</div>

### Healing process

The healing method is to keep track of reversions in the algorithm by storing the partial plan and the unsatisfiable flaw each time a non deterministic choice fails. We note the set of these failed plans $F$. As the POP algorithm encounters a final failure, this auxiliary algorithm get called. The aim is to evaluate each backtracked partial plan to choose the best one.

Therefore, we add an order relation for $F$ noted 
$$\prec : p \prec q \iff v(p) < v(q) | \{p, q\} \subseteq F$$ 
<!-- FIXME necessary ? -->

Once the POP algorithm fails completely, the soft failing algorithm can be invoked to heal the plan. It chooses the best plan $b | \forall p \in F, b \prec p$ to heal it. If two plans have the same violation degree, the algorithm chooses the first one to have happened.
The healing process is similar to how POP works: we apply the healer of the flaw that caused the failure of the partial plan we chosen. We empty the set $F$ to allow POP to iterate further since the flaw is resolved. The healing process can be done for each unsolved flaws as POP fails repeatedly. This ensures some interesting properties explained in the following section.


# SODA POP and its properties {#soda}
The combination of classical POP and all the auxiliary algorithms presented previously gives SODA POP algorithm detailed in Algorithm 6. In this section, we focus on the properties of SODA POP. <!--that can be achieved by combining them together.-->

<div id="soda" class="algorithm" caption="SODA POP">
\footnotesize
\Function{soda}{Problem $P$}
    \State $P.p \gets$ \Call{properPlan}{$P.G$, $P.A$} \Comment Offline execution
    \State \Call{clean}{$P$}
    \State bool $valid \gets false$
    \While{$\lnot valid$}
        \State $valid \gets$ \Call{pop}{$P$} $= Success$
        \If{$valid$} \State \Call{clean}{$P$} \State \Return $Success$ \EndIf
        \State \Call{heal}{$P$}
    \EndWhile
\EndFunction
</div>

## Convergence property of POP {#convergence}
<!-- FIXME Remove ? -->

First, we prove the convergence of POP. The classic planning problem is already proven to be decidable without functions in the fluents [@ghallab_automated_2004]. Therefore, we can categorize the termination cases.

<div class="proof" name="Proof of convergence">
In the case of a solvable problem, POP is proven to be complete. This ensures that it will find a solution for the problem and therefore terminate. Let $flaws(p)$ be the set of flaws of a given partial plan. The number of flaws is the number of subgoals plus the number of threats since $flaws(p) = subgoals(p) \cup threats(p)$. We consider the number of actions $|A|$ as being finite. Therefore, the number of steps in the plan is at worse $|A_p| = |A|$. We also assume that actions have a finite number of preconditions and effects (since we don't use functions over fluents). This leads to $|subgoals(p)| < \sum_{a \in A_p} |pre(a)| < \infty$ and $|threats(p)| < |L| \le \sum_{a \in A_p} |pre(a)| < \infty$.

This means the number of all possible flaws is finite. As POP resolves these flaws it will decrease their number and iterate over resolvers. The number of resolvers is $|subgoals(p)| * |A| + |threats(p) * 2$ and is also finite. This means that the iteration will in the worst case be equal to the number of resolvers before failing. This proves termination and therefore that POP converges. \qedhere
</div>

## Hypersoundness {#hypersoundeness}
<!-- FIXME Remove ? -->

Now that we proved that regular POP converges, we can introduce the next property: hyper soundness.

<div class="definition" name="Hypersoundness"/>
An algorithm is said to be hypersound when it gives a valid solution for all problems regardless of their resolvability.
</div>

We note that this property isn't compatible with consistency regarding the original problem and then doesn't fit the classical idea of soundness that implicitly states that the validity of a solution is relative to the problem. In the case of hypersoundness, we find a solution to a derived problem $P' = \langle A', I,  G, p\rangle$. We note the new set $A' = A \cup S$ with $S$ being the set of saviors for all flaws that made the POP algorithm fail during the execution of SODA. Next we prove the hypersoundness of our algorithm using the convergence of POP and the way the Soft solving behaves.

<div class="proof" name="Proof of hypersoundness of SODA POP">
The proper plan and defect fixing algorithms are obviously convergent. The proper plan algorithm cannot iterate more than the number of actions (since duplicates are forbidden) and the defect resolution will fix the finite number of defects present in the finite partial plan issued by the proper plan algorithm.

POP is sound and converges. Therefore, if SODA POP converges it will return a valid solution for $P'$. In the same way we proved for POP, the new healing process converges because it reduces the number of flaws in the partial plan and since this number is finite, the algorithm converges.\qedhere
</div>

## Enhancement for online planning

SODA POP algorithm can be used in dynamic online environments to allow a robust way to replan an existing obsolete plan. The first step prior to the runtime is the generation of the proper plans for all the goals that will be considered. 
For each online iteration, we take the previous plan, clean all defects in it and makes it loop between POP and the healing mechanism (if necessary). The defect cleaning will also remove all unnecessary savior actions and eventually make the original problem solvable again. Since each previous plan is almost correct, the algorithm will have little to no iterations to make in order to repair it.


# Experimental results

In order to verify the validity of these algorithms we implemented a prototype in Java. We used basic actions with integer fluents to focus on the way the algorithm behaves.
For the simulation runs, we used a computer with an Intel® Core™ i7-4720HQ CPU clocking 8 cores at 2.60GHz and 8GB of memory. The speed was measured in nano seconds before and after the call to the solve function. The process has a warming up phase that computes random plans for a few seconds before starting the benchmarks.

## Quality of classical POP

The first metric we are interested in is the performance of classical POP algorithm. We measure the solving time for valid problems and the quality of the resulting plans. <!--Figure @fig:quality plots these measures.-->
We tested the algorithm with randomly generated valid problems. We generated those by randomly creating plans based on a difficulty setting that was the upper bound for the number of actions and half the number of possible fluents. This difficulty setting also made the initial and goal step larger accordingly. The actions were cleaned of any toxicity and were built using a forward chaining algorithm. We added also random unrelated actions. Each problem was verified by POP before being generated to ensure resolvability.
As we clearly see in the figure @fig:quality, POP linearly increases its solving time depending on the problem difficulty. That obviously follows the fact that POP has more flaws to fix and therefore more iterations.
As we can observe, POP has a rather high link quality with almost no problems issued with causal link regardless of the difficulty of the problem. This can be linked to either the way we generated our valid plans or to a real tendency of POP to not create that kind of defects on its own.
The action quality on the other hand drops significantly as the difficulty gets higher. This is linked to the number of competing and useless actions that can make the plan simpler but are kept by the flaw selection mechanism of POP.

![Action and link quality and solving time of POP over $10^4$ valid problems for each difficulty ](graphics/quality.svg) {#fig:quality}

## Performance of SODA POP

As SODA POP and regular POP have not the same range of capabilities we can't compare them hand to hand. Indeed, our algorithm always outputs plans with 100% quality since the defect detection system aims to remove all of them. Moreover, our algorithm is hypersound so it gives valid plans for unsolvable problems by derivation. In this specific case POP won't be able to return a result and terminates more quickly as the plan is found unsolvable.

Therefore, we measured the performances of SODA POP algorithm on completely random problems. Each time the algorithm outputs a solution with the lowest possible violation despite the complete invalidity of the input.
In figure @fig:performance we can see the way SODA POP scales up on larger problems.
The performances display logarithmic solving time. This is explained by the fact that POP has to be called recursively on the healed problems. The defect detection participates too in the increase of solving time especially the competing action detection that needs to iterate over all actions and edges and calculate usefulness for two actions each time.

![Average solving time of SODA POP over $10^4$ random problems for each difficulty](graphics/performance.svg) {#fig:performance}

# Conclusion {-}

We defined our SODA POP algorithm and demonstrated its properties. The set of algorithms has extended capabilities of plan repairing and soft solving as shown in examples and simulations. While slower than POP, the resilience of SODA POP can be used in various applications. We aim to use it on the particular case of online plan recognition and decision making as the capabilities to derive plans and repair them will prove useful when comparing potential goals with observed plans.
The algorithm can be improved with a better defect detection algorithm that can rank defects and start fixing the most offensive defects before targeting others. Also, if the competing action detection can be improved, it can be used as heuristic for resolver selection to improve the performances.
Another way the present work can be improved is by extending it to the use of more expressive fluents and transpose all notions to variable domains. One possible goal would be to improve fluent domain with a propositional language in order to significantly boost expressiveness. Finally, we plan to release the software with an open source license soon.


# References
