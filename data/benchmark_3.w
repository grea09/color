"lite.w";
(aaa, aab, aac, aba, abb, abc, aca, acb, acc, aa, ab, ac, a) :: Action;

aaaa eff (faaaa);
aaab eff (faaab);
aaac eff (faaac);
aaba eff (faaba);
aabb eff (faabb);
aabc eff (faabc);
aaca eff (faaca);
aacb eff (faacb);
aacc eff (faacc);

abaa eff (fabaa);
abab eff (fabab);
abac eff (fabac);
abba eff (fabba);
abbb eff (fabbb);
abbc eff (fabbc);
abca eff (fabca);
abcb eff (fabcb);
abcc eff (fabcc);

acaa eff (facaa);
acab eff (facab);
acac eff (facac);
acba eff (facba);
acbb eff (facbb);
acbc eff (facbc);
acca eff (facca);
accb eff (faccb);
accc eff (faccc);

aaa method (
	init(aaa) -> aaaa,
	aaaa -> aaab,
	aaab -> aaac,
	aaac -> goal(aaa)
);
aab method (
	init(aab) -> aaba,
	aaba -> aabb,
	aaab -> aabc,
	aabc -> goal(aab)
);
aac method (
	init(aac) -> aaca,
	aaca -> aacb,
	aacb -> aacc,
	aacc -> goal(aac)
);
aba method (
	init(aba) -> abaa,
	abaa -> abab,
	abab -> abac,
	abac -> goal(aba)
);
abb method (
	init(abb) -> abba,
	abba -> abbb,
	abbb -> abbc,
	abbc -> goal(abb)
);
abc method (
	init(abc) -> abca,
	abca -> abcb,
	abcb -> abcc,
	abcc -> goal(abc)
);
aca method (
	init(aca) -> acaa,
	acaa -> acab,
	acab -> acac,
	acac -> goal(aca)
);
acb method (
	init(acb) -> acba,
	acba -> acbb,
	acbb -> acbc,
	acbc -> goal(acb)
);
acc method (
	init(acc) -> acca,
	acca -> accb,
	accb -> accc,
	accc -> goal(acc)
);

aa method (
	init(aa) -> aaa,
	aaa -> aab,
	aab -> aac,
	aac -> goal(aa)
);

ab method (
	init(ab) -> aba,
	aba -> abb,
	abb -> abc,
	abc -> goal(ab)
);

ac method (
	init(ac) -> aca,
	aca -> acb,
	acb -> acc,
	acc -> goal(ac)
);

a method (
	init(a) -> aa,
	aa -> ab,
	ab -> ac,
	ac -> goal(a)
);

init eff ();
goal pre (faccc);
