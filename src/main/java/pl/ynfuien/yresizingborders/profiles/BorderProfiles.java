package pl.ynfuien.yresizingborders.profiles;

import org.bukkit.configuration.ConfigurationSection;
import pl.ynfuien.yresizingborders.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class BorderProfiles {
    private final HashMap<String, BorderProfile> borderProfiles = new HashMap<>();

    public boolean load(ConfigurationSection profiles) {
        if (profiles == null) return false;

        borderProfiles.clear();
        Set<String> profileNames = profiles.getKeys(false);

        // Loop through all profiles and add them to hashmap if there is no errors
        for (String name : profileNames) {
            ConfigurationSection profileSection = profiles.getConfigurationSection(name);
            name = name.toLowerCase();

            if (profileSection == null) {
                logError(String.format("Profile '%s' couldn't be loaded because it doesn't have configuration section!", name));
                continue;
            }

            BorderProfile profile = new BorderProfile(this, name);
            if (!profile.load(profileSection)) {
                logError(String.format("Profile '%s' couldn't be loaded!", name));
                continue;
            }

            borderProfiles.put(name, profile);
            logInfo(String.format("Profile '%s' successfully loaded!", name));
        }

        logInfo(String.format("Successfully loaded %d border profile(s)!", borderProfiles.size()));
        return true;
    }


    //// Log methods
    private void logError(String message) {
        Logger.logWarning("[Profiles] " + message);
    }
    private void logInfo(String message) {
        Logger.log("[Profiles] " + message);
    }


    //// Getters
    public List<BorderProfile> getProfiles() {
        return new ArrayList<>(borderProfiles.values());
    }
    public BorderProfile get(String name) {
        return borderProfiles.get(name);
    }


    public boolean has(String name) {
        return borderProfiles.containsKey(name);
    }
}
