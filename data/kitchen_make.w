# ; // Language Operator and End Of Statement
:: :: Property(Entity, Type);
(~, !, _, *, ?)::Quantifier;
#;

boil(liquid) pre (~(boiled(liquid)));
boil(liquid) eff (boiled(liquid));

take(thing) pre (~(taken(thing)));
take(thing) eff (taken(thing));

infuse(brevage) pre (~(ready(brevage)), boiled(water), water in cup, taken(brevage)); 
infuse(brevage) eff (ready(brevage));
//use x for unification with something that gives the ingredients

pour(ingredient, container) pre (taken(container), taken(ingredient), ingredient ~(in) container);
pour(ingredient, container) eff (ingredient in container);

make :: Action;
make(drink) method (
	init(make) -> boil(water),
	boil(water) -> pour(water, cup),
	init(make) -> take(cup),
	take(cup) -> pour(water, cup),
	init(make) -> take(drink),
	take(drink) -> pour(drink, cup),
	pour(water, cup) -> pour(drink, cup),
	pour(drink, cup) -> infuse(drink),
	infuse(drink) -> goal(make)
);

init eff (ready(~), taken(~), boiled(~), * ~(in) *);
goal pre (ready(tea));