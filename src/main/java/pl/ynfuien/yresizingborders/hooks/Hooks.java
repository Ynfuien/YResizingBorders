package pl.ynfuien.yresizingborders.hooks;

import org.bukkit.Bukkit;
import pl.ynfuien.yresizingborders.YResizingBorders;
import pl.ynfuien.yresizingborders.hooks.placeholderapi.PlaceholderAPIHook;
import pl.ynfuien.yresizingborders.utils.Logger;

public class Hooks {
    private static PlaceholderAPIHook papiHook = null;

    public static void load(YResizingBorders instance) {
        // Register PlaceholderAPI hook
        if (isPapiEnabled()) {
            papiHook = new PlaceholderAPIHook(instance);
            if (!papiHook.register()) {
                papiHook = null;
                Logger.logError("[Hooks] Something went wrong while registering PlaceholderAPI hook!");
            }
            else {
                Logger.log("[Hooks] Successfully registered hook for PlaceholderAPI!");
            }
        }
    }

    public static boolean isPapiEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    public static boolean isPapiHookEnabled() {
        return papiHook != null;
    }
}
