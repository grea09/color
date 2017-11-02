# ; // Language Operator and End Of Statement
:: :: Property(Entity, Type);
(~, !, _, *, ?)::Quantifier;
#;

//(_ @ !, _ city !, ! in !, plane(_)) :: Fluent;
(! @ !) :: Fluent;

drive(t, from, to) pre (t @ from, ?(to));
drive(t, from, to) eff (t ~(@) from, t @ to);
drive(t, from, to) constr (from ~(:) to, from city c, to city c, ?(t));


fly(a, flyFrom, flyTo) pre (plane(a), a @ flyFrom, ?(flyTo));
fly(a, flyFrom, flyTo) eff (a ~(@) flyFrom, a @ flyTo);
fly(a, flyFrom, flyTo) constr (flyFrom ~(:) flyTo, ?(a));

load(vL, pkL, locL) pre (vL @ locL, pkL @ locL);
load(vL, pkL, locL) eff (pkL ~(@) locL, pkL in vL);

unload(vU, pkU, locU) pre (vU @ locU, pkU in vU);
unload(vU, pkU, locU) eff (pkU @ locU, pkU ~(in) vU);

(truckRoger, truckMarcel) :: Truck;
(saxe, maginot, doua) :: Place;
(lyon, bourg) :: City;

init eff (	?(*),
			truckRoger @ saxe,
			truckMarcel @ maginot,
			saxe city lyon,
			doua city lyon,
			maginot city bourg
);

goal pre (truckRoger @ doua);