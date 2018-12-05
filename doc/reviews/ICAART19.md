
Paper Title: HEART: Using Abstract Plans As A Guarantee Of Downward Refinement In Decompositional Planning
Paper #: 45
My Status: Author
Number of Reviews: 3

# Review #: 1	

Criterium	Description	Value
Abstract and Introduction are adequate?	 	Yes
Conclusions/Future Work are convincing?	 	No
Figures are adequate ?	in number and quality	Yes
Improve critical discussion ?	validation	No
Improve English?	 	No
Needs comparative evaluation?	 	No
Needs more experimental results?	 	No
Originality	Newness of the ideas expressed	2
Overall Rating	Weighted value of above items	4
Paper formatting needs adjustment?	 	No
Presentation	Structure/Length/English	4
References are up-to-date and appropriate?	 	Yes
Relevance	Paper fits one or more of the topic areas?	5
Significance	Is the problem worth the given attention?	4
Technical Quality	Theoretical soundness/methodology	5
Scale: 1:Lowest Value;6:Highest Value

Observations for Author
The paper introduces the HEART planner which is based on a hierarchical POCL. The main contribution of the paper regarding previous work is that the planner works in an anytime fashion, being able of providing plans with different levels of abstraction depending on the time. Less time means more abstract plans. I believe the solution adopted in the paper is interesting and well-justified. The paper is well-written in general. 

The only aspect to mention is that the notation used (summarized in Table 1) is somehow complicated to follow along the paper. Specifically for those people not very familiarized with it. Regarding previous work, it is not very clear from the paper what are the exact contributions. 

For instance, definition 2 of decomposition flaws is presented as a contribution of the work, but it seems it is not, given that (as the authors mentioned) HiPOP also made use of that concept. Then, the paper should make clear the difference between the background and the new contributions. Maybe it would be good to make clearer and enphasize in some what does it mean that your version is lifted in runtime. 

The conclusions of the paper can be developed little more. Is there any future work? 

Results: I imagine the memory of the machine could be relevant. What do you mean with "the java process was not limited by memory?. 

I understand the motivation of the work that abstract plans can be useful for several purposes. But also I think that the abstraction level could be relevant. Since there is a trade-off between the time and the abstraction level, I wonder to what extend the constraints of the domains in your experiments (atomic actions only modify one fluent and no negative effects) are relevant in this trade-off. 

What do you mean with "In order to verify the input of the domain" at the beginning of section 3.3. 

Section title (5 Results) and subsection (5.1): Can you include a sentence in between to fill that empty space? 
(Typo) resulting plans : HEART -> remove space before ":" 
(Typo) They are also proof -> proofs.


# Review #: 2	

Criterium	Description	Value
Abstract and Introduction are adequate?	 	No
Conclusions/Future Work are convincing?	 	No
Figures are adequate ?	in number and quality	No
Improve critical discussion ?	validation	Yes
Improve English?	 	No
Needs comparative evaluation?	 	Yes
Needs more experimental results?	 	Yes
Originality	Newness of the ideas expressed	2
Overall Rating	Weighted value of above items	2
Paper formatting needs adjustment?	 	No
Presentation	Structure/Length/English	2
References are up-to-date and appropriate?	 	No
Relevance	Paper fits one or more of the topic areas?	6
Significance	Is the problem worth the given attention?	2
Technical Quality	Theoretical soundness/methodology	1
Scale: 1:Lowest Value;6:Highest Value

Observations for Author
The paper proposes a novel method for hierarchical planning based on idea of interleaving one-step decomposition of all compound tasks with complete resolvent of all flaws in partial-order causal link style. This is an interesting idea, but the paper suffers from several major problems. The first one is clarity, I have found the description and notation somehow cryptic and despite being familiar with HTN and POCL planning, it was still hard for me to understand what the authors want to say. The second problem is that the claims of authors are not really supported. 

The major flaw is the claim that the existence of abstract plan guarantees existence of concrete plan. If this is the case then planning would be much easier problem than it is and I am afraid that this claim will hold only under very restrictive conditions, namely that the root task describes in its effects all possible goal states. This may hold in the single example domain used in experiments, but I do not see how this can be ensured in any domain model. The presented HTN formalism already has some restrictions, for example, there is no recursion, so it is no really HTN, but there are other features that are not clear to me, for example can sub-plans obtained from different compound tasks interleave? Finally, the evaluation is done using a single domain so this does nor really justify that the the proposed concept is better than existing HTN planners. 

I guess, it is not possible to compare with other HTN planners such as SHOP2 simply because the proposed modeling formalism is more restrictive than traditional HTN (it requires much more information in the model). I think that it must be clearly justified why this new hierarchical formalism is viable. In summary, the paper presents an interesting idea, but in my opinion it cannot be published in its current form until the above doubts are resolved. 

Some particular concerns: I think that the title is already misleading. How can you guarantee that the abstract plan can decompose to a concrete plan? I guess that it requires a lot of information in the model, which basically means that designing the model itself involves solving all possible planning problems for it (see below). 

The notation is really confusing. Why don’t you use notation standard in planning? For example, the example at Page 3 does not say a lot as it is never explained and for a reader that is not familiar with this syntax, it is impossible to read. 

Definition 4: While HTN methods use preconditions describing when the method can be applied, I do not really understand the usage of effects there. How can an abstract action have effects that are not clearly connected to effects of primitive actions in its decomposition? If you meant it this way, how you know all possible effects as there are alternative decompositions and combinations between them? Isn’t the guarantee of solvability simply because your require the model to actually encode all possible plans? This is very confusing. 

Page 4 bottom right: The definition of eff(a) probably explains what I wrote above. However, due to alternative decompositions, the effects of an abstract action (btw, it is called a compound task in classical notation) are not a set of literals. There must be some disjunction there. Also, the formula that you use is not very clear - it basically says that all effects of action a are used as preconditions by some other actions. This is strange. Why all effects must be used somewhere? If I put a block to another block to achieve the goal, I also get empty hand by why empty hand should be included in the goal? Again, this is very confusing. 

Page 6, right. You wrote that negative resolvers are mentioned in definition 8, but there is nothing like this. 

Definition 9: I do not understand what you want to say. It looks like you want to describe the set of fluents that is produced by a and consumed by a’. But on the right side, you take all incoming causal links to a’ and from them you select the literals that are provided by a in its outgoing links. This is not the same as literals provided from a to a’. Assume that a provides p as its effect but there is another action between a and a’ that consumes p (deletes p) and yet another (even later) action a’’ that provides p again. Then p is in the incoming link to a’ (provided by a’’), p is in the outgoing link from a, but p is not provided by a to a’. 

Page 7, top left: What is the example supposed to say? 

What is Definition 10 supposed to define? You did not say what is an entity there and what “contained” means. I only guess that you mean actions in the decomposition of some action. 

What is the definition 11 supposed to say? It defines lv using lv, so what is lv? I guess it is the level of decomposition for a given action (the depth of decomposition tree), but this is not what the formula says. 

Example below Definition 12: What is initial and goal step of a method? The definition of method does not contain anything like this. Actually, the description of decomposition resolver is very restrictive about how the method may look like. As I understand, you assume that a compound task has preconditions and effects and they may participate in causal links during the POCL step. When applying the decomposition resolver, you need to reconnect the casual links from the abstract actions to actions in the decomposition. This is the strange part as it means that the abstract task must cover all preconditions and effects from actions in its decomposition. If this is the case (and I see it as the only chance to guarantee your completeness result) then modeling framework will be extremely hard for usage and domain models for non-trivial domains will be huge. 

Definition 13: Again, this is confusing terminology and definition. Why not calling it a level rather than overweighted notion of cycle? Also, how do you know how many levels of decomposition you will need for planning? What if different alternative decompositions have different depths? 

Lemma at page 7: Though I agree that the decomposition does not bring cycles (you see, again the word cycle), I do not understand the arguments in the proof. Why do you talk there about fluents? 

The first Lemma at page 9: Again, I do not agree with arguments in the proof. First, I believe that the same action may appear more times in the plan so you need to talk about occurrence of actions if you want to say that action from the decomposition is not used elsewhere in the plan. It is not true that action a once decomposed at some level cannot appear later again at different level. Otherwise, the depth of decomposition for all the abstract actions must be identical which would be another strong restriction on the model. 

With the last lemma about guaranteed solvability I strongly disagree. I gave arguments above. This lemma is not true, if we use definitions as presented in the paper unless additional and very strong assumptions are applied. Finally, about the experiments, it is not really clear what they are supposed to say. Using a single domain to justify that some planner is faster than other is not enough. The second experiment uses some unspecified domains and again, it is not fully clear what Figure 4 is saying.


# Review #: 3	

Criterium	Description	Value
Abstract and Introduction are adequate?	 	No
Conclusions/Future Work are convincing?	 	Yes
Figures are adequate ?	in number and quality	Yes
Improve critical discussion ?	validation	Yes
Improve English?	 	No
Needs comparative evaluation?	 	Yes
Needs more experimental results?	 	Yes
Originality	Newness of the ideas expressed	2
Overall Rating	Weighted value of above items	2
Paper formatting needs adjustment?	 	No
Presentation	Structure/Length/English	2
References are up-to-date and appropriate?	 	No
Relevance	Paper fits one or more of the topic areas?	5
Significance	Is the problem worth the given attention?	2
Technical Quality	Theoretical soundness/methodology	2
Scale: 1:Lowest Value;6:Highest Value

Observations for Author
Because the issue addressed here was unclear; in introduction, motivation and abstract, the authors suggested the explainablitity of the results, but it was not discussed enough after these sections. In contract, in the section of experimental results, they only showed that performance (computional time). But this was not discussed well as the main issue in the Introduction; So we could not identify the actual issue(s) addressed in this paper. 

(1) Abstract: This part seems not to be written well. This is not the study related to the deep learning but the planning with symbol based approach, so it is usually explainable. 

(2) Explainability is a crucial and important issue, but I am not sure the contribution of this paper from this viewpoint. 

(3) In Experiment, the authors indicated the performance of planner, but if the issue of this paper is the performance, please discuss why the current planners are not sufficient and why the proposed planner satisfies the requirement with comparison the results of the conventional planners. 

(4) Several related works are referred, but some of them are redundant and are not up-to-date. In summary, the issue addressed here was not discussed well. The discussion in the first parts (abstract, introduction, and motivation) seemed not to fit the discussion in the latter sections. However, the technical part in Section 3 and 4 seems good, so i recommend to revise the former parts and submit it again.
