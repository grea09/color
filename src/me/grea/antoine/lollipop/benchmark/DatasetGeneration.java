/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.benchmark;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import me.grea.antoine.lollipop.persistence.ActionSerializer;
import me.grea.antoine.lollipop.persistence.ProblemSerializer;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Problem;
import me.grea.antoine.utils.Log;

/**
 *
 * @author antoine
 */
public class DatasetGeneration {

    static {
        Log.level = Log.Level.VERBOSE;
    }

    public static void main(String[] args) throws FileNotFoundException {
        GsonBuilder bob = new GsonBuilder();
        bob.registerTypeAdapter(Action.class, new ActionSerializer());
        bob.registerTypeAdapter(Problem.class, new ProblemSerializer());
        Gson gson = bob.create(); //Damn son
        
        for (int enthropy = 5; enthropy < 15; enthropy++) {
            for (int hardness = 1; hardness < enthropy * 0.5; hardness++) {
                Log.i("enthropy = " + enthropy + " , hardness = " + hardness);
                File file = new File("data/problems_[" + enthropy +  "," + hardness +"].json");
                new PrintStream(file).print(gson.toJson(ProblemGenerator.generate(100, enthropy, hardness)));
            }
        }

    }

}
