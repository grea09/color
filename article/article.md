---
title: "SODA POP : Robust online Partial Ordering Planning for real life applications"
author: 
    Antoine GRÉA
    Samir AKNINE
    Laetitia MATIGNON
tags: [nothing, nothingness]
abstract: |
  This is the best of all abstracts ever !

  It consists of two paragraphs but that is more than enough for it to be awesome.
  
bibliography: latex/zotero.bib
documentclass: latex/ieeeconf
csl: latex/ieee.csl
smart: yes
standalone: yes
numbersections: yes
latex-engine: xelatex

---
<!-- TODO discuss order of authors-->

# Introduction {-}
<!-- The Soft Ordering and Defect Aware Partial Ordering Planning algorithm (SODA POP)
This name is PERFECT ! -->

For some time Partial Order Planning (POP) has been the most popular approach to planning resolution. This kind of algorithms are based on *least commitment strategy* on plan step ordering that can allow actions to be flexibly interleaved during execution [@weld_introduction_1994]. Thus the way the search is made using flexible partial plan as a search space allowed for more versatility for a wide variety of uses. As of more recent years, new state space search models and heuristics [@richter_lama_2011] have been demonstrated to be more efficient than POP planners due to the simplicity and relevance of states representation opposed to partial plans [@ghallab_automated_2004]. This have made the search of performance the main axis of research for planning in the community.

While this goal is justified, it shadows other problems that some practical applications cause. For example, the flexibility of Plan Space Planning (PSP) is an obvious advantage for applications needing online planning: plans can be repaired on the fly as new informations and objectives enter the system. The idea of using POP for online planning and repairing plans instead of replanning everything is not new [@van_der_krogt_plan_2005], but has never been paired with the resilience that some other cognitive applications may need, especially when dealing with sensors data and noise.

This resilience allows also other usages as plan manipulation can get easier by fixing errors in the logic of the planning algorithm to make sense of the actions to take **Je ne comprends pas ce que tu veux dire avec cette phrase, pas très claire ou phrase trop longue ... **. These client softwares might provide plans that can contain errors and imperfections that will decrease significantly the efficiency of the computation and the quality of the output plan.

Adding to that, these plans may become totally unsolvable. This problem is to our knowledge not treated in planning of all forms (state planning, PSP, and even constraint satisfaction planning) as usually the aim is to find a solution relative to the original plan (which makes sense). But as we proceed a mechanism of *problem* fixing may be required. This will allow soft solving of any problem regardless of its solvability.

One of the application that needs these improvements is plan recognition with the particular use of inverted off-the-shelf planners to infer the pursued goal of an agent where online planning and resilience is particularly important. ***préciser ?*

These problems call for new ways to improve the answer and behavior of a planner. These improvements must provide relevant plan information pointing out exactly what needs to be done in order to make a planning problem solvable, even in the case of obviously broken input data. We aim to solve this in this paper while preserving the advantages of PSP algorithms (**re-citer entre parenthèses ces avantages**). Our Soft Ordering and Defect Aware Partial Ordering Planning (SODA POP) algorithm will target those issues.

Our new set of auxiliary algorithms allows to make POP algorithm more resilient, efficient and relevant. This is done by pre-emptively computing proper plans for goals, by solving new kinds of defects that input plans may provide, and by healing compromised plan by extending the initial problem to allow resolution.

To explain this work we first describe a few notions, notations and specificities of existing POP algorithms. Then we present and illustrate their limitations, categorising the different defects arising from the resilience problem and explaining our complementary algorithms, their uses and properties. To finish we compare the performance, resilence and quality of POP and our solution.

# Plan Space Planning Definitions
<!-- TODO Scénario -->
In order to present our work and explain examples we introduce a way of representation for schema in figure @fig:legend and notations for mathematical representation.

![Global legend for how partial plans are represented in the current paper](graphics/legend.svg) {#fig:legend}

## Fluents

A fluent is a property of the world. It is often represented by first order logical propositions but in our case we choose to focus on the algorithm and to represent fluents as simple literals (fully instantiated). We note $\lnot f$ the complementary fluent of $f$ meaning that $f$ is true when $\lnot f$ is false and vice-versa.

In order to make our example simpler we use $\mathbb{Q}^*$, the set of relative integers without $0$, as the fluent domain. We use negative integers to represent opposite fluents.

## State
We define a state as a set of fluents. States can be additively combined. We note $s_1 + s_2 = \left( s_1 \cup s_2 \right) - \left\{ f \middle| f \in s_1 \land \lnot f \in s_2  \right\}$ such operation. It is the union of the fluents with an erasure of the complementary ones.

## Action
An action is a state operator. It is represented as a tuple $a = \langle pre, eff \rangle$ with $pre$ and $eff$ being sets of fluents, respectively the preconditions and the effects of $a$. An action can be used only in a state that verifies its preconditions. We note $s \models a \Leftrightarrow pre(a) \subset s$ the verification of an action $a$ by a state $s$.

An action $a$ can be functionally applied to a state $s$ following :
$$a:= \substack{ \left\{ s \models a \middle| s \in S\right\} \to S\\
    a(s) \mapsto s + eff(a)}$$ 
with $S$ being the set of all states.
There are some special names for actions. An action with no preconditions is synonymous to a state and one with empty effect is called a goal.

## Partial Plan
A *partial plan* is a tuple $p = \langle A_p, L\rangle$ where $A_p$ is a set of steps (actions) and $L$ is a set of causal links of the form $a_i \xrightarrow{f} a_j$, such that $\{ a_i, a_j \} \subset A_p \land f \in eff(a_i) \cap pre(a_j)$ or said otherwise that $f$ is provided by $a_i$ to $a_j$ via this causal link. We include the ordering constraints of PSP in the causal links. An ordering constraint is noted $a_i \to a_j$ and means that the plan consider $a_i$ as a step that is prior to $a_j$ without specific cause (usually because of threat resolution).

## Problem
We define a partial plan satisfaction problem as a tuple noted 
$P = \langle A, I, G, p \rangle$ with :

* $I$ and $G$ being the pseudo actions representing respectively the initial state and the goal.
* $p$ being a partial plan to refine.
* $A$ the set of all actions.

## Flaws
When refining a partial plan, we need to fix flaws. Those could be present or be created by the refining process. Flaws can either be unsatisfied subgoals or threats to causal links.

### Subgoals
A subgoal $s$ is a precondition of an action $a_s \in A_p$ with $s \in pre(a_s)$ 
that isn't satisfied by any causal link. We can note a subgoal as :
$$a_i \xrightarrow{s} a_s \notin L \mid \{ a_i, a_s \} \subset A_p $$

A resolver for a subgoal is an action $a_r \in A$ that has $s$ as an effect $s \in eff(a_r)$. It is inserted along with a causal link noted $a_r \xrightarrow{s} a_s$.

### Threats
A step $a_t$ is said to threaten a causal link $a_i \xrightarrow{t} a_j$ if and only if 
$$\neg t \in eff(a_t) \land a_i \succ a_t \succ a_j \models L$$ 
Said otherwise, the action has a possible complementary effect that can be inserted between two actions needing this fluent while being consistant with the ordering constraint in $L$.

The usual resolvers are either $a_t \to a_i$ or $a_j \to a_t$ which are called respectively promotion and demotion links. Another resolver is called a white knight that is an action $a_k$ that reestablish $t$ after $a_t$.

## Consistency
A partial plan is consistent if it contains no ordering cycles. That means that the directed graph formed by step as vertices and causal links as edges isn't cyclical. This is important to guarantee the soundness of the algorithm.

## Flat Plan
We can instantiate one or several flat plans from a partial plan. A flat plan is an ordered sequence of actions $\pi = [ a_1, a_2 \ldots a_n]$ that acts like a pseudo action $\pi = \langle pre_\pi, eff_\pi \rangle$ and can be applied to a state $s$ using functional composition operation $\pi := \bigcirc_{i=1}^n a_n$.

We call a flat plan valid if and only if it can be functionally applied on an empty state. We note that this is different from classic state planning because in our case the initial state is the first action that is already included in the plan.

## Solution
A partial plan which generates only valid flat plans is a solution to the given problem.

# Classical POP
Partial Order Planning (POP) is a popular implementation of the general PSP algorithm. It is proven to be sound and complete [@erol_umcp:_1994]. The completeness of the algorithm guarantees that if the problem has a solution it will be found by the algorithm. The soundness assures that any answer from the algorithm is valid. POP refines a partial plan by trying to fix its flaws.

## Description
From that point the base algorithm is very similar for any implementation of POP : using an agenda of flaws that is efficiently updated after each refinement of the plan. A flaw is selected for resolution and we use a non deterministic choice operator to pick a resolver for the flaw. The resolver is inserted in the plan and we recursively call the algorithm on the new plan. On failure we return to the last non deterministic choice to pick another resolver. The algorithm ends when the agenda is empty or when there is no more resolver to pick for a given flaw. **a voir, mais ca serait peut être intéressant de donner l'algo en pseudo-code, surtout si dans la suite, tu précises à quels endroits tes algo auxiliaires se greffent à l'algo de base ...'**

## Limitations

This standard way of doing have seen multiple improvements over expressiveness like with UCPOP [@penberthy_ucpop:_1992], hierarchical task network to add more user control over sub-plans [@bechon_hipop:_2014], cognition with defeasible reasoning [@garcia_defeasible_2008], or speed with multiple ways to implement the popular fast forward method from state planning [@coles_forward-chaining_2010]. However, all these variants do not treat the problem of online planning, resilience and soft solving. **Et les travaux de replan de Krogt ne traitent pas d'online planning ?'**
Indeed, these problems can affect POP's performance and quality as they can interfere with POP's inner working.

![A simple problem that will be used throughout this paper](graphics/problem.svg) {#fig:problem}

Before continuing, we present a simple example of classical POP execution with the problem represented in figure @fig:problem. We did not represent empty preconditions or effects to improve readability. Here we have an initial state $I = \langle \emptyset , \{ 1, 2 \} \rangle$ and a goal $G = \langle \{ 3, 4, -5, 6 \}, \emptyset \rangle$ encoded as dummy steps. We also introduce actions that are not steps yet but that are provided by $A$. The actions $a$, $b$ and  $c$ are normal actions that are useful to achieve the goal. The action $t$ is meant to be threatening to the plan's integrity and will generate threats. The actions $u$, $v$, $w$ and $x$ are toxic actions **je n'emploierai pas le terme toxic ici, car d'après ta définition de la suite, seules u et v seraient toxic '**. We introduce $u$ and $v$ as useless actions, $w$ as a dead-end action and $x$ as a contradictory action.

![Standard POP result to the problem](graphics/pop.svg) {#fig:pop}

This example has been crafted to illustrate problems with standard POP implementations. We give **a possible resulting plan (POP ne donnera pas toujours ce résultat mais peut renvoyer d'autres plans'?)** the resulting plan of standard POP in figure @fig:pop. We can see some issues as for how the plan has been built. The action $v$ is being used even if it is useless since $b$ already provided fluent $4$. We can also notice that despite being contradictory the action $x$ raised no alarm. As for ordering constraints we can clearly see that the link $a \to t$ is redundant with the path $a \xrightarrow{5} c \to t$ that already puts that constraint by transitivity. Also some problems arise during execution with the selection of $w$ that causes a dead-end.

All these issues are caused by what we call *defects* as they are not regular PSP flaws but they still cause problems with execution and results. We will address these defects and propose a way to fix them in [the next section](#defects).

# Auxiliary algorithms to POP

In order to improve POP algorithms' resilience, online performance and plan quality, we propose a set of auxiliary algorithms that provides POP with a clean and efficiently populated initial plan. 

## Proper plan generation
**Comme preciser dans ton TODO, il manque ici un example; peut être que cela expliquera mieux, car en l'état ca n'est pas très clair **

<!-- TODO Pseudo Algorithme + examples-->
```{#properplan .java caption="Java code for proper plan generation"}
public static Plan properPlan(Goal goal, Set<Action> actions) {
    Plan plan = new Plan();
    Set<Action> relevants = satisfy(goal, actions, plan);
    /* The satisfy function iterate throught participating actions and add the causal links */
    Deque<Action> open = new ArrayDeque<>(relevants);
    //queue of opened relevant actions
    while (!open.isEmpty()) {
        Action action = open.pop();
        Set<Action> candidates = satisfy(action, actions, plan);
        /* Searching for candidates that satisfy the selected providing action*/
        for (Action candidate : candidates) {
            if (!relevants.contains(candidate)) {
                /* Only the one not threated already are added to ensure convergence */
                open.push(candidate);
            }
        }
        relevants.addAll(candidates);
    }
    return plan;
}
```````

As in online planning goals can be known in advance, we add a new mechanism that generates proper plans for goals. **préciser que c'est fait en offline, et que initial step inconnu en offline ** We define for that the concept of *participating action*. An action $a$ participates in a goal $G$ if and only if $a$ has an effect $f$ that is needed to accomplish $G$ or that is needed to accomplish another participating action's preconditions. ** Les participating action sont des actions de l'espace d'action A ? ** A proper plan is a partial plan that contains all participating actions as steps and causal links that bind them with the step they are participating in. This proper plan is independent from the initial step because we might not have the initial step at the time of the proper plan generation. 

This auxiliary algorithm is used as a caching mechanism for online planning. The algorithm starts to populate the proper plan with a quick and incomplete backward chaining. 

This can independently be replaced with a quick forward chaining in other applications and thus benefit from the advantage of state planning and the latest improvement over fast-forward **citer une ref**. 

The aim here is to efficiently populate most of the plan without guarantee about completeness and soundness. This way we can make POP much more efficient as most of the selection is done by the time it starts.

**Il faudrait préciser/détailler quel est l'objectif de cette phase offline et du proper plan: accélérer la phase online, qui se chargera alors de réparer le proper plan ? Le proper plan est le pire plan, ou le meilleur ? Est-il incomplet ? Est-ce que l'on calcule un proper plan pour chaque but ? Préciser ou cela se situe par rapport à l'algo POP que tu auras ajouté avant ...**

## Defect resolution {#defects}
<!-- TODO Pseudo Algorithme + examples-->

When the POP algorithm is used to refine a given plan (that was not generated with POP or that was altered), a set of new defects can be present in it interfering in the resolution and sometimes making it impossible to solve. We emphasize that these defects are not regular POP flaws but new problems that classical POP can't solve. The aim of this auxiliary algorithm is to clean the plans from such defects in order to improve computational time, resilience and plan quality  **préciser ici ou dans section 1 comment est définie la qualité d'un plan**. It should be noted that in some cases cleaning plans will increase the number of flaws in the plan but will always improve the overall quality of it. <!--TODO prove that --> ***Préciser comment/où la résolution de défauts s'ajoute à l'algo POP ... *

There are two kinds of defects: the illegal defects that violate base hypothesis of PSP and the interference defects that can lead to excessive computational time and to poor plan quality.

### Illegal defects
These defects are usually hypothesized out by regular models. They are illegal use of partial plan representation and shouldn't happen under regular circumstances. They may appear if the input is generated by an approximate cognitive model that doesn't ensure consistency of the output or by unchecked corrupted data.**Quelles sont les conséquences de ces défauts: aucun plan retourné ? A préciser**

#### Cycles
A plan cannot contain cycles as it makes it impossible to complete. Cycles are usually detected as they are inserted in a plan but poor input can potentially contains them and breaks the POP algorithm as it cannot undo cycles.

We use a popular and simple Depth First Search (DFS) algorithm to detect cycles. Upon cycle detection the algorithm can remove arbitrarily a link in the cycle to break it. The most effective solution is to remove the link that is the farthest from the goal travelling backward as it would be that link that would have been last added in the regular POP algorithm.
<!--TODO prove that and also code that-->

#### Inconsistent actions
In a plan some actions can be illegal for POP. Those are the actions that are contradictory. An action $a$ is contradictory if and only if 
$$\{f, \lnot f \} \in eff(a) \lor \{f, \lnot f \} \in pre(a)$$

We remove only one of those effect or preconditions based on the usage of the action in the rest of the plan. If none of those are used we choose to remove both.**faire le lien avec action x de example**

#### Toxic actions
These actions are those that have effects that are already in their preconditions. This can damage a plan as well as make the execution of POP algorithm much longer than necessary. They are defined as :
$$a | pre(a) \cap eff(a) \neq \emptyset$$

This is fixed of one of two ways : if the action has only some of its fluent toxic ($pre(a) \nsubseteq eff(a)$) then the toxic fluents are removed following $eff(a) = eff(a)-pre(a)$, otherwise the action is removed alltogether from plan and $A$. **faire le lien avec action de example**

#### Liar links
The defects can be related to incorrect links. The first of which are liar links : a link that doesn't reflect the preconditions or effect of its source and target. We can note 
$$a_i \xrightarrow{f} a_j | f \notin eff(a_i) \cap pre(a_j)$$

These can form with the way inconsistent actions are fixed : a deleted fluent could still have links in the plan **je ne comprends pas cette phrase**.

To resolve the problem we either replace $f$ with a saviour, i.e. a fluent in $eff(a_i) \cap pre(a_j)$ that isn't already provided, or we delete the link all together.

### Interference defects
This kind of defects is not as toxic as the illegal ones: they won't make the plan unsolvable but they can still cause performance drops in POP execution. These can appear more often in regular POP results as they are not targeted by standard implementations. **Il faudrait préciser pour chaque defaut suivant, pourquoi ils entrainent des diminutions de performance de POP**

#### Redundant links
This defect can happen in POP generated plans to some extends. A redundant link have a transitive equivalent of longer length. It means that a link $a_i \to a_j$ is redundant if and only if it exists another path from $a_i$ to $a_j$ of length greater to $1$. Since POP relies on those additional links, this part focus on removing the ones that were generated for threat removal purpose to simplify the plan. **manque ici un example ou explication: si POP est basé sur ces liens additionnels, n'est-ce pas un risque de les enlever ? Et enlever des liens générés pour résoudre des menaces ne va-t-il pas faire réapparaitre ces menaces ? Quel est le gain à enlever les liens redondants ?**

#### Competing causal links
Causal links can be found to compete with one another. A competing link $a_i \xrightarrow{f} a_k$ competes with another link $a_j \xrightarrow{f} a_k$ if it provides the same fluent to the same action. This cannot happen in ** classical POP algorithm so it is not handled by it (plutôt que "but won't be fixed by the algorithm"**. **préciser pourquoi ces liens diminuent les perf de POP. Peut être faire le lien avec le besoin d'enlever l'action la moins intéressant, dont tu parles dans la suite mais on ne voit pas vraiment le lien ...**

In order to prune the least useful actions **?**, we need to remove the least interesting link. In order to elect the best one **link ?**, we compare their respective providing action. We choose the link having the providing action with the smaller outgoing degree in the planning graph. **preciser outgoing degree** This indicates that the action is participating to more other actions. If both actions have the same outgoing degree then we remove the action with the most incoming degree **? je ne comprends pas**. This means that we remove the more needy action **je ne comprends pas**. 

**Paragraph précédent pas clair, à reprendre et avec un example**

#### Useless actions
Actions can sometimes have no use in a plan as they don't contribute to it. It is the case of orphans actions (actions without any links) and **(proposition de changement de phrase:) of dead end actions in a completed plan (action with no outgoing path to the goal) **. We also consider useless actions that have no effects (except the goal step).
<!-- FIXME make that an option since Ramirez's method needs this to be off-->

## Soft Resolution
<!-- TODO Pseudo Algorithme + examples-->

This auxiliary algorithm is meant to deal with failure. It will heal the plan to make the failure recoverable for the next iteration of POP. Of course it can't fix the plan by keeping the problem as it is. This obviously breaks some properties as the algorithm no longer adheres to the specification of the input, but in exchange it will always issue a valid plan whatever happens. For more information on this property go take a look at the  [appropriate section bellow](#hypersoundness).

Soft failure is useful when the precision and validity of the output is not the most important criteria we look for. In some cases (like in recognition processes) it is more useful to have an output even if it is not exact than no output at all. That is why we propose a soft failing mechanism for POP algorithms.

We define first some new notions, then we will explain the healing algorithm.

### Needer
A needer is an action that needs a resolution related to a flaw. We define different types of needer according to the type of the flaw.

#### Subgoal needer
For a subgoal $a_n \xrightarrow{s} a_s$ the needer is the action $a_n$ that has an unsatisfied precondition in the current partial plan.

#### Threat needer
The needer of a threat $a_t$ of a link $a_p \xrightarrow{t} a_n$ is the target $a_n$ of the threatened causal link.

### Proper fluents
A proper fluent of a flaw is the one that caused the flaw. For a subgoal $a_n \xrightarrow{s} a_s$ it is the unsatisfied precondition $s$. For a threat $a_t$ of a causal link $a_p \xrightarrow{t} a_n$ it is the fluent $t$ held by the threatened causal link.

### Saviour
The saviour of a flaw is the forged action $a_s = \langle \emptyset, \{p\} \rangle | a_s \notin A$ with $p$ being the proper fluent of the flaw.

### Healers
The concept of healer is made to target rogue flaws that caused total failure. A healer is a resolver that is built around the saviour of the flaw to provide for it **phrase pas claire**. The general formula of a healer is the following :
$$a_s \xrightarrow{p} a_n$$
with $a_s$ being the saviour of the flaw.

For threats we need an additional healer specified as an ordering constraint from the threatening action to the saviour $a_t \to a_s$ to ensure that the saviour acts after the threat and therefore provides the proper fluent for the needer.

### Violation degree
The violation degree $v(p)$ of a plan $p$ is an indicator of the health of a partial plan. It is the sum of the number of flaws and the number of saviours in the plan.

### Healing process

The healing method is to keep track of reversions in the algorithm by storing the partial plan and the unsatisfiable flaw each time a non deterministic choice fails. We note the set of these failed plans $F$. As the POP algorithm encounters a final failure, this auxiliary algorithm get invoked. The aim is to evaluate each backtracking partial plan to choose the best one.

Therefore we add an order relation for $F$ noted 
$$\prec : p \prec q \iff v(p) < v(q) | \{p, q\} \subset F$$ <!-- FIXME necessary ? -->

Once the POP algorithm fails completely the soft failing algorithm can be invoked to heal the plan. It chooses the best plan $b | \forall p \in F, b \prec p$ to heal it. If two plans have the same violation degree, the algorithm chooses one arbitrarily <!-- FIXME better if chosen by order of occurence-->.

The healing process is similar to how POP works : we apply the healer of the flaw that caused the failure of the partial plan we chosen. We empty the set $F$ to allow POP to iterate further since the flaw is resolved. The healing process can be done for each unsolved flaws as POP fails repeatedly. This ensures some interesting properties **lesquelles ?**.

**Donner pseudo code de l'algo**


# Properties of the algorithms ***SODA POP properties ?*
After defining the way our algorithms work we will focus on the properties that can be achieved by combining them together.
**La combinaison de toutes les méthodes proposée donne SODA POP ? Le préciser ici (sinon SODA POP n'apparait que dans l'intro et le titre, il faut dire à un moment ce que c'est ! **

## Convergence of POP {#convergence}
As to our knowledge no proof of the convergence of POP has been done we want to explicitly formulate one. 

The classic planning problem is already proven to be decidable without functions in the fluents [@ghallab_automated_2004]. Therefore we can categorise the termination cases. In the case of a solvable problem, POP is proven to be complete. This ensures convergence in that case. Now for the more complex case of unsolvable problems we need to refer to the way POP works. POP algorithm will seek to solve flaws. At any time there is a finite number of flaws since the plans have a finite number of steps. As POP resolves these flaws it will either continue until resolution or until failure. The problem is that POP can encounter loops in the dependancies of actions or in threats resolution. These loops can't occur in POP algorithm since a cycle in the ordering constraints instantly causes a failure as the plan isn't consistent anymore. This prooves that POP always converges. <!-- FIXME Moar maths ! -->

## **SODA POP Hyper soundness ?** Hyper soundness {#hypersoundeness}
Now that we proved that regular POP converges we can introduce the next property : hyper soundness. An algorithm is said to be hyper sound when it gives a valid solution for all problems including unsolvable ones. We note that this property isn't compatible with consistency regarding the original problem but still does regarding the derived problem that includes all fake actions in $A$. <!-- TODO note mathematically an A' and explain with more details -->

The hyper soundness of our combined algorithm is proven using the convergence of POP and the way the Soft solving behaves. As a POP fails it will issue flawed partial plans. As we fix the flaws artificially we make sure that this failure won't happen again in the next iteration of POP on the fixed plan. As the number of flaws is finite and POP converges, the whole algorithm will converge with all flaws solved therefore issuing a valid plan. <!-- FIXME Moar maths ! -->

## Enhancement for online planning

# Experimental results
<!-- TODO obviously -->

# Conclusion {-}

# References

