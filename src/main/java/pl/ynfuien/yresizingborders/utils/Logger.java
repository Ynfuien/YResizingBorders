package pl.ynfuien.yresizingborders.utils;

import org.bukkit.Bukkit;

public class Logger {
    private static String prefix;

    public static void setPrefix(String prefix) {
        Logger.prefix = prefix;
    }

    public static void log(String message) {
        Messenger.send(Bukkit.getConsoleSender(), prefix + message);
    }

    public static void logWarning(String message) {
        log("<yellow>" + message);
    }

    public static void logError(String message) {
        log("<red>" + message);
    }
}
