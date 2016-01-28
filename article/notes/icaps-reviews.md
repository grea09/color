# REVIEW 1 #

OVERALL EVALUATION: -2 (reject)


## REVIEW ##
This paper studies partial order planning (POP) and proposes a set of algorithms for POP. It is poorly written. The language is awkward and verbose in many places. I don't see a clear focus point in this paper. The techniques are not placed in context of existing work. Much more detailed comparison against state-of-the-art is needed.

## RESPONSE ##
Style and language : Grammar and style corrector + more proof reading
Focus point : Should explicit motivations more clearly + Define a red line and explicit objectives
not placed in context of existing work : Reviewer wants boring papers : **Won't fix**
comparison against state-of-the-art : Need more details + Table of comparison with check marks + more graphs + Comparison in red line

# REVIEW 2 #

OVERALL EVALUATION: -3 (strong reject)

## REVIEW ##
The paper introduces a set of algorithms for partial-order causal-link (POCL) planning that are guaranteed to return "solutions" to a planning problem even in cases where the problem is unsolvable.

Although I would really like to see some progress in the field of POCL planning, I cannot recommend the paper in its current form due to its preliminary state.

First, and most important, the paper is missing a clear motivation for the chosen approach. You are only motivating POCL, but not "soft solving". Given the "problem setting" of a paper is "generally accepted" by the respective community, I do not think that one needs to spend "a lot of time" (space) for the motivation. But here you are generating "solutions" to unsolvable problems, which, at first glance, is at least quite odd. Of course it might be interesting to know why an unsolvable planning problem is unsolvable: is it a domain property or a problem instance property? In each case: why is it unsolvable?. (I would recommend citing papers that are concerned with this topic. I am not certain about the literature that exists for identifying unsolvable problems and pinpointing the reasons for it. The only one I know and that does something related is "Coming up With Good Excuses: What to do When no Plan Can be Found" by Göbelbecker, ICAPS'10). If one would knew these reason!
 s/causes, one could try to fix them in the domain or problem instance. However, you are not discussing any of such reasons, but just assume that it is reasonable to obtain a solution even in cases where the problem is unsolvable. But, assuming the domain and problem instance are correctly reflecting the "real world" or the "actual problem", one does, of course, only want the correct answer to that problem, which is "The problem is unsolvable!". You need to elaborate on this - in particular what such a "solution" (to an unsolvable problem) is intended to mean and why it should be justified in any way to produce it: You are changing some actions, after all, so they do not reflect the domain model anymore and it is not at all clear why one should be allowed to alter the actions in the way you do. If one is allowed to alter any actions just so produce a "solution", then why not just remove all preconditions that don't have a supporting causal link as well as all effects that ca!
 use a causal threat? I am just saying that it must be completely clear (technically and in terms of motivation) what part of the domain/plan might be changed, why, and what the result means - otherwise one could just do everything... Finally, on page 6, you are making some argument for this:

Soft failure is useful when the precision and validity of
the output is not the most important criteria we look for. In
some cases (like in recognition processes) it is more useful
to have an output even if it is not exact than no output at all.

However, that comes way too late and should be elaborated much more. Further, one should differentiate between altering a domain and/or problem in order to make the problem solvable and the algorithm used to do so. You are focusing on a POCL algorithm (which you might do, of course), but from a theoretical point of view, one should more be interested in finding the causes for unsolvability in the domain/problem, which has nothing to do with the used algorithm. This should also be discussed, I think.


In the following, you will find a list of some issues/problems:
- the paper has quite a lot of typing and formulation errors (e.g., missing articles, wrong singular/plural). This is really disruptive many times. In fact, it is sometimes incredibly hard to read due to language quality.
- Section "Classical Planning". I am not a hundred percent sure whether one should call this section "classical planning". If you are referring to the content that way because your problem setting is deterministic and fully observable than you are right. However, it is often referred to the state-based planning technique as opposed, for instance, to POCL planning. Since the described concepts in this section are mostly all POCL-related, you might as well call this section "POCL planning concepts" or "POCL planning foundations" or something... More importantly, I think it's unacceptable that you are not citing a single POCL paper in that section although you are using standard POCL notation.
- Def.3 is odd. Although actions may use negative preconditions (represented as negative literals), you define applicability by means of set inclusion. This only works, however, if you define states as being complete w.r.t. all fluents, which you did not do. E.g., consider the action X = <{(neg a),b},{c}> and the state S = {b}. In your formalism, X is not applicable, because S is missing (neg a). In other words, you consider the states S = {b} and S' = {b, (neg a)} to be different, which seems odd to me.  
- I think it's odd that u put causal links and ordering constraints into the same set and even call ordering constraints "ordering links" later on. I see only problems with this decision. For instance, ordinarily, the ordering constraints of a partial plan induce a strict partial order (which also implies that it's not cyclic) and they may not contradict the ordering induced by the causal links. Both is not required by your definition. This was probably an intended design decision, since you need to ignore these properties if you want to be able to call a tuple <A,L> a "partial plan" if it does contain cycles, for instance (which you need to do since your algorithm produced such structures). However, one could still mention these design choices and explain why they have been performed.
- Fig. 1: Although this figure serves just as a legend, I was quite confused by the fact that the causal link and the ordering constraint points from the action to itself (it supports its own precondition), since this is not possible in (useful or standard) POCL plans. Couldn't you fix this, e.g., by introducing a second action?
- Your representation assumes a ground model. I am not sure whether one should stress this (probably at Def. 1), since POCL planning algorithms often use a lifted representation.
- Def.5: I think it is more common in POCL planning to call a precondition to be "supported by a causal link" or to be "protected by a causal link" (both is syntax), but not "satisfied by a causal link" (which would be semantics).
- Def.5: It seems odd and wrong to say "we can note a subgoal as: a->b \notin L | {a,b}\subseteq A". You define a subgoal as being a *literal*, but here you give a set (?) of links that don't exist? Shouldn't it be: "Formally, a subgoal s is a fluent, for which holds: a->b \notin L and {a,b}\subseteq A"?
- Def.8: You use the term "valid partial plan" here, but *valid* partial plans were never defined. Apart from that problem, you really have to give the formal solution criteria here! Why don't you give the standard POCL criteria stating that a plan is a solution iff it does not have flaws? However, you need to be very careful here, since there two different kinds of solutions: the standard one known to the literature and your "solutions" (which are nowhere clearly defined) which allow, e.g., alterations of actions.
- Def.10: the running index should be i.
- In chapter "classical POP", you state that POP is sound and complete, but you cite paper [6], which is about UMCP, an HTN (hierarchical task network) planning system, not a POP/POCL planning system. Did you want to cite UCPOP instead? Also, please clearly state where Alg.1 comes from.
- chapter "classical POP". No related work is cited here. You are mentioning flaw selection strategies, but you are not citing any POCL paper that is concerned with such flaw selection strategies. Also, and this is just as important: you mention some "problems" of POCL planning in that section, for instance that of "redundant" actions (cf. action v in Fig.2). However, you are not even talking about the most obvious way to handle this: heuristics! Heuristics can judge the quality or "goal distance" of a plan. So, heuristics can, in principle, detect that the plan depicted in Fig.2 is worse than the respective subplan without v. You really should discuss this. (There are some domain-independent POCL heuristics that could also be cited there.) 
- Def.14: you mean \subseteq, instead of \in. Also, a ":" between "f" and "{f," would help... Also, you define an action as being "contradictory" if some fluent occurs both negative and positive in an effect. This is in contradiction to standard STRIPS. Here, it is allowed, since the result of an action application is simply defined as "first removing and than adding" (so, the positive effect would "win"). Also, there is no need to talk about actions that have a literal both as a positive and negative preconditions. It's just not executable, so any preprocessing would identify and prune it. As noted in the beginning of the review (missing motivation), it is absolutely not clear why one should be allowed to change such an action. I understand that this is the most important point of your work, but then you must give a clear motivation and explain on a technical level when a plan is regarded a solution, which alterations are allowed (and why! are those even required to find a !
 solution?) and what exactly the differences are to standard definitions of solutions.
- Section "Convergence property of POP". You are giving a "Proof of convergence." without a theorem/proposition/etc, which is somehow odd. I guess "POP converges." is the "proposition" that you want to prove. However, you need to clearly specify what you mean by that. Do you mean that POP will always terminate? If so, please write this down. Also, what do you mean by "POP"? Do you mean Alg.1 of the paper? If so, write it down. Also, it is commonly known that POCL algorithms are not guaranteed to terminate unless you restrict to a certain search strategy like BFS (which you did not do in your proof) or use other techniques such as termination given the number of actions exceeds a certain number (which Alg.1 also does not do).You can easily see that standard POP algorithms can get stuck infinitely in loops when performing a DFS.
- Def.27 (hypersoundness): This is hardly a definition. First, it is not clear what a "solution" should be in case a problem is not solvable. This needs to be defined. Second, it is also not clear what properties solution plans have if the respective problem *is* solvable. Are those guaranteed to be solutions in the ordinary sense or might those plans be "solutions" in your sense (which is not defined), e.g. that contain altered actions? This is actually a main concern, since the properties of the proposed algorithm need to be clearly presented and, of course, it's of major importance whether "output plans" to solvable problems are actual solutions in the typical sense or "solutions" that allow domain/problem changes. From having a look at Alg.6 (SODA POP) it's clear that you are only "healing" a problem if it is unsolvable (which you also state on page 7: "Once the POP algorithm fails completely, the soft failing algorithm can be invoked to heal the plan. It chooses the bes!
 t plan b|∀p ∈ F, b ≺ p to heal it."). However, this should be presented more clearly to the reader, e.g. in Def.27.
- some times (in the conclusion, but not only there), you state that you want to use your algorithm for online plan recognition and decision making. However, you never go much into detail; thus, it is not convincing although it might be very interesting how domain and problem deviation could be used for these settings. Maybe you should explain this on a technical level as part of the motivation.

# REVIEW 3 #

OVERALL EVALUATION: -2 (reject)

## REVIEW ##
The paper posits to provide techniques for improving plan quality of partial order planning. This is quite necessary for non one-off setting (e.g., online) and practical applications.

However, the authors seem to have ignored existing techniques that many POP planners have already used in literature. I am basing my comments using some well-known papers and results in the area:

* Refinement planning as a unifying framework for plan synthesis
S. Kambhampati
AI Magazine, Vol 18. No. 2, Summer, 1997.

* Design Tradeoffs in partial order (plan space) planning
S. Kambhampati
In Proceedings of 2nd Intl. Conf on AI Planning Systems, 1994

* Courses: 
rakaposhi.eas.asu.edu/cse574/notes/week2.2-s08.ppt 
 

Some examples are: relevant action check, means-end analysis (page 4, proper plan generation) and cycle detection (both forward and backward). Further, the authors do not pay attention to the semantics of plans that their planner is generating. For example, they do not make a distinction between actions and their one or more instances, called operators, in literature. (Page 5, definition 17).


Furthermore, the paper is not written well with missing words, typos and grammatical errors. Specific issues are listed below:

1. Abstract, line 4: doing what?
2. Abstract, second-last sentence: why is a problem impossible to solve but can suddenly become solvable, since the methods only improve plan quality and not change the domain theory?
3. Page 1, second column: unrelevant => irrelevant
4. Page 3, second column: there is no action X in Fig 2; further, any POP would have removed action v in a simple action verification pre-processing that most POP planners do.
5. Page 4, first column: participating action checks are same as relevant action and means-end analysis. 
6. Page 5, liar links: are you assuming the domain theory can be incomplete? If so, how do you suddenly now know that an action is correctly specified with its pre- and effects?
7. Page 7, convergence property of POP: You do not make a difference between number of actions and its instances. If the number of actions is finite, the number of steps in the plan may still not be finitely bounded. For example, in blocks world, the planner can conceivable have infinite hand-empty and hand-full instances of the actions. 
8. The experiments are simplistic and do not make a convincing point

------------------------------------------------------
