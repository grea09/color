/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color;

import io.genn.world.World;
import io.genn.world.parser.ParseException;
import io.genn.world.parser.Parser;
import java.io.FileNotFoundException;
import me.grea.antoine.utils.Log;

/**
 *
 * @author antoine
 */
public class Color {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws FileNotFoundException, ParseException {
		Log.CONTEXTUALIZED = true;
		World config = Parser.world("data/config.w");

//		World planning = Parser.world("data/planning.w");
//		Log.d(planning);
//
//		World domain = Parser.world("data/domain.w", planning.space); //no prob !
//		Log.d(domain);
//
//		//Huston !
//		domain.addAll(Parser.world("data/problem.w", domain.space));
//		Log.d(domain);

		World problem = Parser.world("data/problem.w"); //no prob !
		Log.d(problem);
	}
}
