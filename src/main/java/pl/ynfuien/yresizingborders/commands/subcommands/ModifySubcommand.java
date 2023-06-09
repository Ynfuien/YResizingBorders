package pl.ynfuien.yresizingborders.commands.subcommands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.ynfuien.yresizingborders.YResizingBorders;
import pl.ynfuien.yresizingborders.commands.Subcommand;
import pl.ynfuien.yresizingborders.config.ConfigName;
import pl.ynfuien.yresizingborders.profiles.BorderProfile;
import pl.ynfuien.yresizingborders.profiles.BorderProfiles;
import pl.ynfuien.yresizingborders.utils.CronTask;
import pl.ynfuien.yresizingborders.utils.DoubleFormatter;
import pl.ynfuien.yresizingborders.utils.Lang;
import pl.ynfuien.yresizingborders.utils.Messenger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ModifySubcommand implements Subcommand {
    @Override
    public String permission() {
        return "yresizingborders.command."+name();
    }

    @Override
    public String name() {
        return "modify";
    }

    @Override
    public String description() {
        return Lang.Message.COMMAND_MODIFY_DESCRIPTION.get();
    }

    @Override
    public String usage() {
        return String.format(
                "<%s> <%s> <%s>",
                Lang.Message.COMMANDS_USAGE_PROFILE.get(),
                Lang.Message.COMMANDS_USAGE_SETTING.get(),
                Lang.Message.COMMANDS_USAGE_VALUE.get()
        );
    }

    private final List<String> settings = new ArrayList<>(Arrays.asList(
            "worlds",
            "border.min-size",
            "border.max-size",
            "resize.by",
            "resize.time",
            "resize.interval",
            "resize.crontask",
            "resize.message"
    ));
    private final MiniMessage miniMessage = Messenger.getMiniMessage();

    @Override
    public void run(CommandSender sender, Command command, String label, String[] args) {
        // First argument - profile
        if (args.length < 1) {
            Lang.Message.COMMAND_MODIFY_FAIL_NO_PROFILE.send(sender);
            return;
        }

        // Get profile name from first arg
        String profileName = args[0].toLowerCase();
        YResizingBorders instance = YResizingBorders.getInstance();
        // Get border profiles
        BorderProfiles borderProfiles = instance.getBorderProfiles();

        // Placeholders in messages
        HashMap<String, Object> placeholders = new HashMap<>();
        placeholders.put("profile-name", profileName);

        if (!borderProfiles.has(profileName)) {
            Lang.Message.COMMAND_MODIFY_FAIL_PROFILE_DOESNT_EXIST.send(sender, placeholders);
            return;
        }
        BorderProfile profile = borderProfiles.get(profileName);


        // Second argument - setting
        if (args.length < 2) {
            Lang.Message.COMMAND_MODIFY_FAIL_NO_SETTING.send(sender, placeholders);
            return;
        }

        String setting = args[1].toLowerCase();
        placeholders.put("setting", setting);
        if (!settings.contains(setting)) {
            Lang.Message.COMMAND_MODIFY_FAIL_INCORRECT_SETTING.send(sender, placeholders);
            return;
        }


        // Third argument - value
        if (args.length < 3) {
            Lang.Message.COMMAND_MODIFY_FAIL_NO_VALUE.send(sender, placeholders);
            return;
        }
        String[] value = Arrays.copyOfRange(args, 2, args.length);

        ModifyResponse response = modifyProfile(profile, setting, value);
        if (response.message != null) {
            placeholders.putAll(response.placeholders);
            response.message.send(sender, placeholders);
        }

        if (!response.success) return;

        profile.save();
        instance.getConfigHandler().get(ConfigName.PROFILES).save();
    }

    private ModifyResponse modifyProfile(BorderProfile profile, String setting, String[] value) {
        HashMap<String, Object> placeholders = new HashMap<>();
        ModifyResponse response = new ModifyResponse(placeholders);

        if (setting.equals("resize.crontask")) {
            String cronExpresison = String.join(" ", value);
            placeholders.put("cron-expression", cronExpresison);
            placeholders.put("value", cronExpresison);

            if (Arrays.asList("remove", "none", "-", "delete").contains(cronExpresison)) {
                if (!profile.isUsingCrontask()) return response.setMessage(Lang.Message.COMMAND_MODIFY_CRONTASK_FAIL_DOESNT_EXIST);

                profile.removeCrontask();
                return response.setSuccess().setMessage(Lang.Message.COMMAND_MODIFY_CRONTASK_SUCCESS_REMOVE);
            }

            if (profile.isUsingCrontask() && profile.getResizeCrontask().getExpression().equals(cronExpresison)) {
                return response.setMessage(Lang.Message.COMMAND_MODIFY_FAIL_SAME_VALUE);
            }

            if (!CronTask.validateExpression(cronExpresison)) {
                return response.setMessage(Lang.Message.COMMAND_MODIFY_CRONTASK_FAIL);
            }

            if (!profile.setCronExpression(cronExpresison)) {
                return response.setMessage(Lang.Message.COMMAND_MODIFY_FAIL);
            }

            placeholders.put("cron-description", profile.getResizeCrontask().getDescription());
            return response.setSuccess().setMessage(Lang.Message.COMMAND_MODIFY_CRONTASK_SUCCESS);
        }

        if (setting.equals("resize.message")) {
            String message = String.join(" ", value);
            placeholders.put("resize-message", message);
            placeholders.put("value", message);

            if (profile.getResizeMessage().equals(message)) {
                return response.setMessage(Lang.Message.COMMAND_MODIFY_FAIL_SAME_VALUE);
            }

            profile.setResizeMessage(message);
            return response.setSuccess().setMessage(Lang.Message.COMMAND_MODIFY_RESIZE_MESSAGE_SUCCESS);
        }

        if (setting.equals("worlds")) {
            List<String> worlds = new ArrayList<>();
            for (String world : value) {
                if (Bukkit.getWorld(world) == null) {
                    placeholders.put("world-name", world);

                    return response.setMessage(Lang.Message.COMMAND_MODIFY_WORLDS_FAIL_WORLD_DOESNT_EXIST);
                }

                worlds.add(world);
            }

            if (!profile.setWorlds(worlds)) {
                return response.setMessage(Lang.Message.COMMAND_MODIFY_FAIL);
            }

            placeholders.put("worlds", miniMessage.escapeTags(String.join(", ", worlds)));
            return response.setSuccess().setMessage(Lang.Message.COMMAND_MODIFY_WORLDS_SUCCESS);
        }

        Double doubleValue = null;
        try {
            doubleValue = Double.valueOf(value[0]);
        } catch (NumberFormatException ignored) {}
        DoubleFormatter df = new DoubleFormatter();
        placeholders.put("value", df.format(doubleValue == null ? 0 : doubleValue));

        if (setting.equals("border.min-size")) {
            if (doubleValue == null) {
                return response.setMessage(Lang.Message.COMMAND_MODIFY_BORDER_MIN_SIZE_FAIL_INCORRECT_VALUE);
            }

            if (doubleValue < 1) {
                return response.setMessage(Lang.Message.COMMAND_MODIFY_BORDER_MIN_SIZE_FAIL_TOO_SMALL);
            }

            if (profile.getMinSize() == doubleValue) {
                return response.setMessage(Lang.Message.COMMAND_MODIFY_FAIL_SAME_VALUE);
            }

            if (!profile.setMinSize(doubleValue)) {
                return response.setMessage(Lang.Message.COMMAND_MODIFY_FAIL);
            }

            placeholders.put("min-size", df.format(doubleValue));
            return response.setSuccess().setMessage(Lang.Message.COMMAND_MODIFY_BORDER_MIN_SIZE_SUCCESS);
        }

        if (setting.equals("border.max-size")) {
            if (doubleValue == null) {
                return response.setMessage(Lang.Message.COMMAND_MODIFY_BORDER_MAX_SIZE_FAIL_INCORRECT_VALUE);
            }

            if (doubleValue < 1) {
                return response.setMessage(Lang.Message.COMMAND_MODIFY_BORDER_MAX_SIZE_FAIL_TOO_SMALL);
            }

            if (profile.getMaxSize() == doubleValue) {
                return response.setMessage(Lang.Message.COMMAND_MODIFY_FAIL_SAME_VALUE);
            }

            if (!profile.setMaxSize(doubleValue)) {
                return response.setMessage(Lang.Message.COMMAND_MODIFY_FAIL);
            }

            placeholders.put("max-size", df.format(doubleValue));
            return response.setSuccess().setMessage(Lang.Message.COMMAND_MODIFY_BORDER_MAX_SIZE_SUCCESS);
        }

        if (setting.equals("resize.by")) {
            if (doubleValue == null) {
                return response.setMessage(Lang.Message.COMMAND_MODIFY_RESIZE_BY_FAIL_INCORRECT_VALUE);
            }

            if (doubleValue == 0) {
                return response.setMessage(Lang.Message.COMMAND_MODIFY_RESIZE_BY_FAIL_ZERO);
            }

            if (profile.getResizeBy() == doubleValue) {
                return response.setMessage(Lang.Message.COMMAND_MODIFY_FAIL_SAME_VALUE);
            }

            if (!profile.setResizeBy(doubleValue)) {
                return response.setMessage(Lang.Message.COMMAND_MODIFY_FAIL);
            }

            placeholders.put("resize-by", df.format(doubleValue));
            return response.setSuccess().setMessage(Lang.Message.COMMAND_MODIFY_RESIZE_BY_SUCCESS);
        }

        if (setting.equals("resize.interval")) {
            if (profile.isUsingCrontask()) {
                return response.setMessage(Lang.Message.COMMAND_MODIFY_RESIZE_INTERVAL_FAIL_CRONTASK_IN_USE);
            }

            if (doubleValue == null) {
                return response.setMessage(Lang.Message.COMMAND_MODIFY_RESIZE_INTERVAL_FAIL_INCORRECT_VALUE);
            }

            if (doubleValue < 0.02) {
                return response.setMessage(Lang.Message.COMMAND_MODIFY_RESIZE_INTERVAL_FAIL_TOO_SMALL);
            }

            if (profile.getResizeIntervalMinutes() == doubleValue) {
                return response.setMessage(Lang.Message.COMMAND_MODIFY_FAIL_SAME_VALUE);
            }

            if (!profile.setResizeInterval(doubleValue)) {
                return response.setMessage(Lang.Message.COMMAND_MODIFY_FAIL);
            }

            placeholders.put("resize-interval", df.format(doubleValue));
            return response.setSuccess().setMessage(Lang.Message.COMMAND_MODIFY_RESIZE_INTERVAL_SUCCESS);
        }

        if (setting.equals("resize.time")) {
            if (doubleValue == null) {
                return response.setMessage(Lang.Message.COMMAND_MODIFY_RESIZE_TIME_FAIL_INCORRECT_VALUE);
            }

            int intValue = doubleValue.intValue();

            if (intValue < 0) {
                return response.setMessage(Lang.Message.COMMAND_MODIFY_RESIZE_TIME_FAIL_TOO_SMALL);
            }

            if (profile.getResizeTime() == doubleValue) {
                return response.setMessage(Lang.Message.COMMAND_MODIFY_FAIL_SAME_VALUE);
            }

            if (!profile.setResizeTime(intValue)) {
                return response.setMessage(Lang.Message.COMMAND_MODIFY_FAIL);
            }

            placeholders.put("resize-time", df.format(intValue));
            return response.setSuccess().setMessage(Lang.Message.COMMAND_MODIFY_RESIZE_TIME_SUCCESS);
        }

        return response;
    }

    private static class ModifyResponse {
        boolean success = false;
        Lang.Message message = null;
        HashMap<String, Object> placeholders;

        public ModifyResponse(HashMap<String, Object> placeholders) {
            this.placeholders = placeholders;
        }

        public ModifyResponse setSuccess() {
            this.success = true;
            return this;
        }

        public ModifyResponse setMessage(Lang.Message message) {
            this.message = message;
            return this;
        }

        public ModifyResponse setPlaceholders(HashMap<String, Object> placeholders) {
            this.placeholders = placeholders;
            return this;
        }
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        // Create compltions list
        List<String> completions = new ArrayList<>();

        // Return empty array if args length is higher than 3
        if (args.length > 3) return completions;

        // Get first arg
        String arg1 = args[0].toLowerCase();

        if (args.length == 1) {
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


        // Second arg
        String arg2 = args[1].toLowerCase();
        if (args.length == 2) {
            for (String setting : settings) {
                if (setting.startsWith(arg2)) completions.add(setting);
            }
            return completions;
        }


        // Third arg
        String arg3 = args[2].toLowerCase();
        if (arg2.equals("worlds")) {
            for (World world : Bukkit.getWorlds()) {
                String name = world.getName();
                if (name.toLowerCase().startsWith(arg3)) completions.add(name);
            }
            return completions;
        }

        return completions;
    }
}
