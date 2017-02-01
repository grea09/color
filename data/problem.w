//-- title: Problem for testing --

-> "domain.w";

problem::Problem;
problem : (
	initial:{a table, b table, c table, d table},
	goal : { d on c, c on b, b on a },
	domain:blockworld,
	solution:{}
);