"lite.w";
(a, b, c) :: Action;

c eff (f);

b method (
	init(b) -> c,
	c -> goal(b)
);

a method (
	init(a) -> b,
	b -> goal(a)
);

init eff ();
goal pre (f);
