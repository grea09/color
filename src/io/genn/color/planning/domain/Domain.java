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
public class Domain<F extends Fluent> extends HashSet<Action<F>> {

    public Domain() {
    }
	
    public Domain(Flow flow, Class<? extends F> clas) {
		Store s = flow.store;
		Map<Entity, Entity> pres = new HashMap<>();
		Map<Entity, Entity> effs = new HashMap<>();
		
		// FIXME UGH ! So UGLY !
		for (Entity statement : s.querry(null, flow.interpreter.named("pre"))) { 
			pres.put(s.subject(statement), s.object(statement));
		}
		for (Entity statement : s.querry(null, flow.interpreter.named("eff"))) {
			effs.put(s.subject(statement), s.object(statement));
		}
		for (Entity action : Collections.union(pres.keySet(), effs.keySet())) {
			add(new Action<>(action, pres.get(action), effs.get(action), flow, clas));
		}
    }
    
	

    public Domain(Collection<? extends Action<F>> c) {
        super(c);
    }
    
    @Override
    public String toString() {
        return Formater.toString(this, "\n");
    }
    
}
