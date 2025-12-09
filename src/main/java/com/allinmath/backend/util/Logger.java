package com.allinmath.backend.util;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Logger {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Logger.class);

    private static boolean isDev;

    // Static injection of generic logger configuration
    public Logger(@Value("${spring.profiles.active:default}") String profile) {
        // Simple check, or could use environment variable.
        // Assuming "dev" or "local" or default means dev for now if not prod.
        // User asked: "DEBUG {printed only in develoment not in production}"
        // Let's assume production profile is named "prod" or "production".
        isDev = !profile.equalsIgnoreCase("prod") && !profile.equalsIgnoreCase("production");
    }

    public static void d(String message, Object... args) {
        if (isDev) {
            logger.debug(format(message, args));
        }
    }

    public static void i(String message, Object... args) {
        logger.info(format(message, args));
    }

    public static void w(String message, Object... args) {
        logger.warn(format(message, args));
    }

    public static void e(String message, Object... args) {
        logger.error(format(message, args));
    }

    public static void f(String message, Object... args) {
        // FATAL isn't a standard SLF4J level, usually mapped to ERROR or marker.
        // We will log as ERROR with a FATAL prefix marker.
        logger.error("FATAL: " + format(message, args));
    }

    private static String format(String message, Object... args) {
        // Simple formatting placeholder replacement if needed, or rely on SLF4J's {}
        // But the user requested "Logger.e('msg')" so likely string concatenation usage or simple string.
        // If args are provided, we can use String.format or similar usage?
        // Slf4j uses {}, user might expect printf style?
        // Let's assume standard string, but support slf4j passthrough effectively.
        // Actually, to keep it simple and safe for static calls:
        if (args == null || args.length == 0) {
            return message;
        }
        // Very basic formatter for %s or similar if user wants, 
        // or just let SLF4J handle {} if we passed it directly.
        // The implementation above passes the formatted string to underlying logger.
        // Let's assume we just pass the message and let developer format it before calling if using simple string,
        // OR we can use String.format.
        try {
            return String.format(message, args);
        } catch (Exception e) {
            return message; // Fallback if formatting fails
        }
    }
}
