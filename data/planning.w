//-- title: Definitions for automated planning --

Statement/Fluent;
(name::String, pre::Fluent{}, eff::Fluent{})/Action;
(source::Action, cause::Fluent{}, target::Action)/Link;
Link{}/Plan;

Action{}/Domain;
(initial::Fluent{}, goal::Fluent{}, domain::Domain, solution::Plan)/Problem;
