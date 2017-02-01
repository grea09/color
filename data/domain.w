//-- title: Domain for blockworld --
  
-> "planning.w";

Entity/Block;

blockworld::Domain;
blockworld: {
	pick, put, stack, unstack
};

pick(x::Block): (
	pre:{~ on x, x table, ~ held},
	eff:{x held, x !table}
);

put(x::Block): (
	pre:{x held},
	eff:{~ held, x table}
);

stack(x::Block, y::Block): (
	pre:{x held, ~ on y},
	eff:{~ held, x on y}
);

unstack(x::Block, y::Block): (
	pre:{x held, x on y, ~ on x},
	eff:{x held, ~ on y}
);
