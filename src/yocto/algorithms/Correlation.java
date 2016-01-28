/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yocto.algorithms;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import yocto.plannification.Action;
import yocto.plannification.Goal;
import yocto.plannification.Statement;
import yocto.rdf.NameSpace;
import me.grea.antoine.utils.Log;
import yocto.utils.Query;

/**
 *
 * @author antoine
 */
public class Correlation {

    private final Model model;
    public Map<Set<Goal>, List<Count>> correlation;

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
        Map<Goal, Count> counts = new HashMap<>();
        for (Map.Entry<Goal, Set<Action>> plan : minPlan.plans.entrySet()) {
            counts.put(plan.getKey(), new Count(countProperties(plan.getValue()), plan.getValue().size()));
        }

        Set<Goal> closed = new HashSet<>();

        for (Goal goal1 : minPlan.goals) {
            closed.add(goal1);
            for (Goal goal2 : minPlan.goals) {
                if (closed.contains(goal2)) {
                    continue;
                }
                Log.i("Correlation " + goal1 + " ~ " + goal2);

                Count count = counts.get(goal1).min(counts.get(goal2));

                Count common = new Count();
                // TODO Property grounding
                common.properties = commonProperties(minPlan.plans.get(goal1), minPlan.plans.get(goal2));

                // Actions
                Set<Resource> tmp = new HashSet<>(minPlan.plans.get(goal1));
                tmp.retainAll(minPlan.plans.get(goal2));
                common.actions = tmp.size();

                correlation.put(new HashSet<Goal>() {
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
        for (Set<Goal> pair : correlation.keySet()) {
            List<Count> counts = correlation.get(pair);
            result += "Correlation "
                    + pair.stream().map((goal) -> goal + " ~ ").
                    reduce(result, String::concat)
                    + counts.get(0).correlation(counts.get(1)) + "\n";

        }
        return result;
    }

    private int commonProperties(Set<Action> actions1, Set<Action> actions2) {
        Query query = Query.named().in("a1", actions1).in("a2", actions2);
        ResultSet results = query.execute(model);
        int result = 0;
        Set<Statement> closed = new HashSet<>();
        while (results.hasNext()) {
            QuerySolution next = results.next();
            Statement pre1 = next.getResource("pre1").as(Statement.class);
            Statement pre2 = next.getResource("pre2").as(Statement.class);

            Log.d(NameSpace.prefix(next.getResource("p").getURI()) + " @ "
                    + NameSpace.prefix(next.getResource("a1").getURI()) + " ~ "
                    + NameSpace.prefix(next.getResource("a2").getURI())
            );
            if ((!closed.contains(pre2))
                    && pre1.compatible(pre2).compatible()) {
                Log.d("COMPATIBLE !");
                ++result;
            }
            closed.add(pre1);
        }
        query.close();
        return result;
    }

    private int countProperties(Set<Action> actions) {
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
