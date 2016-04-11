Hypothesis 

* updated operators are known to have been updated by the user
* user provided plans are know to be provided by them.
* user provided plans doesn't contain illegal stuff

#Soundness

Loop invariant : if forall s in subgoals, p satisfies s then p solves P

p is a solution if all pre(G) are satisfied. We can satisfy these precondition using operators iff their precondition are all satisfied. (Flaw selection with subgoals)

(Resolver selection) POP selects an effect e of o.

Lemma : if no loop in input, no loop in output.

Lemma : all remaining open condition in an input plan are satisfied.

Lemma : negative refinements conserve validity

Alternative always replace the removed link with a subgoal = equiv backtracking with all resolvers remaining => solvable

Orphan : no path exists between an orphan and the goal, removing them does not remove satisfaction of preconditions.


#Completeness

A plan is fully supported if all steps are fully suported and they are when each of their pre is and a pre is supported if at least a causal link provides it.

Plans made by Lollipop are fully supported since the validity is recorded with causal links in the alg.

Old proof : POP is complete

Lemma : If the input plan is valid, lollipop returns a valid plan.

Since alternative and orphans conserve validity and no subgoal or threats exists (unless alternative introduce them but POP is complete) then the result is valid.

Lemma : if the plan is incomplete.

Cf POP

Lemma : if the plan is empty.

Cf POP

Lemma : if the user plan leads to a dead end

PROBLEM : ex if 9+3 is in the plan, subgoal will fail and problem will fail.
SOLUTION : On failure, needer of the last flaw is deleted if it wasn't added by LOLLIPOP. They are deleted only once (until the input plan acts like an empty plan).

In this case, the backtracking is preserved and all possibilities are explored as in POP.


