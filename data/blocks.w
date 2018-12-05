# ; // Language Operator and End Of Statement
:: :: Property(Entity, Type);
(~, !, _, *, ?)::Quantifier;
#;

//on :: (!(Block) -> !(Block));

//(_ on _, held(!), down(*)) :: Fluent;

pickUp(xu) pre (~ on xu, down(xu), held(~));
pickUp(xu) eff (~(down(xu)), held(xu));

putDown(xd) pre (held(xd));
putDown(xd) eff (held(~), down(xd));

stack(xs, ys) pre (held(xs), ~ on ys);
stack(xs, ys) eff (held(~), xs on ys);
stack(xs, ys) constr (xs ~(:) ys);

unstack(xn, yn) pre (held(~), xn on yn);
unstack(xn, yn) eff (held(xn), ~ on yn);
unstack(xn, yn) constr (xn ~(:) yn);

(a,b) :: Block;

init eff (~ on *, down(*), held(~));

//goal pre (d on c, c on b, b on a);

//goal pre (b on a);

goal pre (~ on a);

//TODO parse plans for methods in HTN
/*
add(x, y) method (
	init(add) -> pickUp(x), 
	pickUp(x) -> stack(x,y), 
	stack(x,y) -> goal(add)
);

remove(x, y) method (
	init(remove) -> unstack(x, y), 
	unstack(x, y) -> putDown(x), 
	putDown(x) -> goal(remove)
);*/