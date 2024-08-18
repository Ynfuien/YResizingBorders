package pl.ynfuien.yresizingborders.commands.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.ynfuien.ydevlib.messages.colors.ColorFormatter;
import pl.ynfuien.yresizingborders.YResizingBorders;
import pl.ynfuien.yresizingborders.commands.Subcommand;
import pl.ynfuien.yresizingborders.config.ConfigName;
import pl.ynfuien.yresizingborders.profiles.BorderProfile;
import pl.ynfuien.yresizingborders.profiles.BorderProfiles;
import pl.ynfuien.yresizingborders.utils.Lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EnableSubcommand implements Subcommand {
    @Override
    public String permission() {
        return "yresizingborders.command."+name();
    }

    @Override
    public String name() {
        return "enable";
    }

    @Override
    public String description() {
        return Lang.Message.COMMAND_ENABLE_DESCRIPTION.get();
    }

    @Override
    public String usage() {
        return String.format("<%s>", Lang.Message.COMMANDS_USAGE_PROFILE.get());
    }

    @Override
    public void run(CommandSender sender, Command command, String label, String[] args) {
        // Return if profile name isn't provided
        if (args.length == 0) {
            Lang.Message.COMMAND_ENABLE_FAIL_NO_PROFILE.send(sender);
            return;
        }

        // Get profile name from first arg
        String profileName = args[0].toLowerCase();
        // Get border profiles
        BorderProfiles borderProfiles = YResizingBorders.getInstance().getBorderProfiles();

        // Placeholders in messages
        HashMap<String, Object> placeholders = new HashMap<>();
        placeholders.put("profile-name", ColorFormatter.SERIALIZER.escapeTags(profileName));

        if (!borderProfiles.has(profileName)) {
            Lang.Message.COMMAND_ENABLE_FAIL_PROFILE_DOESNT_EXIST.send(sender, placeholders);
            return;
        }

        BorderProfile profile = borderProfiles.get(profileName);
        if (profile.isEnabled()) {
            Lang.Message.COMMAND_ENABLE_ALREADY.send(sender, placeholders);
            return;
        }

        profile.enable();
        profile.save();
        YResizingBorders.getInstance().getConfigHandler().getConfigObject(ConfigName.PROFILES).save();
        Lang.Message.COMMAND_ENABLE_SUCCESS.send(sender, placeholders);
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        // Create completions list
        List<String> completions = new ArrayList<>();

        // Return empty array if args length is higher than 1
        if (args.length > 1) return completions;

        // Get first arg
        String arg1 = args[0].toLowerCase();

        // Loop through profiles
        BorderProfiles borderProfiles = YResizingBorders.getInstance().getBorderProfiles();
        for (BorderProfile profile : borderProfiles.getProfiles()) {
            if (profile.isEnabled()) continue;

            // Get profile name
            String name = profile.getName();
            // Add profile name to completions if it starts with provided arg
            if (name.startsWith(arg1)) completions.add(name);
        }

        return completions;
    }
}
