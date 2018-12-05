----------------------- REVIEW 1 ---------------------
PAPER: 8
TITLE: How explainable plans can make planning faster
AUTHORS: Antoine Gréa, Laetitia Matignon and Aknine Samir

Overall evaluation: 0 (borderline paper)

----------- Overall evaluation -----------
The paper presents the HEART ( Hierarchical Abstraction for Real Time) planner  which creates abstract intermediary plans by combining POCL and HTN. 

The paper is interesting but off topic, more relating to planning or even plan recognition.  While the consequence of this work may have implications that may connect to explainable AI - the connection is not clear and has not been demonstrated or discussed sufficiently. 
It may raise discussion and prove interesting to some of the XAI community. 

The related work is missing any work from the field of explainable AI. 

If accepted the authors should make sure to stress the implications and connections to explanation and provide more concrete, relevant examples.


----------------------- REVIEW 2 ---------------------
PAPER: 8
TITLE: How explainable plans can make planning faster
AUTHORS: Antoine Gréa, Laetitia Matignon and Aknine Samir

Overall evaluation: -1 (weak reject)

----------- Overall evaluation -----------
The paper described a combination of HTN and partial order planning to speed up the plan generation process while preserving the flexibility of the PoP approach and the representational advantages of HTNs. The paper might be interesting to the planning community in general. However, I failed to see any connections to explainable planning. I agree with the general idea that providing an abstract view of the plan generation process can be helpful, but the paper does little to expound on that.

The only place where I could find some XAIP connections is in Section 4.3 -- however, the cursory references to explainable planning are quite confusing. 

-- What is the process of using Sohrabi et al. 2016 (a quite expensive process) for intent recognition? What is the goal set here? Why is the planner trying to recognize its own intent?! 

-- The next example (and Figure 1) should be expanded to illustrate how this is going to help the user -- What is being displayed to the user? How is that helpful in explainable planning? 

As such, the paper reads as if it was written as a POCL-based refinement HTN planner and hastily adopted for the XAI workshop. I would suggest rewriting parts of the paper to highlight the contributions from the perspective of explainable planning.

Relevant: The authors should connect their approach to [1] which attempts explanation generation with model (predicate) abstractions. 

Minor: diverse planning lacks reference.

[1] Hierarchical Expertise-Level Modeling for User-Specific Robot-Behavior Explanations.
Sarath Sreedharan, Siddharth Srivastava, Subbarao Kambhampati. IJCAI 2018.

