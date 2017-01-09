//-- title: Domain for blockworld --

Entity/Block;

pick::Action;
pick(x::Block): (
	pre:{~ on x, x table, ~ held},
	eff:{x held, x !table}
);

put::Action;
put(x::Block): (
	pre:{x held},
	eff:{~ held, x table}
);

stack::Action;
stack(x::Block, y::Block): (
	pre:{x held, ~ on y},
	eff:{~ held, x on y}
);

unstack::Action;
unstack(x::Block, y::Block): (
	pre:{x held, x on y, ~ on x},
	eff:{x held, ~ on y}
);
