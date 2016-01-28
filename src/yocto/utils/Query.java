/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yocto.utils;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import yocto.rdf.NameSpace;

/**
 *
 * @author antoine
 */
public class Query {

    private static final Map<String, Query> namedQuerries = new HashMap<String, Query>() {
        {
            put("actionsSatisfying", new Query().
                    select(Arrays.asList("a")).
                    startWhere().append(
                            "?x :pre ?pre .\n"
                            + "?pre rdf:predicate ?p.\n"
                            + "?eff rdf:predicate ?p.\n"
                            + "?a :eff ?eff.\n"
                            + "?a a :Action.\n"
                    )
            );
            put("goals", new Query().
                    select(Arrays.asList("g")).
                    startWhere().append("?g a :Goal.\n")
            );
            put("actions", new Query().
                    select(Arrays.asList("a")).
                    startWhere().append("?a a :Action.\n")
            );
            put("countProperties", new Query().
                    select(Arrays.asList("(count(*) AS ?number)")).
                    startWhere().append(
                            "?a :pre ?pre.\n"
                            + "?a a :Action.\n")
            );
            put("commonProperties", new Query().
                    select(Arrays.asList("pre1", "pre2", "a1", "a2", "p")).
                    startWhere().append(
                            "?pre1 rdf:predicate ?p.\n"
                            + "?pre2 rdf:predicate ?p.\n"
                            + "?a1 :pre ?pre1.\n"
                            + "?a2 :pre ?pre2.\n"
                            + "?a1 a :Action.\n"
                            + "?a2 a :Action.\n")
            );
            put("entities", new Query().
                    describe(Arrays.asList("e")).
                    startWhere().append("?e a :Entity.\n"
                                        + "MINUS { ?e :param ?a}\n")
            );
            put("preconditions", new Query().
                    select(Arrays.asList("pre")).
                    startWhere().append(
                            "?a :pre ?pre.\n"
                            + "?a a :Action.\n")
            );
            put("effects", new Query().
                    select(Arrays.asList("eff")).
                    startWhere().append(
                            "?a :eff ?eff.\n"
                            + "?a a :Action.\n")
            );

        }
    };

    private ParameterizedSparqlString sparql;
    private QueryExecution execution;

    public Query() {
        sparql = new ParameterizedSparqlString();
        sparql.setNsPrefixes(NameSpace.prefixes);
    }

    Query(String command) {
        this();
        sparql.setCommandText(command);
    }

    public Query(Query other) {
        sparql = new ParameterizedSparqlString(other.toString());
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    public Query append(String string) {
        sparql.append(string);
        return this;
    }

    public Query append(Node node) {
        sparql.appendNode(node);
        return this;
    }

    public Query set(String name, String value) {
        sparql.setLiteral(name, value);
        return this;
    }

    public Query set(String name, Node node) {
        sparql.setParam(name, node);
        return this;
    }

//    public Query in(String name, Set<String> list) {
//        sparql.append("FILTER(");
//        for (String item : list) {
//            sparql.append("?" + name + " = ");
//            append(item);
//            sparql.append(" || ");
//        }
//        sparql.append(false);
//        sparql.append(") .\n");
//        return this;
//    }
    public Query in(String name, Set<? extends Resource> list) {
        sparql.append("FILTER(");
        for (Resource item : list) {
            sparql.append("?" + name + " = ");
            append(item.asNode());
            sparql.append(" || ");
        }
        sparql.append(false);
        sparql.append(") .\n");
        return this;
    }

    public Query select(List<String> names) {
        instruction("SELECT", names);
        return this;
    }

    public Query construct(List<String> names) {
        instruction("CONSTRUCT", names);
        return this;
    }

    public Query describe(List<String> names) {
        instruction("DESCRIBE", names);
        return this;
    }

    public Query ask(List<String> names) {
        instruction("ASK", names);
        return this;
    }

    public Query where(String object, String predicate, String subjet) {
        sparql.append(object + " ");
        sparql.append(predicate + " ");
        sparql.append(subjet + " ");
        sparql.append(" .\n");
        return this;
    }

    public Query startWhere() {
        append("WHERE {\n");
        return this;
    }

    public Query closeWhere() {
        String query = sparql.toString();
        if (!query.endsWith("}") && !query.contains("GROUP BY")) {
            append("}");
        }
        return this;
    }

    public Query groupBy(List<String> names) {
        closeWhere();
        instruction("GROUP BY ", names);
        return this;
    }

    private void instruction(String keyWord, List<String> names) {
        append(keyWord + " ");
        for (String name : names) {
            if (name.startsWith("(")) {
                append(name);
            } else {
                append("?" + name);
            }
        }
        append("\n");
    }

    public <T> T execute(Model model, Map<String, Node> parameters) {
        if (parameters != null) {
            for (Map.Entry<String, Node> parameter : parameters.entrySet()) {
                sparql.setParam(parameter.getKey(), parameter.getValue());
            }
        }
        return execute(model);
    }

    public <T> T execute(Model model) {
        closeWhere();
        com.hp.hpl.jena.query.Query query = sparql.asQuery();
        execution = QueryExecutionFactory.create(query, model);
        switch (query.getQueryType()) {
            case com.hp.hpl.jena.query.Query.QueryTypeAsk:
                return (T) (Object) execution.execAsk();
            case com.hp.hpl.jena.query.Query.QueryTypeConstruct:
                return (T) execution.execConstruct();
            case com.hp.hpl.jena.query.Query.QueryTypeDescribe:
                return (T) execution.execDescribe();
            case com.hp.hpl.jena.query.Query.QueryTypeSelect:
                return (T) execution.execSelect();
            case com.hp.hpl.jena.query.Query.QueryTypeUnknown:
            default:
                throw new IllegalArgumentException();
        }
    }

    public static Set<Resource> toSet(ResultSet results, String varName) {
        Set<Resource> items = new HashSet<>();
        while (results.hasNext()) {
            items.add(results.next().getResource(varName));
        }
        return items;
    }

    public static <T extends Resource> Set<T> toSet(ResultSet results, String varName, Class<T> type) {
        Set<T> items = new HashSet<>();
        while (results.hasNext()) {
            items.add(results.next().getResource(varName).as(type));
        }
        return items;
    }

    public void close() {
        if (execution != null) {
            execution.close();
        }
    }

    public static Query named(String name) {
        return new Query(namedQuerries.get(name));
    }

    public static Query named() {
        for (StackTraceElement element : new Exception().getStackTrace()) {
            if (!element.getClassName().equals(Query.class.getName())) {
                return named(element.getMethodName());
            }
        }
        throw new RuntimeException("To use this method your method should have the same name as a named query !");
    }

    @Override
    public String toString() {
        return sparql.toString();
    }

}
