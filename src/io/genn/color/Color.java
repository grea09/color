/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color;

import io.genn.world.World;
import io.genn.world.data.Property;
import static io.genn.world.data.Quantified.SMTH;
import io.genn.world.data.Thing;
import io.genn.world.parser.ParseException;
import io.genn.world.parser.Parser;
import io.genn.world.utils.InferenceException;
import io.genn.world.utils.NameSpace;
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
	public static void main(String[] args) throws FileNotFoundException,
												  ParseException,
												  InferenceException {
		World config = Parser.world("data/config.w");

		World problem = Parser.world("data/problem.w"); //no prob !
		Log.d(problem);
		NameSpace space = problem.space;

		Thing noProb = problem.query(SMTH, Property.TYP, space.get("Problem"));
		Log.i(noProb);
	}
}
