
----------------------- REVIEW 1 ---------------------
PAPER: 8
TITLE: HEART: HiErarchical Abstraction for Real-Time Partial Order Causal Link Planning
AUTHORS: Antoine Gréa, Laetitia Matignon and Samir Aknine

Overall evaluation: 1 (weak accept)
Reviewer's confidence: 4 (high)
Significance: 1 (minimal contribution or weak impact)
Soundness: 2 (minor inconsistencies or small fixable errors)
Scholarship: 1 (important related work missing, or mischaracterizes prior research)
Clarity: 3 (well written)
Reproducibility: 3 (authors describe the implementation and domains in sufficient detail)

----------- Overall evaluation -----------
The paper introduces HEART, a hierarchical planning system combining HTN planning with POCL planning. A few of these planning systems already exist, and the major (if not sole) contribution of the paper is to allow the generation of abstract plans, i.e., plans that do not contain flaws except "abstract task flaws". That is: A plan that is "executable" if its abstract tasks are interpreted as primitive. Other existing systems of this hybrid HTN/POCL kind focus on finding real solutions, i.e., primitive plans.

Overall evaluation/main critics


The paper fits well in the focus of the workshop, though I believe there is still room for improvement. In fact, I believe that many of my concerns are very easy to address, and, in case the paper gets accepted, I strongly encourage the authors to do so (regarding the easy-to-fix suggestions) for the final version. I think the general topic of the paper is interesting and has some potential, so I do recommend it for acceptance at this workshop. I basically have two main criticisms about the paper:

1. the actual contribution of the paper seems extremely limited and not sufficiently justified.
As far as I can tell, almost the *entire* paper is devoted to basic definitions (formal framework or the algorithm), but no real novel content is presented. Much of the content, like the theorems in Section Properties could have been compressed, or even follow from existing work, like from the HiPOP, FAPE, or PANDA planners, I believe. I think that's true, because your algorithm does not work in a fundamentally different way, but you basically propose a certain flaw selection strategy: postpone abstract task flaws as long as possible. Since the flaw selection strategy does not have an impact on correctness, your algorithm should have exactly the same properties than the others. I even think that the PANDA framework is flexible enough to allow the same: just define the respective flaw selection. All in all, given the limited contribution, I think more space should have been devoted to the novel part, i.e., the generation of abstract solutions. 
(This comment is not intended as a recommendation for changes for a final version, if accepted, but mainly as advice for a resubmission at a major conference: it simply seems that the actual contribution comes much too short.)

2. My second concern is missing or insufficiently classified related work. Though some early and important works on HTN+POCL planning have been cited, the only new work cited is the HiPOP planner. It completely ignores the currently ongoing work by the research group of Susanne Biundo on POCL+HTN planning, e.g, on the PANDA planner (see, e.g., dissertation by Schattenberg 2009, the ECP paper by Biundo and Schattenberg 2001, or more novel work by Bercher et al. on the formalism ("more than a name?") or the PANDA planner (SoCS 2014/IJCAI 2017)). Equally, if not more, important is the work on the FAPE planner (e.g., ICTAI 2014 by Dvor̆ák et al.). If ignoring its capability to handle time, it is basically very close to the HEART planner, since it also assumes acyclic decomposition models. I am also missing a further paper introducing an EMS (expand, then make sound) algorithm, which basically does, if I remember correctly, pretty much the same than your approach: Thomas Lee M!
 cCluskey, Object Transition Sequences: A New Form of Abstraction for HTN Planners, AIPS 2000. Maybe also some work of Yang can be included. In particular because he has investigated the question whether a high-level plan can be refined into a solution, which seems very related.
(I believe that these suggestions can easily, with little work, be incorporated in a final version. Please also consider my list of corrections and suggestions below, which give further suggestions or even corrections.)


Detailed comments
-----------------

Many of the comments are of minor nature, but some of them are actually quite important (e.g. corrections of misclassified related work).

abstract:
* "don't" --> "do not"
* "research often target speed" --> "targets"
* "of POCL" --> so far, that abbreviation was not mentioned. Also give long form here?

introduction:
* "range in expressivity, speed and reliability" --> ", and reliability" (I believe that you were using the oxford comma in the rest of the paper, so it should be used everywhere.)
* "of Ghallab et al. (2004) and (2016)" I think this should read "2004,2016" (if you cite both papers with the same cite command)
* You write/claim that POCl is often used in robotics and collaborative planning. Can you provide any citations for this? I think the original FAPE paper (PlanRob 2014) is also about robotics, so this somehow fits as well, although FAPE is not pure POCL, but HTN+POCL.
* "One of the most flexible approach is" --> "approaches"
* Both in the first mentioning of POCL and HTN (bold), you do not provide references, which is unusual.
* "to combine HTN and POCL in such" --> "HTN planning and POCL planning in"
* when you mention Young and Moore, Kambhampati, and Bechon, I think the work of Biundo et al. (examples/pointers see above) and the FAPE planner should be mentioned as well.
! your main motivation (second-last paragraph in the introduction) mentions that abstract solutions are useful in case no solution can be found. I urge you to be more precise here. Do you mean *when no exists* (i.e., the current plan cannot possibly be refined) or that the time constraints are too short (as indicated later in the next section)? This makes a *major* difference! In case no solution does exist, any informed heuristic would prune the plan altogether, so there does/cannot exist any abstract plan.

motivations:
* "planning has a PSPACE complexity (Erol et al. 1995)" --> This citation is actually wrong. In that paper, Erol proved the complexity for non-ground (lifted) models. That is, the citation probably fits in the parenthesis that follows, but for the PSPACE result, Bylander (1994) should be cited.
* "computing only the first plan level of a hierarchical domain is exponentially easier in relation to the complete problem" --> Can you back this up by a citation? If not, I would write "much easier" or something similar, because it depends on the model how much can be saved, one can even save arbitrary search effort, not "just" exponential.
* "explainable planning". Here, one could also include the ICAPS 2012 paper about the explanation of hierarchical (HTN+POCL) plans by Seegebart et al. (Making Hybrid Plans More Clear to Human Users -- A Formal Approach for Generating Sound Explanations)
* "action based" --> action-based

related works:
* "decompositional planners (Fox 1997)" --> I think the statement overly simplified. As far as I  am aware of, "decompositional planners" are those HTN+POCL or HTN+task insertion planner, in which there is no initial plan (in these cases, the plan existence problem is computationally much easier than compared to the case with an initial plan).
* "on task insertion called SHOP2 [nau_shop2_2003] to" --> the citation command is missing
* when you cite related HTN+POCL planners, PANDA and FAPE should be cited, I guess. FAPE fits particularly well, because it can also not cope with recursion.
* "we also improved made an improved" --> "we also made an improved"
* " that,for" --> "that, for"
* "However, this work" --> Later it becomes clear that you mean Marthi et al.'s work, but maybe that can be made clear earlier.

Definitions:
* is there a reference that can be added for RDF?
* the explanation inthe first paragraph (regarding the meaning of ~) can be improved, I think
* Def.1: it's not clear to me, why you talk about quantified predicates rather than about predicates stemming from first order logic
* Def.3: Maybe in the last bullet point, one can already mention that causal links without a fluent c will be used as ordering constraints?
* "actions does not" --> do not

Partial order causal links:
* in line 11 of Alg.11, you revert decisions. The paper does not explain why and I highly doubt that this is useful. You already clearly write that one needs to iterate over all resolvers (line 6), thus there is no need to revert it. In fact, reverting it means that you might have unsolvable plans in your search fringe which would otherwise (without line 11) have been correctly pruned.

heart of the moment:
* Def.9 is required for the definition of decomposition, right? If so, maybe mention that?

Abstraction in POCL:
* The Duet planner is wrongly classified. It does *not* combine abstraction with POCL! As far as I can tell, there are no causal links in that planner. It does, however, as stated in your paper earlier, combine HTNs with task insertion.

Cycles
* "A cycle is planning phase" --> "a planning phase"

References:
* "The Duet Planner." --> the dot at the end has to be deleted.
* Göbelbecker et al. AND Ramirez: there is a duplicate in the proceedings name, please check
- Also, your references are not formatted according to the required ICAPS style.


----------------------- REVIEW 2 ---------------------
PAPER: 8
TITLE: HEART: HiErarchical Abstraction for Real-Time Partial Order Causal Link Planning
AUTHORS: Antoine Gréa, Laetitia Matignon and Samir Aknine

Overall evaluation: 1 (weak accept)
Reviewer's confidence: 4 (high)
Significance: 1 (minimal contribution or weak impact)
Soundness: 2 (minor inconsistencies or small fixable errors)
Scholarship: 2 (relevant literature cited but could be expanded)
Clarity: 2 (mostly readable with some room for improvement)
Reproducibility: 2 (some details missing but still appears to be replicable with some effort)

----------- Overall evaluation -----------
This paper describes a preliminary hierarchical planning approach that comines some ideas from HTN planning and partiallu-ordered causal link planning. The primary focus of the paper is hierarchical abstraction planning. In that sense, some of the related work comparisons made in the paper are well-placed (e.g., the Duet planner, UCPOP) but some are missing (e.g., ABSTRIPS). 

My main suggestions for this evolving manuscript will be as follows:

+ The experiment results are not clear in terms of what the contributiosn of this approach are. The Listing 1 shows on the operators of the example domain, which seem very simple. I would recommend to evaluate the qualitive in a more involved planning domain. I really could not understand Figure4; more explanations are needed about these results. How are the levels defined? What do they look like (I assume they are similar to the example hierarchy in Figure 2). If the initial state is the empty and there are no negative effects, that means the planner does not backtrack at all in these experiments, does it?

+ Are the abstract actions (called abstraction flaws and decomposition resolvers) in the manuscript executable? The claim about abstract plans provide anytime execution, even if it is approximate, needs to be discussed further and grounded into formal semantics.

+ It's very hard to understand / follow some of the places in the paper. It should be written in a way the concepts are clear. For example, what does it mean to define a method as a partial-order plans that can realize an action? In hierarchical planning, methods do not realize actions -- they decompose nonprimtiive/compound tasks (or abstract actions) into smaller tasks.  Even in Table 1, which is summarized over Ghallab et al 2004, this concept is mis-articulated. As another example, it's not clear what a cycle is because it's defined based on other concepts that are not explained well.


----------------------- REVIEW 3 ---------------------
PAPER: 8
TITLE: HEART: HiErarchical Abstraction for Real-Time Partial Order Causal Link Planning
AUTHORS: Antoine Gréa, Laetitia Matignon and Samir Aknine

Overall evaluation: 1 (weak accept)
Reviewer's confidence: 4 (high)
Significance: 2 (modest contribution or average impact)
Soundness: 3 (correct)
Scholarship: 3 (excellent coverage of related work)
Clarity: 1 (hard to follow)
Reproducibility: 2 (some details missing but still appears to be replicable with some effort)

----------- Overall evaluation -----------
This paper describes a technique for anytime abstraction in the course of HTN planning.  The planner described operates iteratively, building a plan at a fixed level of abstraction and then, given further time, refining the plan by expanding some tasks that were treated as primitive at the more abstract level.  The paper is interesting, and should certainly be accepted.  

I would urge the authors to point out that abstraction is a key notion here -- many HTN planners are not, at least as applied to problems, actually abstraction/refinement planners.  One often hears them described that way, but inspecting HTN plan libraries one finds that the methods very generally correspond to *search guidance* rather than abstraction/refinement.  Partly this is because in order to get useful search guidance, you cannot separate, the decomposition of task A from task B (as abstraction/refinement would do) -- the methods need to know that A and B are being done together in order to provide useful search control.  In this way, HTN planning is functioning more like the LTL search guidance of Bacchus and Kabanza, and less like abstraction/refinement.

I gave this only a middling significance value, because in future work the authors should tackle the *real* problem with this kind of iterative refinement: without the so-called "downward refinement" property, it's not interesting to have an abstract plan, because that abstract plan could be wrong (impossible to extend to a concrete plan).  If wrong, it would be actively misleading, and likely worse than having no abstract plan at all.  However, as is often the case, in the event that the downward refinement property is present, HTN planning is trivial and not very interesting.  The soundness and completeness proofs here, while correct as far as I checked, are not that interesting, because having a sound plan at one abstraction level, doesn't guarantee that there is a concrete plan that extends the absract one.

Minor points:

- It seems to me that Erol's UMCP is effectively a POCL planner, because it has ordering links in its HTNs.  It would be helpful to discuss this comparison.

* The use of the term "hybrid" planning for "HTN + POCL" planning is perhaps unfortunate.  The same term is used (by extension from "hybrid automata") to refer to planning for systems that are "hybrid" in the sense that they have both discrete and continuous state (e.g., PDDL+ planning).

One concern: the paper is very messy, and contains numerous mistakes.  I list some of them here.  To submit this paper to a conference, as opposed to a workshop, you will need to have it thoroughly edited.

* "Since planning has a PSPACE complexity (Erol et al. 1995) in its classical formalization (it can be harder when functions, objects or quantifiers are used), computing only the first plan level of a hierarchical domain is exponentially easier in relation to the complete problem."  This is not a correct statement.  Since planning is PSPACE complete, it *may* be useful to compute only the first level.... But there is no guarantee that it will be exponentially easier -- it's quite possible to encode things so that even an abstract plan will be hard to find (since abstract planning is itself a planning problem), and likewise to make it so that once the abstract plan is found, finding a concrete one is trivial (e.g. if all the difficult sequencing is solved at the highest level of abstraction).  In general, your claim is *likely* to hold, but it should not be stated as a fact and it should definitely not be stated, as here that `because planning is PSPACE complete therefore abstract planning will be exponentially easier.'

* "Sohrabi et al. state that the quality of the recognition is directly linked to the properties of the generated plans. Thus guided diverse planning was preferred along with the ability to infer several probable goals at once."  These two sentences are too loosely connected.  You jump from quality of recognition and property of generated plans to diverse planning with no justification.  And "properties of the plans" is too vague.  Can you imagine a case where the properties of a plan wouldn't matter? In that case the plan itself wouldn't matter.  This thought needs to be expanded and clarified.

* It's pretty clear as I read this that all task networks must have source and sink (start and goal, in the language of the paper) nodes, but nowhere is this explicitly stated, nor is it explicitly stated that all intermediate tasks must be strictly after the source node and before the sink node.  Please give a formal definition of task network and method definition.

* "In order to simplify the input of the domain, the causes in the methods are optional."  What are "causes in methods"?  Again, the paper needs a formal definition of method.

* "We present a new method to compromise on these aspects staying explainable."  This sentence is not parse-able.

* "we present an upgraded planning framework that aims to simplify all notions to their minimal forms in order to reduce the size of the data structures to handle during runtime."  The paper never addresses this topic. Suggest you just drop this claim.

* "nau_shop2_2003" citation is missing

* "We also improved made an improved planning framework..." redundant

* "used them as a heretic[HEURISTIC] guide."

* "Instanciated" should be "instantiated".

* "inferred" has 2 r's

* in figure 1, "unifcation"

* "An abstraction flaw can be related to the introduction of a composite action in the plan by any resolver and invalidated by its removal."  What does this mean?  related in what way?  I think maybe you mean "created by the introduction," but I'm not sure.

* "approximative" --> "approximate"

* "The quality is measured by counting the number of providing fluents in the plan ... which is actually used to compute the probability of a goal in intent recognition."  I know what this means because I've read these papers, but no one will understand this sentence otherwise.  Please rewrite.


-------------------------  METAREVIEW  ------------------------
PAPER: 8
TITLE: HEART: HiErarchical Abstraction for Real-Time Partial Order Causal Link Planning

RECOMMENDATION: accept

Reviewers agree that this paper is on topic and would be a valuable contribution to the workshop. We hope the reviewers suggestions and discussion at the workshop help as you develop this paper for a future conference submission.

In drafting your final paper, please take special care for the detailed comments left by the reviewers.


