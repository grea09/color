"lite.w";

water :: Liquid;
cup :: Container;
spoon :: Ustensil;
sugar :: Additive;
(tea, coffee) :: Drink;

(taken(!), hot(_), ! in !) :: Fluent;


take(item) pre (taken(~), item ~(in) _);
take(item) eff (taken(item));

heat(thing) pre (~(hot(thing)), taken(thing));
heat(thing) eff (hot(thing));

pour(stuff,into) pre (stuff ~(in) into, taken(stuff));
pour(stuff,into) eff (stuff in into);

put(ustensil) pre (~(placed(ustensil)), taken(ustensil));
put(ustensil) eff (placed(ustensil), ~(taken(ustensil)));


infuse(extract,liquid,container) method (
	init(infuse) -> take(extract),
	init(infuse) -> take(liquid),
	take(liquid) -> heat(liquid),
	heat(liquid) -> pour(liquid, cup),
	take(extract) -> pour(extract, cup),
	pour(liquid, cup) -> goal(infuse),
	pour(extract, cup) -> goal(infuse),
	heat(liquid) -> goal(infuse)
);

make(drink) method (
	init(make) -> take(spoon),
	take(spoon) -> put(spoon),
	init(make) -> take(sugar),
	take(sugar) -> pour(sugar, cup),
	init(make) -> infuse(drink,water,cup),
	infuse(drink,water,cup) -> take(cup),
	take(cup) -> put(cup),
	put(spoon) -> goal(make),
	pour(sugar, cup) -> goal(make),
	infuse(drink,water,cup) -> goal(make),
	put(cup) -> goal(make)
);


init eff (hot(~), taken(~), placed(~), ~ in ~);
goal pre (hot(water), tea in cup, water in cup, sugar in cup, placed(spoon), placed(cup));