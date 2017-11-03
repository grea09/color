/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.domain;

import io.genn.world.Flow;
import io.genn.world.World;
import io.genn.world.data.Entity;
import io.genn.world.data.Store;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import me.grea.antoine.utils.collection.Collections;
import me.grea.antoine.utils.text.Formater;

/**
 *
 * @author antoine
 * @param <F>
 */
public class Domain extends HashSet<Action> {

	public Domain() {
	}

	public Domain(Collection<? extends Action> c) {
		super(c);
	}

	@Override
	public String toString() {
		return Formater.toString(this, ", ");
	}

}
