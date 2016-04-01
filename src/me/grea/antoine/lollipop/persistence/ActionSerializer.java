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
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Collection;
import me.grea.antoine.lollipop.type.Action;

/**
 *
 * @author antoine
 */
public class ActionSerializer implements JsonSerializer<Action>, JsonDeserializer<Action> {

    @Override
    public JsonElement serialize(Action t, Type type, JsonSerializationContext jsc) {
        JsonObject object = new JsonObject();
        Gson gson = new Gson();
        object.add("s", gson.toJsonTree(t.symbol));
        object.add("⊧", gson.toJsonTree(t.preconditions));
        object.add("+", gson.toJsonTree(t.effects));
        object.add("f", gson.toJsonTree(t.fake));
        return object;
    }

    @Override
    public Action deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        JsonObject object = je.getAsJsonObject();
        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Integer>>() {
        }.getType();
        char symbol = gson.fromJson(object.get("s"), Character.class);
        return new Action(symbol=='I' ? null : gson.fromJson(object.get("⊧"), collectionType),
                symbol=='G' ? null : gson.fromJson(object.get("+"), collectionType),
                gson.fromJson(object.get("f"), Boolean.class));
    }

}
