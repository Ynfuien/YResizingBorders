package pl.ynfuien.yresizingborders.profiles;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import pl.ynfuien.yresizingborders.YResizingBorders;
import pl.ynfuien.yresizingborders.config.ConfigName;
import pl.ynfuien.yresizingborders.config.ConfigObject;
import pl.ynfuien.yresizingborders.utils.DoubleFormatter;
import pl.ynfuien.yresizingborders.utils.Lang;
import pl.ynfuien.yresizingborders.utils.Messenger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class BorderInterval {
    private final YResizingBorders instance;
    private final BorderProfiles borderProfiles;
    private BukkitTask interval = null;

    public BorderInterval(YResizingBorders instance, BorderProfiles profiles) {
        this.instance = instance;

        this.borderProfiles = profiles;
    }

    public void start() {
        DoubleFormatter df = new DoubleFormatter();

        interval = Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
            List<BorderProfile> resizedProfiles = new ArrayList<>();

            // Loop through all profiles, check if it's time for resize,
            // and then resize each world border, if it's not already on min/max size
            for (BorderProfile profile : borderProfiles.getProfiles()) {
                if (!profile.isEnabled()) continue;
                if (!profile.checkExecution()) continue;

                List<String> worlds = profile.getWorlds();
                if (worlds.isEmpty()) continue;

                boolean resizedAny = false;
                for (String world : worlds) {
                    World w = Bukkit.getWorld(world);
                    if (w == null) continue;

                    WorldBorder wb = w.getWorldBorder();
                    double size = wb.getSize();

                    double resizeBy = profile.getResizeBy();
                    double minSize = profile.getMinSize();
                    double maxSize = profile.getMaxSize();
                    int resizeTime = profile.getResizeTime();

                    double newSize = size + resizeBy;

                    if (resizeBy < 0) {
                        if (size <= minSize) continue;
                        if (newSize < minSize) {
                            resizeBy = size - minSize;
                            newSize = minSize;
                        }
                    } else {
                        if (size >= maxSize) continue;
                        if (newSize > maxSize) {
                            resizeBy = maxSize - size;
                            newSize = maxSize;
                        }
                    }

                    HashMap<String, Object> placeholders = new HashMap<>() {{
                        put("prefix", Lang.Message.PREFIX.get());
                        put("world", w.getName());
                        put("old-size", df.format(size));
                        put("resize-time-seconds", df.format(resizeTime));
                        put("resize-time-minutes", df.format((double) resizeTime / 60));
                        put("resize-time-hours", df.format((double) resizeTime / 60 / 60));
                    }};
                    placeholders.put("new-size", df.format(newSize));
                    placeholders.put("by", df.format(resizeBy));

                    String message = Lang.Message.BORDER_RESIZE_MESSAGE.get(placeholders);
                    if (profile.isResizeMessage()) message = Messenger.replacePlaceholders(profile.getResizeMessage(), placeholders);

                    for (Player p : Bukkit.getOnlinePlayers()) Messenger.send(p, message);

                    double finalNewSize = newSize;
                    Bukkit.getScheduler().runTask(instance, () -> wb.setSize(finalNewSize, resizeTime));

                    resizedAny = true;
                }

                if (resizedAny) {
                    profile.setLastResize(new Date());
                    resizedProfiles.add(profile);
                }
            }


            if (!resizedProfiles.isEmpty()) {
                ConfigObject configObject = instance.getConfigHandler().get(ConfigName.PROFILES);

                for (BorderProfile profile : resizedProfiles) {
                    profile.save();
                }

                configObject.save();
            }
        }, 20, 20);
    }

    public void stop() {
        if (interval == null) return;

        Bukkit.getScheduler().cancelTask(interval.getTaskId());
    }
}
