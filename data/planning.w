//-- title: Planning --

Fluent : Statement/

Action : <pre:Fluent{}, eff:Fluent{}> // This also declares the properties pre and eff like:
	//pre : Action -> Fluent{}

Flag: <dummy, normal, initial, goal>
special: initial | goal

Action : <pre:Fluent{}, eff:Fluent{}, flag:Flag>

CausalLink : <source:Action, target:Action, cause:Fluent{}>

Plan: CausalLink{}
Domain: Action{}
Problem : <initial:Action, goal:Action, domain:Domain, plan:Plan>
