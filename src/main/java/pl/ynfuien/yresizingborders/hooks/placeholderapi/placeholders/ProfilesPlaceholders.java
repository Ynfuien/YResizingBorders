package pl.ynfuien.yresizingborders.hooks.placeholderapi.placeholders;

import org.bukkit.OfflinePlayer;
import pl.ynfuien.yresizingborders.hooks.placeholderapi.Placeholder;
import pl.ynfuien.yresizingborders.profiles.BorderProfile;
import pl.ynfuien.yresizingborders.profiles.BorderProfiles;

import java.util.ArrayList;
import java.util.List;

public class ProfilesPlaceholders implements Placeholder {
    private final BorderProfiles borderProfiles;
    public ProfilesPlaceholders(BorderProfiles borderProfiles) {
        this.borderProfiles = borderProfiles;
    }

    @Override
    public String name() {
        return "profiles";
    }

    @Override
    public String getPlaceholder(String id, OfflinePlayer p) {
        // Placeholder: %yrb_profiles_count%
        // Returns: count of all profiles
        if (id.equals("count")) {
            return String.valueOf(borderProfiles.getProfiles().size());
        }

        // Placeholder: %yrb_profiles_list%
        // Returns: list of all profiles
        if (id.equals("list")) {
            List<String> profiles = new ArrayList<>();
            for (BorderProfile profile : borderProfiles.getProfiles()) {
                profiles.add(profile.getName());
            }

            return String.join(", ", profiles);
        }

        return null;
    }
}
