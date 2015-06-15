/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yocto.utils;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 *
 * @author antoine
 */
public class Log {

    public static PrintStream out = System.err;
    public static boolean MULTILINE = false;
    public static boolean CONTEXTUALIZED = false;

    public static void v(Object toLog) {
        log(Level.VERBOSE, toLog);
    }

    public static void d(Object toLog) {
        log(Level.DEBUG, toLog);
    }

    public static void i(Object toLog) {
        log(Level.INFO, toLog);
    }

    public static void w(Object toLog) {
        log(Level.WARNING, toLog);
    }

    public static void e(Object toLog) {
        log(Level.ERROR, toLog);
    }

    public static void f(Object toLog) {
        log(Level.FATAL, toLog);
        System.exit(-1);
    }

    public static void w(Exception exception) {
        log(Level.WARNING, exception);
    }

    public static void e(Exception exception) {
        log(Level.ERROR, exception);
    }

    public static void f(Exception exception) {
        log(Level.FATAL, exception);
        System.exit(-1);
    }

    private static void log(Level level, Object toLog) {
        String message = "" + toLog;
        out.println(level + "\t"
                + (CONTEXTUALIZED ? caller() : "")
                + (message.length() > 20 && MULTILINE ? "\n\t" : " ")
                + message.replaceAll("\n", "\n\t")
        );
    }

    private static void log(Level level, Exception exception) {
        out.println(level + " "
                + (CONTEXTUALIZED ? caller() : "") + " "
                + exception.getLocalizedMessage());
        exception.printStackTrace(out);
    }

    private static StackTraceElement caller() {
        for (StackTraceElement element : new Exception().getStackTrace()) {
            if (!element.getClassName().equals(Log.class.getName())) {
                return element;
            }
        }
        throw new RuntimeException("Method should be called from outside of Log class.");
    }

    public static enum Level {

        VERBOSE,
        DEBUG,
        INFO,
        WARNING,
        ERROR,
        FATAL;

        @Override
        public String toString() {
            return "[" + name() + ']';
        }

    }
}
