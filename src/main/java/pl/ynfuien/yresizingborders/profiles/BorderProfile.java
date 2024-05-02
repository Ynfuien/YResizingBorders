package pl.ynfuien.yresizingborders.profiles;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import pl.ynfuien.yresizingborders.YResizingBorders;
import pl.ynfuien.yresizingborders.config.ConfigName;
import pl.ynfuien.yresizingborders.utils.CronTask;
import pl.ynfuien.yresizingborders.utils.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BorderProfile {
    private final BorderProfiles borderProfiles;
    private final String name;
    private boolean enabled = false;
    private final List<String> worlds = new ArrayList<>();
    private double maxSize = -1;
    private double minSize = -1;
    private double resizeBy = 0;
    // Resize time in seconds
    private int resizeTime = 0;
    // Resize interval in milliseconds
    private long resizeInterval = -1;
    private Date lastResize;
    private CronTask resizeCrontask = null;
    private String resizeMessage = "";

    // Keys that profile config must have
    private final static String[] profileKeys = new String[] {"enabled", "worlds", "border", "border.max-size", "border.min-size", "border.resize"};
    private final static String[] resizeKeys = new String[] {"by", "time", "interval", "crontask"};

    public BorderProfile(BorderProfiles borderProfiles, String name) {
        this.borderProfiles = borderProfiles;
        this.name = name;
    }

    public boolean load(ConfigurationSection profile) {
        if (profile == null) return false;

        // Return if config doesn't have needed values
        for (String key : profileKeys) {
            if (profile.contains(key)) continue;

            // Return
            logError(String.format("Missing key '%s'!", key));
            return false;
        }

        // Enabled
        enabled = profile.getBoolean("enabled");

        // Worlds
        worlds.clear();
        List<String> worldList = profile.getStringList("worlds");
        for (String world : worldList) {
            if (!world.matches("[^ ]+")) {
                logError(String.format("Name of the world '%s' is incorrect. Profile won't affect this world!", world));
                continue;
            }

            worlds.add(world);
        }

        // Max size
        maxSize = profile.getDouble("border.max-size");
        if (maxSize < 1) {
            logError("Max border size can't be lower than 1!");
            return false;
        }

        // Min size
        minSize = profile.getDouble("border.min-size");
        if (minSize < 1) {
            logError("Min border size can't be lower than 1!");
            return false;
        }


        //// Resize section
        ConfigurationSection resize = profile.getConfigurationSection("border.resize");
        if (resize == null) return false;

        // Return if border resize section doesn't have needed values
        for (String key : resizeKeys) {
            if (resize.contains(key)) continue;

            // Return
            logError(String.format("Missing key 'border.resize.%s'!", key));
            return false;
        }

        // Resize by
        resizeBy = resize.getDouble("by");
        if (resizeBy == 0) {
            logError("Border resize by can't be 0! The whole purpose of this plugin is to RESIZE border >.<");
            return false;
        }

        // Resize time
        resizeTime = resize.getInt("time");
        if (resizeTime < 0) {
            logError("Border resize time can't be lower than 0!");
            return false;
        }

        // Last resize time
        lastResize = new Date(resize.getLong("last-resize"));

        // Resize message
        resizeMessage = resize.getString("message", "").trim();

        // Resize interval and crontask
        resizeInterval = (long) (resize.getDouble("interval") * 60 * 1000);
        String cronExpression = resize.getString("crontask", "").trim();
        if (cronExpression.isEmpty()) {
            if (resizeInterval < 1200) {
                logError("Border resize interval can't be lower than 0.02!");
                return false;
            }

            return true;
        }

        if (!setCronExpression(cronExpression)) {
            logError("Border resize crontask is invalid!");
            return false;
        }

        return true;
    }

    public boolean save() {
        FileConfiguration config = YResizingBorders.getInstance().getConfigHandler().getConfig(ConfigName.PROFILES);

        String p = name;
        config.set(p+".enabled", enabled);
        config.set(p+".worlds", worlds);

        p += ".border";
        config.set(p+".min-size", minSize);
        config.set(p+".max-size", maxSize);

        p += ".resize";
        config.set(p+".by", resizeBy);
        config.set(p+".time", resizeTime);
        config.set(p+".interval", (double) resizeInterval / 1000 / 60);
        config.set(p+".last-resize", lastResize.getTime());
        config.set(p+".crontask", isUsingCrontask() ? resizeCrontask.getExpression() : "");
        config.set(p+".message", resizeMessage);

        return true;
    }

    public boolean checkExecution() {
        if (isUsingCrontask()) return resizeCrontask.checkTime();

        Date now = new Date();
        return !new Date(now.getTime() - resizeInterval).before(lastResize);
    }

    private void logError(String message) {
        Logger.logWarning(String.format("[Profile] [%s] %s", name, message));
    }


    //// Getters
    public BorderProfiles getBorderProfiles() {
        return borderProfiles;
    }

    public String getName() {
        return name;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public List<String> getWorlds() {
        return worlds;
    }
    public double getMaxSize() {
        return maxSize;
    }
    public double getMinSize() {
        return minSize;
    }
    public double getResizeBy() {
        return resizeBy;
    }

    public int getResizeTime() {
        return resizeTime;
    }

    public long getResizeIntervalMilliseconds() {
        return resizeInterval;
    }
    public double getResizeIntervalMinutes() {
        return (double) resizeInterval / 60 / 1000;
    }

    public Date getLastResize() {
        return lastResize;
    }

    public CronTask getResizeCrontask() {
        return resizeCrontask;
    }

    public String getResizeMessage() {
        return resizeMessage;
    }

    public boolean isUsingCrontask() {
        return resizeCrontask != null;
    }
    public boolean isResizeMessage() {
        return !resizeMessage.isEmpty();
    }

    //// Setters
    public void enable() {
        enabled = true;
    }
    public void disable() {
        enabled = false;
    }

    public boolean setWorlds(List<String> worlds) {
        if (worlds == null) return false;

        for (String world : worlds) if (world.contains(" ")) return false;

        this.worlds.clear();
        this.worlds.addAll(worlds);
        return true;
    }

    public boolean setMaxSize(double maxSize) {
        if (maxSize < 1) return false;

        this.maxSize = maxSize;
        return true;
    }

    public boolean setMinSize(double minSize) {
        if (minSize < 1) return false;

        this.minSize = minSize;
        return true;
    }

    public boolean setResizeBy(double resizeBy) {
        if (resizeBy == 0) return false;

        this.resizeBy = resizeBy;
        return true;
    }

    public boolean setResizeTime(int resizeTime) {
        if (resizeTime < 0) return false;

        this.resizeTime = resizeTime;
        return true;
    }

    public boolean setResizeInterval(double resizeInterval) {
        if (resizeInterval < 0.02) return false;

        this.resizeInterval = (long) (resizeInterval * 60 * 1000);
        return true;
    }

    public void setLastResize(Date time) {
        lastResize = time;
    }

    public boolean setCronExpression(String cronExpression) {
        CronTask crontask = new CronTask(cronExpression);
        if (!crontask.validate()) return false;

        resizeCrontask = crontask;
        return true;
    }

    public void removeCrontask() {
        resizeCrontask = null;
    }

    public void setResizeMessage(@NotNull String message) {
        this.resizeMessage = message.trim();
    }
}
