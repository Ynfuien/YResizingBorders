package pl.ynfuien.yresizingborders.commands.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.ynfuien.yresizingborders.YResizingBorders;
import pl.ynfuien.yresizingborders.commands.Subcommand;
import pl.ynfuien.yresizingborders.profiles.BorderProfile;
import pl.ynfuien.yresizingborders.profiles.BorderProfiles;
import pl.ynfuien.yresizingborders.utils.Lang;

import java.util.HashMap;
import java.util.List;

public class ListSubcommand implements Subcommand {
    @Override
    public String permission() {
        return "yresizingborders.command."+name();
    }

    @Override
    public String name() {
        return "list";
    }

    @Override
    public String description() {
        return Lang.Message.COMMAND_LIST_DESCRIPTION.get();
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public void run(CommandSender sender, Command command, String label, String[] args) {
        // Get border profiles
        BorderProfiles borderProfiles = YResizingBorders.getInstance().getBorderProfiles();

        Lang.Message.COMMAND_LIST_HEADER.send(sender);

        Lang.Message entry = Lang.Message.COMMAND_LIST_ENTRY;
        Lang.Message enabledProfile = Lang.Message.COMMAND_LIST_ENABLED_PROFILE;
        Lang.Message disabledProfile = Lang.Message.COMMAND_LIST_DISABLED_PROFILE;

        int i = 0;
        for (BorderProfile profile : borderProfiles.getProfiles()) {
            i++;

            boolean enabled = profile.isEnabled();
            String name = profile.getName();

            HashMap<String, Object> placeholders = new HashMap<>();
            placeholders.put("number", i);
            placeholders.put("profile-name", name);
            placeholders.put("command", String.format("%s info %s", label, name));

            String entryPlaceholder = enabled ? enabledProfile.get(placeholders) : disabledProfile.get(placeholders);
            placeholders.put("profile", entryPlaceholder);

            entry.send(sender, placeholders);
        }
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return null;
    }
}
