# ; // Language Operator and End Of Statement
:: :: Property(Entity, Type);
(~, !, _, *, ?)::Quantifier;
#;

//(boiled(_)) :: Fluent;


use(appliance) eff (used(appliance));

take(obj) eff (taken(obj));

boil(liquid) pre (used(boiler), ~(boiled(liquid)));
boil(liquid) eff (boiled(liquid));

make(x) pre (boiled(water), ~(ready(x)));
make(x) eff (ready(x));

makeWith(x, y) pre (boiled(water), ~(ready(x)), taken(y));
makeWith(x, y) eff (ready(x), added(y));

init eff (~(ready(*)), boiled(~));

goal pre (ready(covfefe), ready(tea), added(sugar), added(cream));