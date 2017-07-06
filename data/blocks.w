"planning.w";

pickUp(x) pre (~ on x, x on table, ~ held true);
pickUp(x) eff (x ~(on) table, x held true);

putDown(x) pre (x held true);
putDown(x) eff (~ held true, x on table);

stack(x, y) pre (x held true, ~ on y);
stack(x, y) eff (~ held true, x on y);

unstack(x, y) pre (~ held true, x on y);
unstack(x, y) eff (x held true, ~ on y);

init eff (~ on a, ~ on b, ~ on c, ~ on d, 
		a on table, b on table, c on table, d on table,
		~ held true);

goal pre (d on c, c on b, b on a);

//TODO parse plans for methods in HTN