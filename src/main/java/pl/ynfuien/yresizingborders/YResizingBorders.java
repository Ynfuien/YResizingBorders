package pl.ynfuien.yresizingborders;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import pl.ynfuien.ydevlib.config.ConfigHandler;
import pl.ynfuien.ydevlib.messages.YLogger;
import pl.ynfuien.yresizingborders.commands.MainCommand;
import pl.ynfuien.yresizingborders.config.ConfigName;
import pl.ynfuien.yresizingborders.hooks.Hooks;
import pl.ynfuien.yresizingborders.profiles.BorderInterval;
import pl.ynfuien.yresizingborders.profiles.BorderProfiles;
import pl.ynfuien.yresizingborders.utils.Lang;

public final class YResizingBorders extends JavaPlugin {
    private static YResizingBorders instance;
    private final ConfigHandler configHandler = new ConfigHandler(this);
    private final BorderProfiles borderProfiles = new BorderProfiles();
    private final BorderInterval borderInterval = new BorderInterval(this, borderProfiles);

    private boolean reloading = false;

    @Override
    public void onEnable() {
        instance = this;
        YLogger.setup("<dark_aqua>[<aqua>Y<green>RB<dark_aqua>] <white>", getComponentLogger());

        loadConfigs();
        loadLang();

        // Load border profiles
        FileConfiguration profilesConfig = configHandler.getConfig(ConfigName.PROFILES);
        if (!borderProfiles.load(profilesConfig)) {
            YLogger.error("Border profiles couldn't be loaded! Correct 'profiles.yml' file and restart server, for plugin to work.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Load hooks
        Hooks.load(this);

        borderInterval.start();

        // Register command
        Bukkit.getPluginCommand("yresizingborders").setExecutor(new MainCommand());
        Bukkit.getPluginCommand("yresizingborders").setTabCompleter(new MainCommand());

        // BStats
        new Metrics(this, 23087);

        YLogger.info("Plugin successfully <green>enabled<white>!");
    }

    @Override
    public void onDisable() {
        borderInterval.stop();

        YLogger.info("Plugin successfully <red>disabled<white>!");
    }

    // Loads language config
    private void loadLang() {
        // Get lang config
        FileConfiguration config = configHandler.getConfig(ConfigName.LANG);

        // Reload lang
        Lang.loadLang(config);
    }

    private void loadConfigs() {
        configHandler.load(ConfigName.PROFILES);
        configHandler.load(ConfigName.LANG, true, true);
    }

    public boolean reloadPlugin() {
        reloading = true;

        borderInterval.stop();

        // Reload all configs
        configHandler.reloadAll();

        // Reload lang
        instance.loadLang();

        // Reload border profiles
        FileConfiguration profilesConfig = configHandler.getConfig(ConfigName.PROFILES);
        instance.getBorderProfiles().load(profilesConfig);

        borderInterval.start();

        reloading = false;
        return true;
    }

    public boolean isReloading() {
        return reloading;
    }


    //// Getters
    public static YResizingBorders getInstance() {
        return instance;
    }

    public BorderProfiles getBorderProfiles() {
        return borderProfiles;
    }
    public BorderInterval getBorderInterval() {
        return borderInterval;
    }
    public ConfigHandler getConfigHandler() {
        return configHandler;
    }
}
