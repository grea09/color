
Expression/Fluent;
Action : (pre::Fluent, eff::Fluent, cost::Integer);
Link : (source:Action, target:Action, cause:Fluent{});
Plan: Link{};
Domain: Action{};
Problem : (initial::Action, goal::Action, domain::Domain, plan::Plan);