/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yocto.utils;

import me.grea.antoine.utils.Log;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import yocto.plannification.Action;

/**
 *
 * @author antoine
 */
public class ModelManager {

    private static final ModelMaker factory = ModelFactory.createMemModelMaker();

    public static Model create() {
        return factory.createDefaultModel();
    }

    public static Model copy(Model source) {
        return source.difference(create());
    }

    public static Model from(Set<? extends Resource> resources) {
        Model model = create();
        for (Resource resource : resources) {
            model.add(resource.listProperties());
        }
        return model;
    }

    public static Model read(String path) {
        return read(new File(path));
    }

    public static Model read(File file) {
        Model model = create();
        InputStream in = null;
        try {
            // Open the bloggers RDF graph from the filesystem
            in = new FileInputStream(file);
            model.read(in, null); // null base URI, since model URIs are absolute
        } catch (FileNotFoundException ex) {
            Log.e(ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Log.e(ex);
            }
        }
        return model;
    }

}
