package pl.ynfuien.yresizingborders.hooks.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import pl.ynfuien.yresizingborders.YResizingBorders;
import pl.ynfuien.yresizingborders.hooks.placeholderapi.placeholders.ProfilePlaceholders;
import pl.ynfuien.yresizingborders.hooks.placeholderapi.placeholders.ProfilesPlaceholders;
import pl.ynfuien.yresizingborders.hooks.placeholderapi.placeholders.WorldborderPlaceholders;
import pl.ynfuien.yresizingborders.profiles.BorderProfiles;

public class PlaceholderAPIHook extends PlaceholderExpansion {
    private final YResizingBorders instance;
    private final BorderProfiles borderProfiles;

    private final Placeholder[] placeholders;

    public PlaceholderAPIHook(YResizingBorders instance) {
        this.instance = instance;

        borderProfiles = instance.getBorderProfiles();

        placeholders = new Placeholder[] {
            new ProfilePlaceholders(borderProfiles),
            new ProfilesPlaceholders(borderProfiles),
            new WorldborderPlaceholders()
        };
    }

    @Override @NotNull
    public String getAuthor() {
        return "Ynfuien";
    }

    @Override @NotNull
    public String getIdentifier() {
        return "yrb";
    }

    @Override @NotNull
    public String getVersion() {
        return instance.getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    // Profile
    // %yrb_profile_<profile>_enabled%
    // %yrb_profile_<profile>_worlds%
    // %yrb_profile_<profile>_border.min-size%
    // %yrb_profile_<profile>_border.max-size%
    // %yrb_profile_<profile>_resize.by%
    // %yrb_profile_<profile>_resize.time_seconds%
    // %yrb_profile_<profile>_resize.time_minutes%
    // %yrb_profile_<profile>_resize.time_hours%
    // %yrb_profile_<profile>_resize.interval_seconds%
    // %yrb_profile_<profile>_resize.interval_minutes%
    // %yrb_profile_<profile>_resize.interval_hours%
    // %yrb_profile_<profile>_resize.interval_days%
    // %yrb_profile_<profile>_resize.last-resize_timestamp%
    // %yrb_profile_<profile>_resize.last-resize_time%
    // %yrb_profile_<profile>_resize.last-resize_date%
    // %yrb_profile_<profile>_resize.last-resize_time-date%
    // %yrb_profile_<profile>_resize.crontask_expression%
    // %yrb_profile_<profile>_resize.crontask_description%
    // %yrb_profile_<profile>_resize.message%

    // Profiles
    // %yrb_profiles_count%
    // %yrb_profiles_list%

    // World border
    // %yrb_worldborder_size%
    // %yrb_worldborder_size_<world>%


    @Override
    public String onRequest(OfflinePlayer p, @NotNull String params) {
        Placeholder placeholder = null;

        // Loop through placeholders and get that provided by name
        for (Placeholder ph : placeholders) {
            if (params.startsWith(ph.name() + "_")) {
                placeholder = ph;
                break;
            }
        }

        // If provided placeholder is incorrect
        if (placeholder == null) return "incorrect placeholder";

        // Get placeholder properties from params
        String id = params.substring(placeholder.name().length() + 1);
        // Get placeholder result
        String result = placeholder.getPlaceholder(id, p);

        // If result is null
        if (result == null) return "incorrect property";

        // Return result
        return result;
    }
}