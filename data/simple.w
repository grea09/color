# ; // Language Operator and End Of Statement
:: :: Property(Entity, Type);
(~, !, _, *, ?)::Quantifier;
#;

(boiled(_)) :: Fluent;

boil(x) eff (boiled(x));

make(x) pre (boiled(water), ~(ready(x)));
make(x) eff (ready(x));

init eff (~(ready(covfefe)));

goal pre (ready(covfefe));