package pl.ynfuien.yresizingborders.hooks.placeholderapi.placeholders;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import pl.ynfuien.ydevlib.utils.DoubleFormatter;
import pl.ynfuien.yresizingborders.hooks.placeholderapi.Placeholder;

public class WorldborderPlaceholders implements Placeholder {
    private final DoubleFormatter df = new DoubleFormatter();

    public WorldborderPlaceholders() {}

    @Override
    public String name() {
        return "worldborder";
    }

    @Override
    public String getPlaceholder(String id, OfflinePlayer p) {
        // Placeholder: %yrb_worldborder_size%
        // Returns: size of the border in player's world
        if (id.equals("size")) {
            if (p == null) return "player is not provided";
            Player player = p.getPlayer();
            if (player == null) return "player is offline";

            return df.format(player.getWorld().getWorldBorder().getSize());
        }

        // Placeholder: %yrb_worldborder_size_<world>%
        // Returns: size of the border of provided world
        if (id.startsWith("size_")) {
            String worldName = id.substring(5).trim();
            if (worldName.isEmpty()) return "no world name provided";

            World world = Bukkit.getWorld(worldName);
            if (world == null) return "world doesn't exist";

            return df.format(world.getWorldBorder().getSize());
        }

        return null;
    }
}
