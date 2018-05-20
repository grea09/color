---
title: "HEART: HiErarchical Abstraction for Real-Time partial order planning"
author: 
 - Paper \#1788
# - Antoine Gréa
# - Laetitia Matignon
# - Samir Aknine
#institute: first.lastname@liris.cnrs.fr, LIRIS UMR5205, University of Lyon, 69622 Villeurbanne, France
tags: [Activity and Plan Recognition, Hierarchical planning, Planning Algorithms, Real-time Planning, Robot Planning, POP, Partial Order Planning, POP, HTN]
abstract: When asked about the biggest issues with automated planning, the answer often consists of speed, quality, and expressivity. However, certain applications need a mix of these criteria. Most existing solutions focus on one area while not addressing the others. We aim to present a new method to compromise on these aspects in respect to demanding domains like assistive robotics, intent recognition, and real-time computations. The HEART planner is an anytime planner based on a hierarchical version of Partial Order Planning (POP). Its principle is to retain a partial plan at each abstraction level in the hierarchy of actions. While the intermediary plans are not solutions, they meet criteria for usages ranging from explanation to goal inference. This paper also evaluates the variations of speed/quality combinations of the abstract plans in relation to the complete planning process.


style: ijcai
---

# Introduction {-}

One of the most discussed issues in the domain of assistive robotics is intent recognition. Recently some new methods emerged to tackle this problem [@talamadupula_coordination_2014]. However, the domain requires being able to solve expensive human-related problems on robots with limited processing power and to provide real-time answers [^polling].

[^polling]: Within a polling period of around 100ms.

Planners are not meant to solve intent recognition problems. However, several works extended what is called in psychology as the *theory of mind*.
That theory is the equivalent of asking "*what would **I** do if I was them ?*" when observing the behavior of other agents. This leads to new ways to use *inverted planning* as an inference tool.
One of the first to propose that idea was @baker_goal_2007 that use Bayesian planning to infer intentions. @ramirez_plan_2009 found an elegant way to transform a plan recognition problem into classical planning. This is done simply by encoding observation constraints in the planning domain [@baioletti_encoding_1998] to ensure the selection of actions in the order they were observed. A cost comparison will then give a probability of the goal to be pursued given the observations.
Some works extended this with multi-goal recognition [@chen_planning_2013]. A new method, proposed by @sohrabi_plan_2016, makes the recognition fluent centric. It assigns costs to missing or noisy observed fluents [^fluent]. This method also uses a meta-goal that combines each possible goal and is realized when at least one of these goals is satisfied. Sohrabi *et al.* state that the quality of the recognition is directly linked to the properties of the generated plans. Thus guided diverse planning[^diverse] was preferred along with the ability to infer several probable goals at once.

[^fluent]: Working with fluents allows for finer probabilities with less detailed observations than when doing so with actions' costs.

[^diverse]: Diverse planning is set to find a set of $m$ plans that are distant of $d$ from one another.

The impact of problems size on performance is an issue of automated planning as it has been proven to be a *P-SPACE* problem if not harder [@weld_introduction_1994]. Sizable problems are usually intractable within the tight constraints of real-time robotics. The problem of dealing with unsolvability has already been addressed by @gobelbecker_coming_2010 where "excuses" are being investigated as potential explanations for when a problem has no solutions. The closest way to address time unsolvability is by using explainability [@fox_explainable_2017]. In our context, we do not need the complete solution as much as an explanation of the plan.

For intent recognition, an important metric is the number of correct fluents. So if finding a complete solution is impossible, a partial solution can meet enough criteria to give a good approximation of the goal probability. One of the main approaches to automated planning is called **Partial Order Planning (POP)**. It works by refining partial plans into a solution. The process and resulting plans are more flexible than classical ones. Another approach is **Hierarchical Task Networks (HTN)** that is meant to tackle the problem using composite actions in order to define hierarchical tasks within the plan. These two approaches are not exclusive and have been combined in previous works [@gerevini_combining_2008]. Our work is based on **Hierarchical POP (HiPOP)** by @bechon_hipop_2014. The idea is to expand the classical POP algorithm with new flaws in order to make it compatible with HTN problems. Planning with HTN results in high-level plans which are useful when applied to intent recognition [@holler_plan_2018].

In the rest of the paper, we detail how HiErarchical Abstraction for Real-Time partial order planning (HEART) creates abstract intermediary plans that can be used for intent recognition within our time constraints. First, we present updated definitions for POP, then we explain our approach and prove its properties to finally discuss the experimental results.

# Definitions

**Symbol**                    **Description**
----------                    ---------------
$\mathcal{D}, \mathcal{P}$    Planning domain and problem.
$pre(a)$, $eff(a)$            Preconditions and effects of the action $a$.
$methods(a)$                Methods of the action $a$.
$lv(a), lv(\mathcal{D})$    Abstraction level of the action $a$ or domain $\mathcal{D}$.
$\phi^\pm(l)$                Signed incidence function for partial order plans.
                            $\phi^-$ gives the source and $\phi^+$ the target step of $l$.
                            No sign gives a pair corresponding to link $l$.
$L^\pm(a)$                    Set of incoming ($L^-$) and 
                            outgoing ($L^+$) links of step $a$.
                            No sign gives all adjacent links.
$a_s \xrightarrow{c} a_t$    Link with source $a_s$, target $a_t$ and cause $c$.
$causes(l)$                    Gives the causes of a causal link $l$.
$a_a \succ a_s$                A step $a_a$ is anterior to the step $a_s$.
$l \downarrow a$            Link $l$ participates in the partial support of step $a$.
$\pi \Downarrow a$            Plan $\pi$ fully supports $a$.
$\downarrowbarred_f a$        Fluent $f$ isn't supported in step $a$.
$a_b \olcross l$            Breaker action $a_b$ threatens causal link $l$.
$a \oplus_m$                Expansion of composite action $a$ using method $m$.
$A_x^n$                        Proper actions set of $x$ down $n$ levels.
                            $A_x$ for $n=1$ and $A_x^*$ for $n=lv(x)$.
$a \rhd^\pm a'$                Transpose the links of action $a$ onto $a'$.
$[exp]$                        Iverson's brackets: $0$ if $exp=false$, $1$ otherwise.

: Our notations are adapted from [@ghallab_automated_2004]. The symbol $\pm$ is only used when the notation has signed variants. {#tbl:symbols}

Planners often work in two phases: first we compile the planning domain then we give the planner an instance of a corresponding planning problem to solve.

## Domain

The domain specifies the allowed operators that can be used to plan and all the fluents they use as preconditions and effects.

::: {.definition name="Domain"} :::
A domain is a tuple $\mathcal{D} = \langle C_\mathcal{D} , R, F, O \rangle$ where:

* $C_\mathcal{D}$ is the set of **domain constants**.
* $R$ is the set of **relations** (also called *properties*) of the domain. These relations are similar to quantified predicates in first order logic.
* $F$ is the set of **fluents** used in the domain to describe operators.
* $O$ is the set of **operators** which are fully lifted *actions*.
:::::::::::::::::::::::::::::::::::

*Example*: The example domain in @lst:domain is inspired from the kitchen domain of @ramirez_probabilistic_2010.

~~~~ {#lst:domain}
take(item) pre (taken(~), ?(item)); //?(item) to make item into a variable
take(item) eff (taken(item));
heat(thing) pre (~(hot(thing)), taken(thing));
heat(thing) eff (hot(thing));
pour(thing, into) pre (thing ~(in) into, taken(thing));
pour(thing, into) eff (thing in into);
put(utensil) pre (~(placed(utensil)), taken(utensil));
put(utensil) eff (placed(utensil), ~(taken(utensil)));
infuse(extract, liquid, container) :: Action; //Level 1
make(drink) :: Action; // Level 2
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
: Domain file used in our planner. Composite actions have been truncated for lack of space.

::: {.definition name="Fluent"} :::
A fluent $f$ is a parameterized boolean relation $r(arg_1, arg_2, …, arg_n)$ where:

* $arg_i | i \in [1,n]$ are the arguments (possibly quantified).
* $n = |r|$ is the arity of $r$.

Fluents are signed. Negative fluents are noted $\neg f$ and behave as a logical complement. The quantifiers are affected by the sign of the fluents. We don't use the closed world hypothesis: fluents are not satisfied until provided whatever their sign. 
:::::::::::::::::::::::::::::::::::

*Example*: To describe an item not being held we use the fluent $\neg taken(item)$. If the cup contains water $in(water, cup)$ is true.

The central notion of planning is operators. Instanciated operators are usually called *actions*. In our framework, actions can be partially instantiated. We use the term action for both lifted and grounded operators.

::: {.definition #def:action name="Action"} :::
An action is a parametrized tuple $a(args)=\langle name, pre, eff, methods \rangle$ where:

* $name$ is the **name** of the action.
* $pre$ and $eff$ are sets of fluents that are respectively the **preconditions and the effects** of the action.
* $methods$ is a set of **methods** (partial order plans) that can realize the action. Methods, and their enclosed actions' methods, cannot contain the parent action.
::::::::::::::::::::::::::::::::::::::::::::::::::

*Example*: The precondition of the operator $take(item)$ is simply a single negative fluent noted $\neg taken(item)$ ensuring the variable $item$ isn't already taken.

*Composite* actions are represented using methods. An action without methods is called *atomic*. Methods are plans used only in HTN variants of POP.

::: {.definition name="Plan"} :::
A partially ordered plan is an *acyclic* directed graph $\pi = (S, L)$, with:

* $S$ the set of **steps** of the plan as vertices. A step is an action belonging in the plan. $S$ must contain an initial step $I_\pi$ and goal step $G_\pi$.
* $L$ the set of **causal links** of the plan as edges.
We note $l: a_s \xrightarrow{c} a_t$ the link between its source $a_s$ and its target $a_t$ caused by the set of fluent $c$.
:::::::::::::::::::::::::::::::::

In HEART, *ordering constraints* are defined as the transitive cover of causal links over the set of steps. We note ordering constraints: $a_a \succ a_s$, with $a_a$ being *anterior* to its *successor* $a_s$. Ordering constraints can't form cycles, meaning that the steps must be different and that the successor can't also be anterior to its anterior steps:
$a_a \neq a_s \land a_s \not \succ a_a$.
In all plans, the initial and goal steps have their order guaranteed: $I_\pi \succ G\pi \land \nexists a_x \in S_\pi, a_x \succ I_\pi \lor G_\pi \succ a_x$.
If we need to enforce order, we simply add a causal link without any cause. The use of graphs and implicit order constraints help to simplify the model while maintaining its properties.

Our representation for methods is very simplified and only gives causeless causal links. The causes of each link to the preconditions and effects of composite actions are inferred. This inference is done by running an instance of POP on the methods of each composite action $a$ while compiling the domain. We use the following formula to compute the final preconditions and effects of $a$: $pre(a) = \bigcup_{a_s \in L^+(a)} causes(a_s)$ and $eff(a) = \bigcup_{a_s \in L^-(a)} causes(a_s)$. Errors are reported if POP cannot be completed or if methods contain their parent action as a step or as a step of any nested composite actions' methods ($a \notin A_a^*$, see @def:proper).

## Problem

Problems instances are often most simply described by two components: the initial state and the goal.

::: {.definition name="Problem"} :::
The planning problem is defined as a tuple $\mathcal{P} = \langle \mathcal{D}, C_\mathcal{P} , \Omega\rangle$ where:

* $\mathcal{D}$ is a planning domain.
* $C_\mathcal{P}$ is the set of **problem constants** disjoint from the domain constants.
* $\Omega$ is the problem's **root operator** which methods are solutions of the problem.
::::::::::::::::::::::::::::::::::::

*Example*: We use a simple problem for our example domain. The initial state provides that nothing is ready, taken or hot and all containers are empty (all using quantifiers). The goal is to have tea made. For reference, @lst:problem contains the problem instance we use as an example.

~~~~{#lst:problem}
init eff (hot(~), taken(~), placed(~), ~ in ~);
goal pre (hot(water), tea in cup, water in cup, placed(spoon), placed(cup));
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
: Example of a problem instance for the kitchen domain.

The root operator is initialized to $\Omega = \langle "", s_0, s^*, 
\lbrace \pi \rbrace\rangle$, with $s_0$ being the initial state and $s^*$ the goal specification. The method $\pi$ is a partial order plan with only the initial and goal steps linked together.
The initial partial order plan is $\pi = (\lbrace I,G \rbrace, \lbrace I \rightarrow G \rbrace)$, with $I = \langle "init", \emptyset, s_0, \emptyset\rangle$ and $G = \langle "goal", \emptyset, s^*, \emptyset\rangle$.

## Partial Order Planning

Our method is based on the classical POP algorithm. It works by refining a partial plan into a solution by recursively removing all its flaws.

::: {.definition name="Flaws"} :::
Flaws have a *proper fluent* $f$ and a cause step often called the *needer* $a_n$. Flaws in a partial plan are either:

* **Subgoals**, *open conditions* that are yet to be supported by another step $a_n$ often called *provider*. We note subgoals $\downarrowbarred_f a_n$ (see @def:support).
* **Threats**, caused by steps that can break a causal link with their effects. They are called *breakers* of the threatened link. A step $a_b$ threatens a causal link $a_p \xrightarrow{f} a_n$ if and only if $\neg f \in eff(a_b) \land a_p \succ a_b \succ a_n$. Said otherwise, the breaker can cancel an effect of a providing step $a_p$, before it gets used by its needer $a_n$. We note threats $a_b \olcross a_p \xrightarrow{f} a_n$
::::::::::::::::::::::::::::::::::

*Example*: Our initial plan contains two unsupported subgoals: one to make the tea ready and another to put sugar in it. 
In this case, the needer is the goal step and the proper fluents are each of its preconditions.

These flaws need to be fixed in order for the plan to be valid. In POP it is done by finding their resolvers.

::: {.definition name="Resolvers"} :::
Classical resolvers are additional causal links that aim to fix a flaw.

* For subgoals, the resolvers are a set of potential causal links containing the proper fluent $f$ in their causes while taking the needer step $a_n$ as their target.
* For threats, we usually consider only two resolvers: *demotion* ($a_b \succ a_p$) and *promotion* ($a_n \succ a_b$) of the breaker relative to the threatened link.
::::::::::::::::::::::::::::::::::::::

*Example*: The subgoal for $in( water, cup )$, in our example, can be solved by using the action $pour(water, cup)$ as the source of a causal link carrying the proper fluent as its only cause.

The application of a resolver does not necessarily mean progress. It can have consequences that may require reverting its application in order to be able to solve the problem.

::: {.definition #def:side-effects name="Side effects"} :::
Flaws that are caused by the application of a resolver are called *related flaws*. They are inserted into the *agenda* [^agenda] with each application of a resolver:

* *Related subgoals* are all the new open conditions inserted by new steps.
* *Related threats* are the causal links threatened by the insertion of a new step or the deletion of a guarding link.

Flaws can also become irrelevant when a resolver is applied. It is always the case for the targeted flaw, but this can also affect other flaws. Those *invalidated flaws* are removed from the agenda upon detection:

* *Invalidated subgoals* are subgoals satisfied by the new causal links or the removal of their needer.
* *Invalidated threats* happen when the breaker no longer threatens the causal link because the order guards the threatened causal link or either of them have been removed.
:::::::::::::::::::::::::::::::::::::::::

[^agenda]: Flaw container used for POP's flaw selection.

*Example*: Adding the action $pour(water, cup)$ causes a related subgoal for each of the action's preconditions which are: the cup and the water must be taken and water must not already be in the cup.

In @def:side-effects, we mentioned effects that aren't present in classical POP, namely *negative effects*. All classical resolvers only add steps and causal links to the partial plan. Our method needs to remove composite steps and their adjacent links when expanding them.

In @alg:pop we present a generic version of POP [@ghallab_automated_2004, section 5.4.2].

::: {.algorithm #alg:pop name="Partial Order Planner" startLine="1"}
\Function{pop}{Agenda $a$, Problem $\mathcal{P}$}
    \If{$a = \emptyset$} \Comment{Populated agenda of flaws needs to be provided}
        \State \Return Success \Comment{Stops all recursion}
    \EndIf
    \State Flaw $f \gets$ \Call{choose}{$a$} \label{line:flawselection}
    \Comment{Heuristically chosen flaw removed from agenda}
    \State Resolvers $R \gets$ \Call{solve}{$f$, $\mathcal{P}$}
    \ForAll{$r \in R$} \Comment{Non-deterministic choice operator}
        \State \Call{apply}{$r$, $\pi$} \label{line:resolverapplication} 
        \Comment{Apply resolver to partial plan}
        \State Agenda $a' \gets$ \Call{update}{$a$} \label{line:updateagenda}
        \If{\protect\Call{pop}{$a'$, $\mathcal{P}$} = Success} \Comment{Refining recursively}
            \State \Return Success
        \EndIf
        \State \Call{revert}{$r$, $\pi$} \Comment{Failure, undo resolver application}
    \EndFor
    \State $a \gets a \cup \{f\}$ \Comment{Flaw wasn't resolved}
    \State \Return Failure \Comment{Revert to last non-deterministic choice of resolver}
\EndFunction
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

# The heart of the approach

In this section, we explain how our method combines POP and HTN and how they are used to generate intermediary abstract plans.

## Additional notions

In order to properly introduce the changes made for using HTN domains in POP, we need to define a few notions.

::: {.definition name="Transposition"} :::
In order to transpose the causal links of an action $a'$ with the ones of an existing step $a$, we do the following operation : $a \rhd^- a = \lbrace \phi^-(l) \xrightarrow{causes(l)} a', l \in L^-_\pi(a) \rbrace \cup (L^-_\pi \setminus L^-_\pi(a))$ and the same with $a' \xrightarrow{causes(l)} \phi^+(l)$ and $L^+$ for $a \rhd^+ a'$. This supposes that the respective preconditions and effects of $a$ and $a'$ are equivalent. When not signed, the transposition is generalized: $a \rhd a' = a\rhd^-a' \cup a\rhd^+a'$.
::::::::::::::::::::::::::::::::::::::::

*Example*: Using $a\rhd^-a'$ means that all incoming links of $a$ are now incoming links of $a'$ instead.

::: {.definition #def:proper name="Proper actions"} :::
Proper actions are actions that are "contained" within an entity:

* For a *domain* or a *problem* it is $A_{\mathcal{D}|\mathcal{P}} = O$.
* For a *plan* it is $A_\pi = S_\pi$.
* For an *action* it is $A_a = \bigcup_{m \in methods(a)} S_m$. Recursively: $A_a^n = \bigcup_{a'\in A_a} A_{a'}^{n-1}$.

We note $A_a^* = A_a^{lv(a)}$ the complete set of all proper actions of the action $a$. 
:::::::::::::::::::::::::::::::::::::::::::

*Example*: The proper actions of $make(drink)$ are the actions contained within its methods. The complete set of proper actions adds all proper actions of its single proper composite action $infuse(drink, water, cup)$.

::: {.definition #def:level name="Abstraction level"} :::
This is a measure of the maximum amount of abstraction an entity can hold [^iverson]: 
$$lv(x) = \left ( \max_{a \in A_x}(lv(a)) + 1 \right ) [A_x \neq \emptyset]$$
::::::::::::::::::::::::::::::::::::::::::::::

[^iverson]: We use Iverson brackets here, see notations in @tbl:symbols.

*Example*: The abstraction level of any atomic action is $0$ while it is $2$ for the composite action $make(drink)$. The example domain (in @lst:domain) has an abstraction level of $3$.

## Abstraction in POP

The most straightforward way  to handle abstraction with POP is illustrated in another planner called Duet [@gerevini_combining_2008] by managing hierarchical actions separately from a regular planner. We chose another way strongly inspired by the works of  @bechon_hipop_2014 on a planner called HiPOP. The difference between the original HiPOP and our implementation of it is that we focus on the expressivity and the ways flaw selection can be exploited for partial resolution. Our version is lifted at runtime while the original is grounded for optimizations. All approaches we have implemented use POP but with different management of flaws and resolvers. The @alg:pop is left untouched.

::: {.definition name="Abstraction flaw"} :::
It occurs when a partial plan contains a non-atomic step. This step is the needer $a_n$ of the flaw. We note it $a_n \oplus$.

* *Resolvers*: An abstraction flaw is solved with an **expansion resolver**. The resolver will replace the needer with one of its instantiated methods in the plan. This is done by linking all causal links to the initial and goal steps of the method as such:
$a_n \rhd^- I_m \land a_n \rhd^+ G_m$, with $m \in methods(a_n)$.
* *Side effects*: An abstraction flaw can be related to the introduction of a composite action in the plan by any resolver and invalidated by its removal.
:::::::::::::::::::::::::::::::::::::::::::::

*Example*: When adding the step $make(tea)$ in the plan to solve the subgoal that needs tea being made, we also introduce an abstraction flaw that will need this composite step replaced by its method using an expansion resolver.

The main differences between HiPOP and HEART in our implementations are the functions of flaw selection and the handling of the results (one plan for HiPOP and a plan per cycle for HEART).
In HiPOP, the flaw selection is made by prioritizing the abstraction flaws. The authors state that it makes the full resolution faster. However, it also loses opportunities to obtain abstract plans in the process.

## Cycles

![The cycle process on the example domain. Atomic actions that are copied from a cycle to the next are omitted.](graphics/cycles.old.pdf){#fig:cycles .wide}

The main focus of our work is toward obtaining **abstract plans** which are plans that are completed while still containing composite actions. In order to do that the flaw selection function will enforce cycles in the planning process.

::: {.definition name="Cycle"} :::
During a cycle, all abstraction flaws are delayed. Each cycle has a designated *abstraction level* that limits the resolver selection for subgoals to providers with an abstraction level less than or equal to it. Once no more flaws other than abstraction flaws are present in the agenda, the current plan is saved and all remaining abstraction flaws are solved at once before the abstraction level is lowered for the next cycle. Each cycle produces a more detailed abstract plan than the one before.
:::::::::::::::::::::::::::::::::::::::

Abstract plans allow the planner to do an approximative form of anytime execution. At any given time the planner is able to return a fully supported plan. Before the first cycle, the plan returned $\pi_\Omega$ is the following $I \xrightarrow{s_0} \Omega \xrightarrow{s^*} G$. We use the root operator to indicate that no cycles have been completed.

*Example*: In our case using the method of intent recognition of @sohrabi_plan_2016, we can already use $\pi_\Omega$ to find a likely goal explaining an observation (a set of temporally ordered fluents). That can make an early assessment of the probability of each goal of the recognition problem.

These intermediary plans are not solutions of the problem. However, they have some interesting properties that make them useful. In order to find a solution, the HEART planner needs to reach the final cycle of abstraction level $0$.

*Example*: In the @fig:cycles, we illustrate the way our example problem is progressively solved. Before the first cycle (level $3$) all we have is the root operator. Then within the first cycle, we select the composite action $make(tea)$ instanciated from the operator $make(drink)$ along with its methods. All related flaws are fixed until all that is left in the agenda is the abstract flaws. We save the partial plan for this cycle and expand $make(tea)$ into a copy of the current plan for the next cycle.

# Properties

## Soundness {#sec:sound}

For an algorithm to be sound, it needs to provide only *valid* solutions. In order to prove soundness, we first need to define the notion of support.

::: {.definition #def:support name="Support"} :::
An open condition $f$ of a step $a$ is supported in a partial order plan $\pi$ if and only if $\exists l \in L^-_\pi(a) \land \nexists a_b \in S_\pi:
f \in causes(l) \land \left ( \phi^-(l) \succ a_b \succ a \land \neg f \in eff(a_b) \right )$. This means that the fluent is provided by a causal link and isn't threatened by another step. We note support $\pi \downarrow_f a$.

**Full support** of a step is achieved when all its preconditions are supported: $\pi \Downarrow a\equiv \forall f \in pre(a): \pi \downarrow_f a$.
::::::::::::::::::::::::::::::::::::

We also need to define validity in order to derive all its logical equivalences for the proofs:

::: {.definition name="Validity"} :::
A plan $\pi$ is a valid solution of a problem $\mathcal{P}$ if and only if $\forall a \in S_\pi: \pi \Downarrow a \land lv(a) = 0$.
:::::::::::::::::::::::::::::::::::::

We can now start to prove the soundness of our approach. We base this proof upon the one done in [@erol_umcp_1994]. It states that for classical POP if a plan doesn't contain any flaws, it is fully supported. Our main difference being with abstraction flaws we need to prove that its resolution doesn't leave classical flaws unsolved in the resulting plan.

::: {.lemma name="Expansion with an empty method"} :::
When a fully supported composite action $\pi \Downarrow a$ is expanded using an empty method $m = \left ( \lbrace I_m, G_m \rbrace, \lbrace I_m \rightarrow G_m \rbrace \right )$, adding all open conditions of $G_m$ as subgoals will result in a plan $\pi'$, without any undiscovered flaws.
:::::::::::::::::::::::::::::::::::::::::::

::: proof :::
The initial and goal steps of a method follows ($pre(a) = eff(a)$). By definition of full support:
$$L^-_{\pi'}(I_m) = L_\pi^-(a) \land pre(I_m) = pre(a) \implies \left ( \pi \Downarrow a \equiv \pi' \Downarrow I_m \right )$$

The only remaining undiscovered open conditions in the plan $\pi'$ are therefore those caused by $G_m$: $\lbrace \pi' \downarrowbarred_f G_m: f \in pre(G_m) \rbrace$.

No new threats are introduced since the link between $I_m$ and $G_m$ is causeless and they inherit the order of the parent composite action. All added actions are atomic so no abstraction flaws are added.
:::::::::::::

::: {.lemma name="Expansion with an arbitrary method"} :::
If a fully supported composite action $\pi \Downarrow a$ is replaced by an arbitrary method $m$ and all open conditions contained within $S_m$ as subgoals and all threatened links within $L_m$ as threats are added to the agenda, the resulting plan $\pi'$ will not have any undiscovered flaws.
:::::::::::::::::::::::::::::::::::::::::::

::: proof :::
When replacing a composite action with a method in an existing plan we do the following operations: $S_{\pi'} = (S_\pi \setminus a) \cup S_m$, $L_{\pi'}(I_m) = a \rhd I_m \cup (L_\pi \setminus L_\pi(a))$. The only added flaws are then:
$$\bigcup_{f \in pre(a_m)}^{a_m \in S_m} \pi' \downarrowbarred_f a_m\bigcup_{l \in L_{\pi'}}^{a_b \in S_{\pi'}} a_b \olcross l \bigcup_{a_c \in S_m}^{lv(a_c) \neq 0} a_c \oplus$$

That means that all added actions have their subgoals considered and all links have their threats taken into account along with all additional abstraction flaws.

This proves that expansion does not introduce flaws that are not added to the POP agenda. Since POP must resolve all flaws in order to be successful and according to the proof of POP's soundness, HEART is sound as well.
:::::::::::::

Another proven property is that intermediary plans are valid in the classical definition of the term (without abstraction flaws) and that when using only this definition, HEART is sound on its anytime results too.

## Completeness
The completeness of POP has been proven in the same paper as for its soundness [@erol_umcp_1994]. Since our method uses the same algorithm only the differences must be proven to respect the contract of application and reversion.

::: {.lemma name="Expansion solves the abstraction flaw"} :::
The application of an expansion resolver invalidates the related abstraction flaw.
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

::: proof :::
Abstraction flaws arise from the existence of their related composite step $a$ in the plan. Since the application of an expansion resolver is of the form $S'_\pi = (S_\pi \setminus a) \cup S_m$ unless $a \in S_m$ (which is forbidden by @def:action), therefore $a \notin S'_\pi$.
:::::::::::::

::: {.lemma name="Solved abstraction flaws cannot reoccur"} :::
The application of an expansion resolver on a plan $\pi$, guarantees that $a \notin S_\pi$ for any partial plan refined from $\pi$ without reverting the application of the resolver.
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

::: proof :::
As stated in definition of the methods (@def:action): $a \notin A_a^*$. This means that $a$ cannot be introduced in the plan by its expansion or the expansion of its proper actions.
HEART restricts the selection of providers for an open condition to operators that have an abstraction level strictly inferior to the one currently being made. So once $a$ is expanded, the current cycle lowers that allowed level and $a$ cannot be selected by subgoal resolvers. It cannot either be contained in another action's methods that are selected afterward because otherwise by definition its level would be at least $lv(a)+1$.

Since the implementation guarantees that the reversion is always done without side effects, all the aspects of completeness of POP are preserved in HEART.
:::::::::::::

# Results

In order to assess its capabilities, HEART was tested on two criteria: quality and complexity. All tests were executed on an Intel® Core™ i7-7700HQ CPU clocked at 2.80GHz. The Java process used only one core and wasn't limited by time or memory. Each experiment was repeated between 700 and 10 000 times to ensure that variations in speed weren't impacting the results.

![Evolution of the quality with computation time.](graphics/quality-speed.svg){#fig:quality}

@fig:quality shows how the quality is affected by the abstraction in partial plans. The tests made using our example domain (see @lst:domain). The only variation when implementing our version of HiPOP was regarding the flaw selection and result representation, all the rest is identical to the HEART implementation (including runtime instantiation). These results show that in some cases it may be more interesting to plan in a leveled fashion to solve HTN.

The quality is measured by counting the number of providing fluents in the plan $\left| \bigcup_{a \in S_\pi} eff(a) \right|$, which is actually used to compute the probability of a goal in intent recognition. This means that this quality is correlated to the precision of the prediction.

![Impact of domain shape on the computation time by levels. The scale of the vertical axis is logarithmic.](graphics/level-spread.svg){#fig:width}

In the second test, we used generated domains. These domains consist of an action of abstraction level $5$. It has a single method containing a number of actions of lower level. We call this number the width of the domain. All needed actions are built recursively to form a tree shape. Atomic actions only have single fluent effects. The goal is the effect of the higher level action and the initial state is empty. These domains do not contain negative effects.
@fig:width shows the computational profile of HEART for various levels and widths. We note that the behavior of HEART seems to follow an exponential law with the negative exponent of the trend curves seemingly being correlated to the actual width.
This means that computing the first cycles has a complexity that is close to being *linear* while computing the last cycles is of the same complexity as classical planning which is at least *P-SPACE* [@weld_introduction_1994].

# Conclusions {-}

In this paper, we have presented a new planner called HEART based on POP.
We showed how HEART performs compared to complete planners in terms of speed and quality. While the abstract plans generated during the planning process are not complete solutions, they are exponentially faster to generate while retaining significant quality over the final plans. By using these plans it is possible to find good approximations to intractable problems within tight time constraints.

# References {-}

