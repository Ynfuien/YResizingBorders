package pl.ynfuien.yresizingborders.commands.subcommands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.ynfuien.yresizingborders.YResizingBorders;
import pl.ynfuien.yresizingborders.commands.Subcommand;
import pl.ynfuien.yresizingborders.profiles.BorderProfile;
import pl.ynfuien.yresizingborders.profiles.BorderProfiles;
import pl.ynfuien.yresizingborders.utils.DoubleFormatter;
import pl.ynfuien.yresizingborders.utils.Lang;
import pl.ynfuien.yresizingborders.utils.Messenger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InfoSubcommand implements Subcommand {
    @Override
    public String permission() {
        return "yresizingborders.command."+name();
    }

    @Override
    public String name() {
        return "info";
    }

    @Override
    public String description() {
        return Lang.Message.COMMAND_INFO_DESCRIPTION.get();
    }

    @Override
    public String usage() {
        return String.format("<%s>", Lang.Message.COMMANDS_USAGE_PROFILE.get());
    }

    private final MiniMessage miniMessage = Messenger.getMiniMessage();

    @Override
    public void run(CommandSender sender, Command command, String label, String[] args) {
        // Return if profile name isn't provided
        if (args.length == 0) {
            Lang.Message.COMMAND_INFO_FAIL_NO_PROFILE.send(sender);
            return;
        }

        // Get profile name from first arg
        String profileName = args[0].toLowerCase();
        // Get border profiles
        BorderProfiles borderProfiles = YResizingBorders.getInstance().getBorderProfiles();

        // Placeholders in messages
        HashMap<String, Object> placeholders = new HashMap<>();
        placeholders.put("profile-name", miniMessage.escapeTags(profileName));

        if (!borderProfiles.has(profileName)) {
            Lang.Message.COMMAND_INFO_FAIL_PROFILE_DOESNT_EXIST.send(sender, placeholders);
            return;
        }

        BorderProfile profile = borderProfiles.get(profileName);

        // {enabled} placeholder
        placeholders.put("enabled", profile.isEnabled() ? Lang.Message.COMMAND_INFO_PLACEHOLDER_ENABLED.get() : Lang.Message.COMMAND_INFO_PLACEHOLDER_DISABLED.get());

        // {worlds} placeholder
        String[] worlds = profile.getWorlds().toArray(String[]::new);
        for (int i = 0; i < worlds.length; i++) {
            worlds[i] = Lang.Message.COMMAND_INFO_PLACEHOLDER_WORLD.get().replace("{world-name}", worlds[i]);
        }
        placeholders.put("worlds", String.join(Lang.Message.COMMAND_INFO_PLACEHOLDER_WORLDS_SEPARATOR.get(), worlds));

        DoubleFormatter df = new DoubleFormatter();

        // {min-size} placeholder
        placeholders.put("min-size", df.format(profile.getMinSize()));
        // {max-size} placeholder
        placeholders.put("max-size", df.format(profile.getMaxSize()));

        // {resize-by} placeholder
        placeholders.put("resize-by", df.format(profile.getResizeBy()));

        // {resize-time} placeholder
        int resizeTime = profile.getResizeTime();
        placeholders.put("resize-time-seconds", resizeTime);
        placeholders.put("resize-time-minutes", df.format((double) resizeTime / 60));

        // {resize-interval} placeholder
        double intervalInMinutes = profile.getResizeIntervalMinutes();
        placeholders.put("interval", df.format(intervalInMinutes));
        String resizeIntervalPlaceholder = profile.isUsingCrontask() ? Lang.Message.COMMAND_INFO_PLACEHOLDER_RESIZE_NO_INTERVAL.get(placeholders) : Lang.Message.COMMAND_INFO_PLACEHOLDER_RESIZE_INTERVAL.get(placeholders);
        placeholders.put("resize-interval", resizeIntervalPlaceholder);

        // {resize-lastresize} placeholder
        String formattedLastResize = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(profile.getLastResize());
        placeholders.put("resize-lastresize", formattedLastResize);

        // {resize-crontask} placeholder
        String crontaskPlaceholder = Lang.Message.COMMAND_INFO_PLACEHOLDER_RESIZE_NO_CRONTASK.get();
        if (profile.isUsingCrontask()) {
            placeholders.put("resize-crontask-description", profile.getResizeCrontask().getDescription());
            placeholders.put("resize-crontask-expression", profile.getResizeCrontask().getExpression());
            crontaskPlaceholder = Lang.Message.COMMAND_INFO_PLACEHOLDER_RESIZE_CRONTASK.get(placeholders);
        }
        placeholders.put("resize-crontask", crontaskPlaceholder);

        // {resize-message} placeholder
        String resizeMessagePlaceholder = Lang.Message.COMMAND_INFO_PLACEHOLDER_RESIZE_NO_MESSAGE.get();
        if (profile.isResizeMessage()) {
            HashMap<String, Object> tmp = new HashMap<>();
            tmp.put("prefix", Lang.Message.PREFIX.get());
            tmp.put("message", profile.getResizeMessage());
//            tmp.put("resize-message-unformatted", miniMessage.escapeTags(profile.getResizeMessage()));
            resizeMessagePlaceholder = Lang.Message.COMMAND_INFO_PLACEHOLDER_RESIZE_MESSAGE.get(tmp);
        }
        placeholders.put("resize-message", resizeMessagePlaceholder);

        // All info messages to send
        Lang.Message[] messages = {
                Lang.Message.COMMAND_INFO_HEADER,
                Lang.Message.COMMAND_INFO_ENTRY_ENABLED,
                Lang.Message.COMMAND_INFO_ENTRY_WORLDS,
                Lang.Message.COMMAND_INFO_ENTRY_BORDER,
                Lang.Message.COMMAND_INFO_ENTRY_RESIZE,
                Lang.Message.COMMAND_INFO_ENTRY_RESIZE_BY,
                Lang.Message.COMMAND_INFO_ENTRY_RESIZE_TIME,
                Lang.Message.COMMAND_INFO_ENTRY_RESIZE_INTERVAL,
                Lang.Message.COMMAND_INFO_ENTRY_RESIZE_LASTRESIZE,
                Lang.Message.COMMAND_INFO_ENTRY_RESIZE_CRONTASK,
                Lang.Message.COMMAND_INFO_ENTRY_RESIZE_MESSAGE
        };

        for (Lang.Message message : messages) {
            message.send(sender, placeholders);
        }
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        // Create compltions list
        List<String> completions = new ArrayList<>();

        // Return empty array if args length is higher than 1
        if (args.length > 1) return completions;

        // Get first arg
        String arg1 = args[0].toLowerCase();

        // Loop through profiles
        BorderProfiles borderProfiles = YResizingBorders.getInstance().getBorderProfiles();
        for (BorderProfile profile : borderProfiles.getProfiles()) {
            // Get profile name
            String name = profile.getName();
            // Add profile name to completions if it starts with provided arg
            if (name.startsWith(arg1)) completions.add(name);
        }

        return completions;
    }
}
