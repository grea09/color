/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.domain;

import io.genn.world.data.Property;
import io.genn.world.data.Quantified;
import io.genn.world.data.Statement;
import io.genn.world.data.Thing;

/**
 *
 * @author antoine
 */
public class WorldFluent extends Statement implements Fluent<WorldFluent> {

	public WorldFluent(Thing subject, Property property, Thing object) {
		super(subject, property, object);
	}

	@Override
	public boolean unifies(WorldFluent lesser) {
		return unify(subject(), lesser.subject()) 
				&& lesser.property().equals(property())
				&& unify(object(), lesser.object());
	}

	@Override
	public boolean contradicts(WorldFluent counter) {
		return !unifies(counter) 
				&& counter.property().equals(property()) 
				|| counter.property().negate().equals(property());
	}

	@Override
	public WorldFluent instanciate(WorldFluent lesser) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public WorldFluent negate() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean negative() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private static boolean unify(Thing left, Thing right) {
		return left.equals(right)
				|| left.equals(Quantified.SMTH) || right.equals(Quantified.SMTH);
	}

}
