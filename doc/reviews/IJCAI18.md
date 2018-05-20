# Review 1 {-}

This paper introduces a planning approach combining hierarchical planning and partial-order planning. The intention is to offer some kind of anytime behavior of the planner by coming up with partial plans on any level of abstraction. These "intermediary" plans are no (abstract) solutions, however.

The referenced related work ignores a strand of research addressing so-called "hybrid planning", a combination of hierarchical planning and POP as well. It was initially introduced in work by Kambhampati et al. (AAAI'98) and Biundo and Schattenberg (ECP'01), followed by contributions of Geier and Bercher (IJCAI'11) and Bercher et al. (ECAI'16), among others. Also related is the work by Marthi et al. (ICAPS'07) etc. 

The paper's abstract and introduction claim that the approach is relevant for explanation and for goal inference. While these are important and topical issues, none of these is outlined in the following sections.

Apparently, the paper introduces a proper formal theory of the planning formalism underlying HEART. Semantics of this formalism are missing, though.

In summary, in its current form the paper is not sufficiently original, relevant, and significant to be published at IJCAI/ECAI.

# Review 2 {-}

The paper proposes a novel planning system, called HEART, which combines HTN planning with POP (more precisely: with partial order causal link (POCL) planning). The problem that they solve is given in terms of a goal description, just as in standard classical planning. That is, the hierarchy is not given in order to restrict the set of solutions to those that can be obtained from an initial plan/task network as done in HTN planning and its variants, but instead (a) for advice (for find solutions quicker) or (b) to create plans on higher levels of abstraction. These planners that basically solve classical planning problems with composite actions in a POCL planner are often referred to as decompositional planners. The paper is proposing such a planner and evaluates it empirically. The main motivation for this planner is two-fold: (1) It allows to generate abstract plans (plans in which all preconditions and effects are supported by causal links, but where some actions are still composites). One mentioned motivation for such plans is to obtain an anytime algorithm that can use such intermediate abstract plans as approximations of the original problem. (2) for practical purposes like intent recognition. Both motivations are not presented in an in-depth manner, but remain motivation. 

At its current state, I have to argue against the acceptance at IJCAI-ECAI for several reasons. I'll first give an abstract overview of my main concerns; later, I'll give a more detailed list of suggestions for improvement for future versions of the paper.

First, the contributions and the motivation for them was very unclear to me. The abstract is just abstractly arguing for the practical usefulness of hierarchies as justification for a new decompositional planner (which would be okay if this line of arguments would also be used later on), but then the introduction very prominently motivates the new planner by its usefulness for plan and intent recognition, so (despite being not that clear from the abstract), intent recognition seems to be a prominent motivation for the planner. [So far, my concern is mainly about a mismatch between abstract and introduction, which is really just a *minor* criticism about readability/structure.] However, in my opinion, the paper then fails to formally show how the paper's contribution (HEART) is beneficial for this motivation. That is, after heaving read the introduction I was a bit confused: what is the purpose of and motivation behind the planner? What does plan and intent recognition have to do with this / will the planner be relevant for this research area? From my point of view, the paper needs a clear concise motivation that can, in a non-abstract manner, state the benefits of the paper. The introduction failed this purpose. For instance, let's say your actual goal is to improve goal recognition (see: "For intent recognition, an important metric is the number of correct fluents. So if finding a complete solution is impossible, a partial solution can meet enough criteria to give a good approximation of the goal probability."), then your paper had to evaluate this purpose and compare it to other recognition approaches. However, your paper introduces a planner without comparing it to approaches for intent/goal recognition, so I think that the introduction is somehow misleading and should focus on the properties that HEART has and the advantages that it brings with it. 

My second main concern is the paper's scholarship. Some citations are not the ones one would expect (I will give them below in the detailed comments, because this is not my main concern). More importantly, some important (and some less important, but still interesting/fitting) related work is missing. The paper's main contribution is a decompositional planner, yet only one such planner is cited, published as STAIRS, though several similar approaches exist, including well-known ones, such as the hybrid planning approach by Kambhampati et al. (AAAI 1998) and DPOCL by Young et al. (AIPS 1994). I believe (not sure) that these approaches have not been implemented, but they are yet important related works that should at least be cited as such. I also deem the angelic hierarchical planning approach by Marthi et al. (ICAPS 2007 and ICAPS 2008) as highly related that cannot possibly be ignored. They are also concerned with generating abstract solutions (they call it high-level plans) and they also introduce anytime algorithms for generating them. Because the preconditions and effects of their composite actions have a different semantics than the ones used in the current paper, they are able to guarantee that such abstract solutions can be refined into actual solutions. That is, they can guarantee the downward refinement criterion. I will give more detailed comments (including further pointers to related work) below.

My third main concern is the quality of the overall presentation. Though I was able to follow the general ideas of the paper, its presentation raised several questions. The formalization seems much more complicated than it had to be (so I believe that many problems that I was having just occurred because of its unnecessary complexity), but I believe that even when sticking to the current formalization, its presentation quality must be improved (see my detailed comments).



detailed corrections/questions/suggestions
------------------------------------------

I will simply go through the paper from beginning to end (so there is no priority given in any way).

[x]
--[mismatch abstract vs. introduction]
I was already stating that I had problems in the mismatch of the abstract and the introduction. I would thus suggest to make those part a bit more matching regarding story line.

[x]
--[unclear citation meaning]
One citation in the beginning of the introduction was very confusing to me. There two sentences with two citations and I have no clue for which proposition you cite the second paper:
"Ramirez and Geffner [2009] found an elegant way to transform a plan recognition problem into classical planning. This is done simply by encoding observation constraints in the planning domain [Baioletti et al. 1998] to ensure the selection of actions in the order they were observed."
In my eyes, it is impossible to understand for which proposition you cite [Baioletti et al. 1998] here. You basically write "Author X [CITE] has done Y. They do Y in a certain way to ensure property Z." Thus, in my eyes, only the first citation makes sense. Your sentence "Author X [CITE] has done Y [CITE-2]. They do Y in a certain way to ensure property Z." was thus very confusing me, because it was not clear, which role the second citation plays. I have checked Ramirez and Geffner's work, but couldn't find that second paper, so its also not the case that their transformation (Y) is taken from the second paper (or at least not to the knowledge of Ramirez and Geffner). I am not familiar with their work, but I suggest to clearly phrase what can be found in the second paper, otherwise it's just an unmotivated citation.

[x]
--[Weld and PSPACE proposition]
In the introduction (and again right before the conclusion) there is a citation to Weld's POCL paper that I can't match to the paper's content. In the introduction, you write: 
"The impact of problems size on performance is an issue of automated planning as it has been proven to be a P-SPACE problem if not harder [Weld 1994]."
I am not sure what you mean by "is a PSPACE problem if not harder". To me, that sounds like: it could be harder, but one is not sure yet. This would not be true, since most "standard" complexity results for classical planning are already known.
Maybe this is just a language/formulation issue. But to me, this also reads like you claim that Weld proved the PSPACE hardness, which is not correct either. These results were given by Bylander, 1994, AI, "The Computational Complexity of Propositional STRIPS Planning" and by Erol et al., 1995, AI, "Complexity, decidability and undecidability results for domain-independent planning".

[x]
--[citations for POP and HTN]
When you cite POP the very first time (bold, in the introduction), I'd add one of the standard references, like the Automated Planning book by Ghallab, Nau, Traverso, or one of the first POCL papers (like the introduction to POCL by Weld, 1994, that you already cite). Similar for your first mentioning of HTN planning. Here, I would include a citation as well, maybe again the Ghallab et al. book or a standard HTN paper. 

[x]
--[RELATED WORK]
After the before-mentioned introduction of POP and HTN you write that both have been combined before and mention as a single reference the paper by Gerevini et al., which is about the Duet planner. I think, this statement should be improved significantly, because this is basically your main statement where you are describing the related work of your approach (or at least it's the first one).
I believe that Gerevini's paper is misclassified here. However, whether this is true highly depends on your definition of POP. From the content of your paper, we have to assume that you mean POCL planning by this (this is also in correspondence to the standard POP algorithm, which is a POCL algorithm). Thus, this paper is wrongly classified, since they do not do anything POCL-related. They combine HTN search (based on SHOP2) with task insertion (based on the stochastic search planner LGP for classical planning), so it would be a reference for this HTN+task insertion aspect. Maybe this is what you have meant, but then you should expand your description.
More importantly, I believe that papers are missing here, which are *very* close to what you are doing, namely HTN planning + POCL concepts.
I have already listed several works above; here the full citations:
- Kambhampati et al., AAAI 1998, Hybrid Planning for Partially Hierarchical Domains
- Young et al., AIPS 1994, Decomposition and Causality in Partial-Order Planning

Also, I think the paper deserves some more clarification (already in the beginning), *how* you are combining HTN planning with POCL concepts. The important point here is whether there is an initial plan with composite actions or just a goal description. In your case, there is just a goal description, but I think this should already be mentioned here in the beginning. As far as I know, this sort of planning (or problem class) is referred to as Decompositional Planning (see Young et al.'s paper and the one by Fox).
- Fox, ECP, 1997, Natural Hierarchical Planning using Operator Decomposition.

So far, in addition to Gerevini et al.'s paper, you only mention the planner HiPOP (Bechon et al.) as an example for fusing HTNs with POCL. Bercher et al. give an overview about further formalisms that fuse HTNs with POCL, though their paper seems not to mention explicit references to such planners.
- Bercher et al., ECAI 2016, More than a Name? On Implications of Preconditions and Effects of Compound HTN Planning Tasks

Regarding further more closely related work, in addition to the papers mentioned above, the following more recent ones are known to me, too:
- FAPE (the first paper is by Dvor̆ák, ICTAI 2014, Planning and Acting with Temporal and Hierarchical Decomposition Models, but it is still under development with recently published heuristics, e.g. at AAAI and ECAI)
- PANDA (that planning system does exist for a very long time, but I think the newest version of it is decribed by Bercher et al., SoCS 2014, Hybrid Planning Heuristics Based on Task Decomposition Graphs)
Loosely related is the planner BiPOCL by Winer et al., INLG 2016, Discourse-Driven Narrative Generation with Bipartite Planning. The planner is basically a reimplementation of DPOCL, as far as I know, which is tailored to narrative generation.
The first two planners actually solve hierarchical problems (i.e., in contrast to your approach, they allow the specification of an initial task network), but from an algorithmic point of view, the difference is minimal.


[x]
--[redundant entry in Def.1]
I was a bit confused with Def.1., because it contains both relations and fluents, though both seem to be the same. In fact, relations are never mentioned again, except as part of the formal definition of the fluents. So they seem redundant in Def.1. I also think that the formulation "These relations are similar to quantified predicates in first order logic." is suboptimal, since predicates cannot be quantified in FOL, only variables can be quantified. 

[x]
--[listing caption]
I suggest to add a short explanation to Listing 1 that explains the input language. It is not given in PDDL, so it cannot be assumed to be a standard. Even though I am able to understand this first listing, I am not able to understand Listing 2, because of the "(~)" in that example.


--[composite actions: preconditions and effects]
According to Def.3, also composite actions show preconditions and effects. This is perfectly reasonable, because otherwise one could not insert them into plans easily. But because this is in contrast to standard HTN planning (where composite actions are never inserted), I think it might be nice to emphasize this difference. Also, I think it is noteworthy to say what these preconditions mean, i.e., how they are related to the methods that are specified for them. (You write something about this later, but maybe one could/should move it here already?)

[x]
--[causal link definition]
Personally, I think it's odd to use causal links without conditions instead of ordering constraints. I do not know a single work in the POCL setting that does this and I would also not propose to do so. The term "causal link" has a special meaning and it is simply different from "ordering constraint", so using the same term for both is making the formalism more complicated. Even if you had implemented it in that way, there is no reason to break with conventions regarding the presentation of it.

[x]
--[inconsistent explanation: causal links in plans]
Right below the definition of plans, or, more precisely, right above Sec. 1.2 (p.3, top-left) there is a complete paragraph that is just totally confusing due to several reasons. So, I encourage you to improve the description significantly. Here is the paragraph:

Our representation for methods is very simplified and only
gives causeless causal links. The causes of each link to 
the preconditions and effects of composite actions are 
inferred. This inference is done by running an instance
of POP on the methods of each composite action $a$ while 
compiling the domain. We use the following formula to 
compute the final preconditions and effects of a: 
pre(a) = ⋃as ∈L+(a) causes(as ) and 
eff(a) = ⋃a s∈L−(a) causes(as ). Errors are reported if 
POP cannot be completed or if nested methods contain 
their parent action as a step (a ∉ A∗a , see definition 10).

These are my concerns:

1. You write that your methods only contain causeless causal links. 
This is a direct contradiction to you previous definitions! Before
Def.4, you write that methods are plans. And in Def.4 you clearly 
state that plans have causal links that *do* contain fluents.

2. You write that you infer the causes by using POP. I do not know
what could be meant here, because POP is an algorithm to find plans, 
not to infer anything, so you cannot talk about POP here, but about some
different specialized algorithm, correct?

(3. just a remark to improve presentation: in the set union formula,
you use "a_s" for the causal links. This was confusing me, since you
use "a" for actions ordinarily. I propose to change it to "l" (for link).)

4. I am still confused about what you want to do here. In the beginning
of this paragraph, you write that you want to infer the causes of the causal
links. But in the end, you do not give a formula to infer these causes, 
but instead to infer the preconditions and effects of the composite actions.
Quote: "We use the following formula to compute the final preconditions 
and effects of a", but how does this fit to the introduction stating
"The causes of each link to the preconditions and effects of composite 
actions are inferred."?

[x]
--[POCL encoding of init and goal]
In the last line before Sec.1.3, I think the order of "\emptyset" and "s^*" 
in G must be swapped. (the goal description is G's precondition, not its effects)

[x]
--[wrong definition of causal threats]
In Def.6, you define a causal threat as a situation in which a threatening action a_b
*is* in between the producer and consumer, i.e., a_p > a_b > a_n. While this is actually
*also* a threat, it is too restrictive, because this causal threat cannot be resolved anymore.
The correct definition of causal threats demands that the ordering constraints *allow* that
the threater a_b is ordered between a_p and a_n, not that it already is.


--[reverting decisions, Def.8, and Alg.1]
I have trouble understanding why the paper allows to revert decisions. This is mentioned at several locations, which I shortly go through.
Right before Def. 8 you write "The application of a resolver does not necessarily mean progress. It can have consequences that may require reverting its application in order to be able to solve the problem."
I was very confused, when I have read this. In *every* planning approach (like classical planning, i.e., state-based progression search), performing some modification (like action application to progress a state) might be wrong, so in principle there is no need in emphasizing this. However, this issue is normally dealt with by simple backtracking, i.e., by choosing one of the siblings of the search node that was created as a result of a "bad" decision. That is, this decision is not "reverted", but one simply chooses another search node. This is not what is done here:
In Definition 8, the deletion of a causal link is described. Later, the paragraph right before Section 2 seems to clarify why this is done. It states that the deletion is necessary because of the decomposition of compound tasks. But the Algorithm seems to contradict this:  Lines 11 and 12 clearly state that resolvers can be applied and that, if all resolvers fail, the selected flaw gets re-added.
If this is really necessary, it definitely needs to be discussed. In standard POCL planning, this would be contra-productive, because a plan is actually *proven* unsolvable if all resolvers fail. Even in decompositional planning, i.e., in your setting, in which there are composite actions as well, a plan can be discarded if all resolvers for addressing a flaw fail, so these two lines are not just redundant, they only make the algorithm less efficient, because it deleted fewer search nodes that are provably unsolvable. If there are some reasons why in your case not all the resolvers for a plan can be provided upon its detection (this is the only reason that I see that can justify reintroducing a flaw after all resolvers have failed), then, I strongly believe, you should emphasize and explain this fact, because this is not standard and your paper is about the planner, so it is about such details, anyway.


--[Def.9 and its explanation]
Def.9 defines a certain set of causal links, which will be used for the definition of decomposition. I encourage to clearly write this right before this definition. When first going through the definition, I believed that the definition was wrong, until I have read the explanation that comes right behind it (moving it upfront would eliminate this). But even this explanation only explains what the syntax is defining in a technical way, it does not mention what it is needed for (i.e., for decomposition). This motivation should added there as well. I think the variable $L^-_\pi$ is undefined, I'v only found the definition of $L^-_\pi(a)$. In the next line, the varibale $L^+$ is undefined as well, I believe.


--[Motivation of Def.10]
Definition 10 is completely unmotivated; please describe upfront what this definition will be used for.


--[first Lemma in Sec.3]
The lemma states that after decomposing a composite action and adding all new open conditions and new threats, all flaws for the resulting plans will be present. I think you also need to add the "composite action flaws" here, i.e., the flaws that encode due to which actions the resulting plan is not primitive, yet.


--[Correctness of POP: wrong citation]
In Sec. 3.2, you cite Erol et al.'s paper about their HTN planning system UMCP for the proposition that completeness and soundness of POP was proven. This citation is wrong: UMCP is an HTN planning system, it does not even allow task insertion, so it has nothing to do with POP. I do not immediately know the best citation that is needed here, but the paper by Penberthy and Weld, KR 1992, UCPOP: A Sound, Complete, Partial Order Planner for ADL, is probably a good starting point.


--[last Lemma in Sec.3]
You write that any composite action flaw that is removed can never be introduced before. This implies that your decomposition hierarchy is acyclic, i.e., free of recursion. You should state this clearly in the input specification, because it restricts your model. I was not aware of that until reading that lemma.


--[Evaluation]
Figure 2: Is the time scale correct? Most results end at 4 (ms), which seems *incredibly* small. Should it read (m) instead? But I have to confess that I didn't get its content anyway. I suggest to improve its presentation and discussion.
In general, I generally doubt the usefulness of the results. Quality is ordinarily measured in terms of action costs or number of actions. If a different measurement is used, it should do so on a solid justification, not just stating "it is used in goal recognition". If your planner is supposed to be used in the context of goal recognition, then you had to do so my providing a clear experimental setting in that context and comparing against the current state of the art in that setting.


--[references]
It seems like you have used a wrong bibtex style: the reference list looks syntactically odd.


list of misspellings and suboptimal phrasings/etc.
--------------------------------------------------

-intro
"fluent centric" --> "fluent-centric" (I think)

-intro
"the impact of problems size" --> "problem size"

-intro
You write: "Another approach is Hierarchical Task Networks (HTN) that are meant..."
First, I think it should be "(HTNs)". More importantly, HTNs is no approach, because, as you write, HTNs are *networks*, i.e., data structures. They are no approaches. "Using or applying HTNs" can be approach, or simply "HTN planning" is an approach, but not HTNs.

-Tab.1
In the the caption, it should read "from Ghallab et al. [2004]" instead of "from [Ghallab et al. 2004]", I think.

-everywhere
You write "eff" in math environment "$eff$", which is really ugly because of the wrong spacing ("e f f" instead of "eff"). I suggest to use "\mathit{}" or "\textit{}" instead. I think you should check all words that have an "f" in them, all these words always look ugly in math mode because of the space before and behind the f.

-Sec.1
In the beginning, you write that planners often work in two phases: first compiling the domain and then the problem instance. I am not sure whether this is correct. Most of the time, the planning instance contains the constants/objects, so planners cannot even start working before they know the problem instance. Thus, I believe it's not true that many planners first do something with the domain before moving on to the problem. Maybe you just wanted to say that one often separates between the domain definition and the respective problem instance (for instance, because one might define several instances that belong to the same domain).

-Sec.1
"fluents are not satisfied untul provided whatever their sign". I do understand the sentence, but I am not sure whether it is grammatically correct/complete.

-
Underneath you write "Methods are plans used only in HTN variants of POP". I think that this is a suboptimal formulation, because, technically speaking, it is incorrect. Methods are used in every single HTN approach (no matter whether it's fused with POP). Thus, it is not true that they are only used in HTN variants of POP. I think you wanted to say sth. like "In non-HTN variants of POP, methods are not used, because all operators are atomic". (but I think that the complete sentence is not necessary, since this is common knowledge)

-
In Def. 4, last point, it should read "the set of fluents c" (the "s" was missing)

-
Beginning of Sec. 1.2
"Problems instances" --> "Problem instances"

-Sec.2 
Check capitalization ("Of The")

-Sec.2
"...our method combines POP and HTN and how..."
Do you mean "...our method combines POP with HTN planning and how..." or sth. similar (as mentioned before: HTN means "hierarchical task network", the acronym itself is not an approach)

-Def.9
I propose to substitute the comma in the set by either "|" or ":", since otherwise the meaning one reads into is a set consisting of two entries (before and after the comma).

-everywhere
check references to "figure" and "listing". They should be written capitalized, I guess.

# Review 3 {-}

The paper provides an algorithm for an acyclic restriction of hierarchical abstractions for POCL planning, a restriction of the formalism that PANDA and others use (see Bercher 2016, More than a Name?).  They provide a planning algorithm, a proof of correctness (that's fast and loose with completeness!), and an evaluation on a two domains against a HiPOP. 

The main contribution is the ability to provide partially-refined plans quickly.  The utility of partially-refined plans is an interesting and open question, since, depending on the domain, many abstract plans may not be refinable into solutions.  To evaluate this, the paper provides a 'quality' metric, which is defined as the number of unique effects in the abstract plan.  There's a disconnect between this and the experiments, where quality is listed as a percentage (possibly correct fluents out the final refinement?), and between the experiments and any proposed use case, such as plan recognition.  That makes the importance of this contribution difficult to evaluate.

The paper only loosely situates itself in the literature.  Any differences with related formalisms are not well explained, and the differences with HiPOP are on cursorily mentioned. The text is rough, notably with a different syntax between definitions/examples and listings, references in the wrong format, and numerous typos.  I've listed what I found below.

Listings 1 and 2 have a different (unexplained) syntax from Table 1.
Paragraph after Def 2.: "equals to the" -> "equals the"
Following example: "If the cup contains water" -> "If the cup contains water,"
Just before 1.3, nill and s* appear swapped in the definition of G
In the following "Threats" bullet, the definition says a_b is a threat iff it is strictly ordered between a_p and a_n.
1.3 talks about adding negative resolvers, but I assume these are not generally available when resolving flows, but only when expanding an abstract action.  This isn't described.
In Alg 1, a for-loop is listed as a non-deterministic choice operator.  For-loops and the choice operator have different semantics, especially when you're dealing with infinite search spaces.
Def 10, the base case for atomic actions is missing, so A_a^n is technically nil.
Section 3.2, Erol's UMCP paper is cited for the completeness of POP?
Section 3.2 claims that HEART is complete, but complete with respect to what?  It is not complete for same problems as POP, because acyclic hierarchical action space limits the resulting plans.
Sec 4, second paragraph.  "figure" -> "Figure"

# Review 4 {-}

This paper elaborates over HiPOP planner, presenting a new hierarchical POP planner called HEART that extended HiPOP with new flaws in order to make it compatible with HTN problems.

The main problem of the paper is that it has not a clear goal to be reflected in title, abstract and introduction. The paper does not state clearly the motivation behind its work, that is, what is the problem that has not been addressed by existing HTN or hiearchical POP planners and the proposed planner tackles successfully.

A related work or background section is not provided. On the contrary, the paper spends two pages (three with the introduction) out of six to review elementary planning and partial order planning notions. The evaluation section is rather limited.

Overall, the significance of the paper is rather limited.


