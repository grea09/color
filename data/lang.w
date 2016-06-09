//-- title: Language --
// * matches everything
// _ means anything
// : means definition
// () for parameters
// Type[] means ordered list of Type
// Type{} means unordered set of Type
// <Type1, Type2> is used for tuples //TODO details
// Type1 / Type2 means that Type2 subsumes Type1
// Type1[n]->Type2[m] is for domain (m and n optional defaults to 1)
// = means equals
// !property means complementary
// .property is a hidden property

//# Base Types

lang : <.entity: Entity // Entity is the father of all type
	.statements: <subject:Entity, property:Property, object:Entity> // The language is simply a list of statements
	type: Entity->Type // Type is the metatype for reflexively treat type
	property: Statement->Property> // Property is used to type properties
