# POOP
First thing first, the algorithm is meant to be more efficient thant SODA POP without being, … you know … too poopy.
Let's first enumerate the characteristics of the algorithm. The first thing to happen is the old defect resolution but only for illegal deffects. The name defect is to drop or to be used only for those now. The defect resolution is all about the plan graph and (also some basic things about actions). So first we delete/fix the bad actions (less work on the links then). If an action is fixed we fix its links. We then scan for liar links and cycles. In fact when geting through the graph for cycle finding we fix all found liar links. Once the graph is stable we continue to an improved POP.

Improvement to the old flaw selection mechanism is also meant to be installed using the utility function for each step while searching for candidates. We make sure that the most usefula actions gets selected first. (TODO improve utility function and store the outgoing and incomming degree of each step and updating them efficiently).
Another improvement meant to remove the redundant link problem from POP is that each threat detection gets delayed until all related subgoals gets solved. This way the check for path alternative is done when the plan is the most completed and we don't solve imaginary threats before we actually really need to.

The improved POP is all about optimization. We start by introducing a new couple of new flaws. The first is the *alternative* flaw. This flaw is all about finding alternative in the plan setup. The alternatives are for each step finding a better candidate in the plan. If none are found then we consider it solved. If not we issue a resolver that will replace the step by its concurent. (We won't search again a new operator to replace it with because this would be way too ineficient). Second flaw is the *orphan* flaw. This is meant to cut orphan action branches from the plan.


## Orphan 
outGoingDegree = 0

related: outGoingDegree = 1

## Alternative

a1 -f> a2 and a1 < a3, a1 -×> a2 (FIXME if equal lighter action)

related : subgoal a2, orphan a1


### Heuristic
Frequence of fluent
