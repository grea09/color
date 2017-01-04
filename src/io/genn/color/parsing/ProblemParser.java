/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.parsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.Action.Flag;
import io.genn.color.planning.domain.Domain;
import io.genn.color.planning.domain.IntFluent;
import io.genn.color.planning.domain.State;
import io.genn.color.planning.problem.Problem;
import me.grea.antoine.utils.Log;

/**
 *
 * @author antoine
 */
public class ProblemParser {

    public enum Delimiter {
        PARENTHESIS('(', ')'),
        BRACKET('[', ']'),
        BRACE('{', '}'),
        ANGLE('<', '>');
        public final char start;
        public final char end;

        private Delimiter(char start, char end) {
            this.start = start;
            this.end = end;
        }

        public static Delimiter get(char c) {
            for (Delimiter value : values()) {
                if (c == value.start || c == value.end) {
                    return value;
                }
            }
            return null;
        }

        public static boolean is(char c) {
            for (Delimiter value : values()) {
                if (c == value.start || c == value.end) {
                    return true;
                }
            }
            return false;
        }

    }

    public static Problem<IntFluent> parse(File input) throws IOException {
        String content = new String(Files.readAllBytes(input.toPath()));
        Matcher matcher = Pattern.compile("(\\w*)\\s*::\\s*Problem").matcher(content); // first Problem
        String instance = matcher.find() ? matcher.group(1) : "problem";
        String definition = definition(instance, content);
        matcher = Pattern.compile("initial").matcher(definition); // initial
        if (!matcher.find()) {
            return null;
        }
        Action<IntFluent> inital = parse(Flag.INITIAL, definition.substring(matcher.end()));
        matcher = Pattern.compile("goal").matcher(definition); // initial
        if (!matcher.find()) {
            return null;
        }
        Action<IntFluent> goal = parse(Flag.GOAL, definition.substring(matcher.end()));

        matcher = Pattern.compile("domain").matcher(definition); // initial
        if (!matcher.find()) {
            return null;
        }

        Domain<IntFluent> domain = parseDomain(definition.substring(matcher.end()));

        return new Problem(inital, goal, domain);
    }

    public static String definition(String name, String content) {
        Matcher matcher = Pattern.compile(name + "\\s*:[^:]").matcher(content); // start of the definition
        if (!matcher.find()) {
            return "";
        }
        return braced(content, matcher.end());
    }

    public static String braced(String content) {
        return braced(content, 0);
    }

    public static String braced(String content, int start) {
        while (start<content.length()-1 && !Delimiter.is(content.charAt(start))) {
            start++;
        }
        Delimiter delimiter = Delimiter.get(content.charAt(start));
        int end = ++start;
        int count = 1;
        for (int i = start; count != 0 && i < content.length(); i++) { // normally  the last definition wins
            if (content.charAt(i) == delimiter.start) {
                count++;
            } else if (content.charAt(i) == delimiter.end) // works except for ()
            {
                count--;
            }
            end = i;
        }
        return content.substring(start, end);
    }

    public static Domain<IntFluent> parseDomain(String input) {
        Domain<IntFluent> domain = new Domain<>();
        BufferedReader lines = new BufferedReader(new StringReader(braced(input)));
        String line;
        try {
            while ((line = lines.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                domain.add(parse(Flag.NORMAL, line));
            }
        } catch (IOException ex) {
            Log.f(ex);
        }
        return domain;
    }

    public static Action<IntFluent> parse(Flag flag, String input) {
        String content = braced(input);
        Set<IntFluent> pre = new HashSet<>();
        Set<IntFluent> eff = new HashSet<>();

        Matcher matcher = Pattern.compile("pre\\s+").matcher(content); // pre
        if (matcher.find()) {
            pre = parse(content.substring(matcher.end()));
        }

        matcher = Pattern.compile("eff\\s+").matcher(content); // eff
        if (matcher.find()) {
            eff = parse(content.substring(matcher.end()));
        }
        return new Action<>(new State<IntFluent>(pre), new State<IntFluent>(eff), flag);
    }

    public static Set<IntFluent> parse(String input) {
        Set<IntFluent> result = new HashSet<>();
        String content = braced(input);
        for (String integer : content.split(",")) {
            result.add(new IntFluent(Integer.parseInt(integer.trim())));
        }
        return result;
    }
}
