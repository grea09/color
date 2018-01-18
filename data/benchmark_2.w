"lite.w";
(aa, ab, a) :: Action;

aaa eff (faaa);
aab eff (faab);
aba eff (faba);
abb eff (fabb);

aa method (
	init(aa) -> aaa,
	aaa -> aab,
	aab -> goal(aa)
);

ab method (
	init(ab) -> aba,
	aba -> abb,
	abb -> goal(ab)
);

a method (
	init(a) -> aa,
	aa -> ab,
	ab -> goal(a)
);

init eff ();
goal pre (fabb);
