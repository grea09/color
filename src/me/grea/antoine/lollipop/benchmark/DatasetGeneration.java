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
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.grea.antoine.lollipop.persistence.ActionSerializer;
import me.grea.antoine.lollipop.persistence.ProblemSerializer;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Problem;
import static me.grea.antoine.utils.Collections.list;
import me.grea.antoine.utils.Log;

/**
 *
 * @author antoine
 */
public class DatasetGeneration {

    static {
        Log.level = Log.Level.INFO;
    }

    public static void main(String[] args) throws FileNotFoundException {
        GsonBuilder bob = new GsonBuilder();
        bob.registerTypeAdapter(Action.class, new ActionSerializer());
        bob.registerTypeAdapter(Problem.class, new ProblemSerializer());
        Gson gson = bob.create(); //Damn son

        for (int enthropy = 5; enthropy < 100; enthropy++) {
            for (int hardness = 0; hardness < enthropy * 0.7; hardness++) {
                Log.i("enthropy = " + enthropy + " , hardness = " + hardness);
                File file = new File("data/problems_[" + enthropy +  "," + hardness +"].json");
                new PrintStream(file).print(gson.toJson(ProblemGenerator.generate(10, enthropy, hardness)));
            }
        }

    }

}
