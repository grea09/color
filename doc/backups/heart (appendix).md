---
#title: "HEART: HiErarchical Abstraction for Real-Time Partial Order Causal Link Planning"
#author: 
# - Antoine Gréa
# - Laetitia Matignon
# - Samir Aknine
#institute: Univ Lyon, Université Lyon 1, CNRS, LIRIS UMR5205, F-69621, LYON, France (first.lastname@liris.cnrs.fr)
#thanks: Univ Lyon, Université Lyon 1, CNRS, LIRIS, UMR5205, F-69621, LYON, France (first.lastname@liris.cnrs.fr)
#tags: [Activity and Plan Recognition, Hierarchical planning, Planning Algorithms, Real-time Planning, Robot Planning, POCL, Partial Order Planning, POP, Partial Order Causal Link, POCL, HTN]
#abstract: In recent years the ubiquity of artificial intelligence raised concerns among the uninitiated. The misunderstanding is further increased since most advances do not have explainable results. For automated planning, the research often targets speed, quality, or expressivity. Most existing solutions focus on one area while not addressing the others. However, human-related applications require a complex combination of all those criteria at different levels. We present a new method to compromise on these aspects while staying explainable. We aim to leave the range of potential applications as wide as possible but our main targets are human intent recognition and assistive robotics. The HEART planner is a real-time decompositional planner based on a hierarchical version of Partial Order Causal Link (POCL). It cyclically explores the plan space while making sure that intermediary high level plans are valid and will return them as approximate solutions when interrupted. This paper aims to evaluate that process and its results related to classical approaches in terms of efficiency and quality.

bibliography: bibliography/heart.bib
style: aaai
---

# Properties

## Soundness {#sec:sound}

For an algorithm to be sound, it needs to provide only *valid* solutions. In order to prove soundness, we first need to define the notion of support.

::: {.definition #def:support name="Support"} :::
An open condition $f$ of a step $a$ is supported in a partial order plan $\pi$ if and only if 
$$\begin{array}{l} 
\exists l \in L^-_\pi(a) \land \nexists a_b \in S_\pi:\\
f \in \mathit{causes}(l) \land \left ( \phi^-(l) \succ a_b \succ a \land \neg f \in \mathit{eff}(a_b) \right )
\end{array}$$
This means that the fluent is provided by a causal link and isn't threatened by another step. We note support $\pi \downarrow_f a$.

**Full support** of a step is achieved when all its preconditions are supported: $\pi \Downarrow a\equiv \forall f \in \mathit{pre}(a): \pi \downarrow_f a$.
::::::::::::::::::::::::::::::::::::

We also need to define validity in order to derive all its logical equivalences for the proofs:

::: {.definition name="Validity"} :::
A plan $\pi$ is a valid solution of a problem $\mathcal{P}$ if and only if $\forall a \in S_\pi: \pi \Downarrow a \land lv(a) = 0$.
:::::::::::::::::::::::::::::::::::::

We can now start to prove the soundness of our method. We base this proof upon the one done in [@penberthy_ucpop_1992]. It states that for classical POCL if a plan does not contain any flaws, it is fully supported. Our main difference being with decomposition flaws we need to prove that its resolution does not leave classical flaws unsolved in the resulting plan.

::: {.lemma name="Decomposition with an empty method"} :::
When a fully supported composite action $\pi \Downarrow a$ is expanded using an empty method $m = \left ( \lbrace I_m, G_m \rbrace, \lbrace I_m \rightarrow G_m \rbrace \right )$, adding all open conditions of $G_m$ as subgoals (along with their related flaws) will result in a plan $\pi'$, without any undiscovered flaws.
:::::::::::::::::::::::::::::::::::::::::::

::: proof :::
The initial and goal steps of a method follows ($\mathit{pre}(a) = \mathit{eff}(a)$). By definition of full support:
$$L^-_{\pi'}(I_m) = L_\pi^-(a) \land \mathit{pre}(I_m) = \mathit{pre}(a) \implies \left ( \pi \Downarrow a \equiv \pi' \Downarrow I_m \right )$$

The only remaining undiscovered open conditions in the plan $\pi'$ are therefore those caused by $G_m$: $\lbrace f \in \mathit{pre}(G_m) : \pi' \downarrowbarred_f G_m \rbrace$.

No new threats are introduced since the link between $I_m$ and $G_m$ is causeless and they inherit the order of the parent composite action. All added actions are atomic so no decomposition flaws are added.
:::::::::::::

::: {.lemma name="Decomposition with an arbitrary method"} :::
If a fully supported composite action $\pi \Downarrow a$ is replaced by an arbitrary method $m$ and all open conditions contained within $S_m$ as subgoals and all threatened links within $L_m$ as threats are added to the agenda, the resulting plan $\pi'$ will not have any undiscovered flaws.
:::::::::::::::::::::::::::::::::::::::::::

::: proof :::
When replacing a composite action with a method in an existing plan we do the following operations: $S_{\pi'} = S_m \cup (S_\pi \setminus a)$, $L_{\pi'}(I_m) = a \rhd I_m$. The only added flaws are then:
$$\bigcup^{f \in \mathit{pre}(a_m)}_{a_m \in S_m} \pi' \downarrowbarred_f a_m\bigcup^{l \in L_{\pi'}}_{a_b \in S_{\pi'}} a_b \olcross l \bigcup_{a_c \in S_m}^{lv(a_c) \neq 0} a_c \oplus$$

That means that all added actions have their subgoals considered and all links have their threats taken into account along with all additional decomposition flaws.

This proves that decomposition does not introduce flaws that are not added to the POCL agenda. Since POCL must resolve all flaws in order to be successful and according to the proof of the soundness of POCL, HEART is sound as well.
:::::::::::::

Another proven property is that intermediary plans are valid in the classical definition of the term (without considering decomposition flaws) and when using this definition, HEART is sound on its anytime results too.

## Completeness

The completeness of POCL has been proven in the same paper as for its soundness [@penberthy_ucpop_1992]. Since our method uses the same algorithm only the differences must be proven to respect the contract of applications and reversion. This contract states that applying a resolver prevents the reoccurrence of its flaw and that reverting the application of a resolver must restore the plan and agenda to their previous state.

::: {.lemma name="Decomposition solves the decomposition flaw"} :::
The application of a decomposition resolver invalidates the related decomposition flaw.
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

::: proof :::
decomposition flaws arise from the existence of their related composite step $a$ in the plan. Since the application of a decomposition resolver is of the form $S'_\pi = (S_\pi \setminus a) \cup S_m$ unless $a \in S_m$ (which is forbidden by definition), therefore $a \notin S'_\pi$.
:::::::::::::

::: {.lemma name="Solved decomposition flaws cannot reoccur"} :::
The application of a decomposition resolver on a plan $\pi$, guarantees that $a \notin S_\pi$ for any partial plan refined from $\pi$ without reverting the application of the resolver.
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

::: proof :::
As stated in the definition of the methods: $a \notin A_a^*$. This means that $a$ cannot be introduced in the plan by its decomposition or the decomposition of its proper actions.
Indeed, once $a$ is expanded, the level of the following cycle $c_{lv(a)-1}$ prevents $a$ to be selected by subgoal resolvers. It cannot either be contained in the methods of another action that are selected afterward because otherwise by definition its level would be at least $lv(a)+1$.

Since the implementation guarantees that the reversion is always done without side effects, all the aspects of completeness of POCL are preserved in HEART.
:::::::::::::

# References {-}

\setlength{\parindent}{-0.2in}
\setlength{\leftskip}{0.2in}
\setlength{\parskip}{8pt}
\small
\noindent
