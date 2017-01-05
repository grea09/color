/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color;

import io.genn.world.World;
import io.genn.world.parser.Parser;
import me.grea.antoine.utils.Log;

/**
 *
 * @author antoine
 */
public class Main {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		Log.CONTEXTUALIZED = true;
		try {
			World config = new Parser("data/config.w").parse();
			World planning = new Parser("data/planning.w").parse();
			World domain = new Parser("data/domain.w").world(planning.new_); //no prob !
			
			
			
			//Huston !
			domain.addAll(new Parser("data/problem.w").world(domain.new_));
		} catch (Exception e) {
			Log.f(e);
		}
	}

}
