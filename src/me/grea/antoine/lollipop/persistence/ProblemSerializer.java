/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.persistence;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Domain;
import me.grea.antoine.lollipop.type.Plan;
import me.grea.antoine.lollipop.type.Problem;

/**
 *
 * @author antoine
 */
public class ProblemSerializer implements JsonSerializer<Problem>, JsonDeserializer<Problem>{

    @Override
    public JsonElement serialize(Problem t, Type type, JsonSerializationContext jsc) {
        JsonObject object = new JsonObject();
        Gson gson = new Gson();
        object.add("I", gson.toJsonTree(t.initial, Action.class));
        object.add("G", gson.toJsonTree(t.goal, Action.class));
        object.add("D", gson.toJsonTree(t.domain, Domain.class));
        object.add("q", gson.toJsonTree(t.expectedLength));
        return object;
    }

    @Override
    public Problem deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        JsonObject object = je.getAsJsonObject();
        Gson gson = new Gson();
        Problem problem = new Problem(gson.fromJson(object.get("I"), Action.class), 
                gson.fromJson(object.get("G"), Action.class), 
                gson.fromJson(object.get("D"), Domain.class), 
                new Plan());
        problem.expectedLength = gson.fromJson(object.get("q"), Integer.class);
        return problem;
    }
    
}
