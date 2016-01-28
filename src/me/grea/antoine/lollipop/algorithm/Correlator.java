/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.algorithm;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.DirectedGraph;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Edge;
import me.grea.antoine.lollipop.type.Goal;
import me.grea.antoine.lollipop.type.Plan;
import me.grea.antoine.utils.Collections;

/**
 *
 * @author antoine
 */
public class Correlator {

    public Map<Set<Goal>, List<Count>> correlation;

    private static int countProperties(Plan plan1) {
        int count = 0;
        count = plan1.vertexSet().stream().map((action)
                -> action.preconditions.size() + action.effects.size()
        ).reduce(count, Integer::sum);
        return count;
    }

    private static Set<Integer> preconditions(Plan plan1) {
        Set<Integer> properties = new HashSet();
        for (Action action : plan1.vertexSet()) {
            properties.addAll(action.preconditions);
        }
        return properties;
    }

    private static Set<Integer> effects(Plan plan1) {
        Set<Integer> properties = new HashSet();
        for (Action action : plan1.vertexSet()) {
            properties.addAll(action.effects);
        }
        return properties;
    }

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

    public double correlation(Plan plan1, Plan plan2) {
        Count total = new Count(countProperties(plan1), plan1.vertexSet().size()).min(
                new Count(countProperties(plan2), plan2.vertexSet().size()));
        Count common = new Count(
                Collections.union(Collections.intersection(preconditions(plan1), preconditions(plan2)),
                        Collections.intersection(effects(plan1), effects(plan2))
                ).size(),
                Collections.intersection(plan1.vertexSet(), plan2.vertexSet()).size());
        return total.correlation(common);
    }

    public double ramirez(Plan base, Plan observed) {
        return base.vertexSet().size() - observed.vertexSet().size();
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
}
