package com.allinmath.backend.util;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Logger {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Logger.class);

    private static boolean isDev;

    // ANSI Colors
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_CYAN = "\u001B[36m";

    // Static injection of generic logger configuration
    public Logger(@Value("${spring.profiles.active:default}") String profile) {
        // Simple check, or could use environment variable.
        // Assuming "dev" or "local" or default means dev for now if not prod.
        isDev = !profile.equalsIgnoreCase("prod") && !profile.equalsIgnoreCase("production");
    }

    public static void d(String message, Object... args) {
        if (isDev) {
            String formatted = format(message, args);
            logger.debug(ANSI_CYAN + formatted + ANSI_RESET);
        }
    }

    public static void i(String message, Object... args) {
        String formatted = format(message, args);
        logger.info(ANSI_GREEN + formatted + ANSI_RESET);
    }

    public static void w(String message, Object... args) {
        String formatted = format(message, args);
        logger.warn(ANSI_YELLOW + formatted + ANSI_RESET);
    }

    public static void w(Throwable t, String message, Object... args) {
        String formatted = format(message, args);
        logger.warn(ANSI_YELLOW + formatted + ANSI_RESET, t);
    }

    public static void e(String message, Object... args) {
        String formatted = format(message, args);
        logger.error(ANSI_RED + formatted + ANSI_RESET);
    }

    public static void e(Throwable t, String message, Object... args) {
        String formatted = format(message, args);
        logger.error(ANSI_RED + formatted + ANSI_RESET, t);
    }

    public static void f(String message, Object... args) {
        // FATAL isn't a standard SLF4J level, usually mapped to ERROR or marker.
        // We will log as ERROR with a FATAL prefix marker.
        String formatted = format(message, args);
        logger.error(ANSI_RED + "FATAL: " + formatted + ANSI_RESET);
    }

    private static String format(String message, Object... args) {
        if (args == null || args.length == 0) {
            return message;
        }
        try {
            return String.format(message, args);
        } catch (Exception e) {
            return message; // Fallback if formatting fails
        }
    }
}
