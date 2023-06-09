package pl.ynfuien.yresizingborders.managers.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import pl.ynfuien.yresizingborders.config.updater.ConfigUpdater;
import pl.ynfuien.yresizingborders.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class ConfigManager {
    private final Plugin plugin;
    private final HashMap<String, ConfigObject> configs = new HashMap<>();

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
    }

    // Gets file configuration by name
    public FileConfiguration getConfig(String name) {
        ConfigObject configObject = get(name);
        if (!configObject.isBuilt()) return configObject.build();

        return configObject.getConfig();
    }


    public ConfigObject get(String name) {
        if (configs.containsKey(name)) return configs.get(name);

        ConfigObject configObject = new ConfigObject(name);
        configs.put(name, configObject);

        return configObject;
    }

    // Creates config
    private FileConfiguration createConfig(ConfigObject options) {
        // Get name
        String name = options.getName();

        // Create file object of config
        File configFile = new File(plugin.getDataFolder(), name);

        // If file doesn't exist create it
        boolean factoryNew = false;
        if (!configFile.exists()) {
            logInfo("Config doesn't exist, creating new...", name);
            configFile.getParentFile().mkdirs();
            plugin.saveResource(name, false);
            factoryNew = true;
        }

        // Try loading config
        FileConfiguration config = new YamlConfiguration();
        try {
            // Load config
            config.load(configFile);
            // Return if config was just created
            if (factoryNew) return config;
            // Return if config shouldn't be updated
            if (!options.getUpdating()) return config;

            // Get default config
            FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource(name), StandardCharsets.UTF_8));

            // Get whether config is missing some keys
            boolean isMissingKeys = ((Supplier<Boolean>) () -> {
                for (String key : defaultConfig.getKeys(true)) {
                    if (config.contains(key)) continue;
                    if (options.getIgnoredKeys().contains(key)) continue;

                    boolean missing = true;
                    for (String dontUpdateKey : options.getIgnoredKeys()) {
                        if (key.startsWith(dontUpdateKey+".")) {
                            missing = false;
                            break;
                        }
                    }

                    if (missing) return true;
                }
                return false;
            }).get();

            // Return if loaded config isn't missing any key
            if (!isMissingKeys) return config;

            logError("Config is missing some keys, updating..", name);

            // Get date
            Date date = new Date();
            // Create date formatter
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm");

            // Split name at dot
            String[] split = name.split("\\.");
            // Create name for old config in format: <name>-old_<date>.<extension>
            String oldConfigName = String.format("%s-old_%s.%s", split[0], formatter.format(date), split[1]);

            logError(String.format("Old file will be saved as %s", oldConfigName), name);

            // Create file object for old config
            File oldConfig = new File(plugin.getDataFolder(), oldConfigName);

            // Copy existing config to old config path
            Files.copy(configFile.toPath(), oldConfig.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Update existing config
            ConfigUpdater.update(plugin, name, configFile);

            // Load updated config
            config.load(configFile);
            // And return it
            return config;
        } catch (IOException | InvalidConfigurationException e) {
            // Print stack trace
            e.printStackTrace();
            logError("An error occurred while loading config from file!", name);

            // If can't be used default config
            if (!options.canUseDefault()) return null;

            logError("Will be used default one...", name);
            // Get default config and return it
            return YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource(name), StandardCharsets.UTF_8));
        }
    }

    public void reloadConfigs() {
        for (ConfigObject config : configs.values()) {
            config.build();
        }
    }

    public boolean saveAll() {
        boolean allSaved = true;
        for (ConfigObject config : configs.values()) {
            if (!config.save()) allSaved = false;
        }

        return allSaved;
    }

    //// Log methods
    private void logInfo(String message, String name) {
        Logger.log(String.format("[Configs-%s] %s", name, message));
    }
    private void logError(String message, String name) {
        Logger.logWarning(String.format("[Configs-%s] %s", name, message));
    }

    // Config object class
    public class ConfigObject {
        private final File file;
        private String name;
        private FileConfiguration config;
        private boolean updating = true;
        private boolean canUseDefault = false;
        private List<String> ignoredKeys = new ArrayList<>();

        public ConfigObject(String name) {
            this.name = name;

            file = new File(plugin.getDataFolder(), name);
        }
        public ConfigObject(String name, boolean updating, boolean canUseDefault, List<String> ignoredKeys) {
            this.name = name;
            this.updating = updating;
            this.canUseDefault = canUseDefault;
            this.ignoredKeys = ignoredKeys;

            file = new File(plugin.getDataFolder(), name);
        }

        //// Methods
        public FileConfiguration build() {
            logInfo("Loading...", name);
            FileConfiguration fileConfig = createConfig(this);

            if (fileConfig == null) {
                logError("Fix the error and then restart server for plugin to work!", name);
                // Disable plugin
                plugin.getServer().getPluginManager().disablePlugin(plugin);

                return null;
            }

            config = fileConfig;

            logInfo("Successfully loaded!", name);
            return config;
        }

        public boolean save() {
            if (!isBuilt()) return false;

            try {
                ConfigUpdater.save(plugin, file, config);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();

                logError("Couldn't save config!", name);
                return false;
            }

            return true;
        }

        // Getters
        public String getName() {
            return name;
        }
        public FileConfiguration getConfig() {
            return config;
        }
        public boolean getUpdating() {
            return updating;
        }
        public boolean canUseDefault() {
            return canUseDefault;
        }
        public List<String> getIgnoredKeys() {
            return ignoredKeys;
        }
        public boolean isBuilt() {
            return config != null;
        }

        // Setters
        public ConfigObject setName(String name) {
            this.name = name;
            return this;
        }
        public ConfigObject setConfig(FileConfiguration config) {
            this.config = config;
            return this;
        }
        public ConfigObject setUpdating(boolean updating) {
            this.updating = updating;
            return this;
        }
        public ConfigObject setCanUseDefault(boolean canUseDefault) {
            this.canUseDefault = canUseDefault;
            return this;
        }
        public ConfigObject setIgnoredKeys(List<String> ignoredKeys) {
            this.ignoredKeys = ignoredKeys;
            return this;
        }
    }
}
