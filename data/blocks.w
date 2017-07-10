//"planning.w";

//on :: (!(Block) -> !(Block));

(! on !, held(!), down(_)) :: Fluent;

pickUp(x) pre (~ on x, down(x), held(~));
pickUp(x) eff (~(down(x)), held(x));

putDown(x) pre (held(x));
putDown(x) eff (held(~), down(x));

stack(x, y) pre (held(x), ~ on y);
stack(x, y) eff (held(~), x on y);

unstack(x, y) pre (held(~), x on y);
unstack(x, y) eff (held(x), ~ on y);

//init eff (~ on a, ~ on b, ~ on c, ~ on d, 
//		down(a), down(b), down(c), down(d),
//		held(~));

init eff (~ on a, ~ on b, down(a), down(b), held(~));

//goal pre (d on c, c on b, b on a);

goal pre (b on a);


//TODO parse plans for methods in HTN