//-- title: Definitions for automated planning --


Fluent:Statement;
Action:(name::String, pre::Fluent{}, eff::Fluent{});
Link:(source::Action, cause::Fluent{}, target::Action);
Plan:Link{};

Domain:Action{};
Problem:(initial::Fluent{}, goal::Fluent{}, domain::Domain, solution::Plan);
