/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yocto.algorithms;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ReifiedStatement;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import yocto.rdf.Predicate;
import yocto.utils.Comparator;
import yocto.utils.Log;
import yocto.utils.Query;

/**
 *
 * @author antoine
 */
public class Correlation {

    private final Model model;
    public Map<Set<Resource>, List<Count>> correlation;

    public static class Count {

        public int properties;
        public int actions;

        public Count() {
            this(0, 0);
        }

        public Count(int properties, int actions) {
            this.properties = properties;
            this.actions = actions;
        }

        @Override
        public String toString() {
            return "#p=" + properties + "#a=" + actions;
        }

        public Count min(Count other) {
            return new Count(Math.min(properties, other.properties),
                    Math.min(actions, other.actions));
        }

        public double correlation(Count divisor) {
            return (((double) properties) / divisor.properties)
                    * (((double) actions) / divisor.actions);
        }

    }

    public Correlation(Model model, MinPlan minPlan) {
        this.model = model;
        correlation = new HashMap<>();
        Map<Resource, Count> counts = new HashMap<>();
        for (Map.Entry<Resource, Set<Resource>> plan : minPlan.plans.entrySet()) {
            counts.put(plan.getKey(), new Count(countProperties(plan.getValue()), plan.getValue().size()));
        }

        Set<Resource> closed = new HashSet<>();

        for (Resource goal1 : minPlan.goals) {
            closed.add(goal1);
            for (Resource goal2 : minPlan.goals) {
                if (closed.contains(goal2)) {
                    continue;
                }
                Log.i("Correlation :" + goal1 + " ~ " + goal2);

                Count count = counts.get(goal1).min(counts.get(goal2));

                Count common = new Count();
                // TODO Property grounding
                common.properties = commonProperties(minPlan.plans.get(goal1), minPlan.plans.get(goal2));

                // Actions
                Set<Resource> tmp = new HashSet<>(minPlan.plans.get(goal1));
                tmp.retainAll(minPlan.plans.get(goal2));
                common.actions = tmp.size();

                correlation.put(new HashSet<Resource>() {
                    {
                        add(goal1);
                        add(goal2);
                    }
                }, Arrays.asList(common, count));

                Log.i(common + " / " + count);
                Log.i("~ " + common.correlation(count));

            }
        }
    }

    @Override
    public String toString() {
        String result = "";
        for (Set<Resource> pair : correlation.keySet()) {
            List<Count> counts = correlation.get(pair);
            result += "Correlation :"
                    + pair.stream().map((goal) -> goal + " ~ ").
                    reduce(result, String::concat)
                    + counts.get(0).correlation(counts.get(1)) + "\n";

        }
        return result;
    }

    private int commonProperties(Set<Resource> actions1, Set<Resource> actions2) {
        Query query = Query.named().in("a1", actions1).in("a2", actions2);
        ResultSet results = query.execute(model);
        int result = 0;
        Set<Resource> closed = new HashSet<>();
        while (results.hasNext()) {
            QuerySolution next = results.next();
            Log.d(next.getResource("p") + " @ " + next.getResource("a1") + " ~ " + next.getResource("a2"));
            if ((!closed.contains(next.getResource("pre2")))
                    && Comparator.propertiesCompatible(
                            next.getResource("pre1").as(ReifiedStatement.class).getStatement(),
                            next.getResource("pre2").as(ReifiedStatement.class).getStatement()
                    )) {
                Log.d("COMPATIBLE !");
                ++result;
            }
            closed.add(next.getResource("pre1"));
        }
        query.close();
        return result;
    }

    private int countProperties(Set<Resource> actions) {
        Query query = Query.named().in("a", actions).groupBy(Arrays.asList("a"));
        ResultSet results = query.execute(model);
        int result = 0;
        while (results.hasNext()) {
            result += results.next().getLiteral("number").getInt();
        }
        query.close();
        return result;
    }

}
