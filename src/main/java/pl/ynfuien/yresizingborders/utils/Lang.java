package pl.ynfuien.yresizingborders.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import pl.ynfuien.ydevlib.messages.LangBase;
import pl.ynfuien.ydevlib.messages.Messenger;
import pl.ynfuien.ydevlib.messages.colors.ColorFormatter;

import java.util.HashMap;

public class Lang extends LangBase {
    public enum Message implements LangBase.Message {
        PREFIX,
        PLUGIN_IS_RELOADING,
        HELP_NO_COMMANDS,
        HELP_TOP,
        HELP_COMMAND_TEMPLATE,
        COMMANDS_USAGE_PROFILE,
        COMMANDS_USAGE_SETTING,
        COMMANDS_USAGE_VALUE,
        COMMANDS_INCORRECT,
        COMMANDS_NO_PERMISSION,
        COMMAND_HELP_DESCRIPTION,
        COMMAND_RELOAD_DESCRIPTION,
        COMMAND_MODIFY_DESCRIPTION,
        COMMAND_ENABLE_DESCRIPTION,
        COMMAND_DISABLE_DESCRIPTION,
        COMMAND_LIST_DESCRIPTION,
        COMMAND_INFO_DESCRIPTION,
        COMMAND_VERSION_DESCRIPTION,
        COMMAND_RELOAD_FAIL,
        COMMAND_RELOAD_SUCCESS,
        COMMAND_ENABLE_FAIL_NO_PROFILE,
        COMMAND_ENABLE_FAIL_PROFILE_DOESNT_EXIST,
        COMMAND_ENABLE_SUCCESS,
        COMMAND_ENABLE_ALREADY,
        COMMAND_DISABLE_FAIL_NO_PROFILE,
        COMMAND_DISABLE_FAIL_PROFILE_DOESNT_EXIST,
        COMMAND_DISABLE_SUCCESS,
        COMMAND_DISABLE_ALREADY,
        COMMAND_LIST_HEADER,
        COMMAND_LIST_ENTRY,
        COMMAND_LIST_ENABLED_PROFILE,
        COMMAND_LIST_DISABLED_PROFILE,
        COMMAND_INFO_FAIL_NO_PROFILE,
        COMMAND_INFO_FAIL_PROFILE_DOESNT_EXIST,
        COMMAND_INFO_HEADER,
        COMMAND_INFO_ENTRY_ENABLED,
        COMMAND_INFO_PLACEHOLDER_ENABLED,
        COMMAND_INFO_PLACEHOLDER_DISABLED,
        COMMAND_INFO_ENTRY_WORLDS,
        COMMAND_INFO_PLACEHOLDER_WORLD,
        COMMAND_INFO_PLACEHOLDER_WORLDS_SEPARATOR,
        COMMAND_INFO_ENTRY_BORDER,
        COMMAND_INFO_ENTRY_RESIZE,
        COMMAND_INFO_ENTRY_RESIZE_BY,
        COMMAND_INFO_ENTRY_RESIZE_TIME,
        COMMAND_INFO_ENTRY_RESIZE_INTERVAL,
        COMMAND_INFO_PLACEHOLDER_RESIZE_INTERVAL,
        COMMAND_INFO_PLACEHOLDER_RESIZE_NO_INTERVAL,
        COMMAND_INFO_ENTRY_RESIZE_LASTRESIZE,
        COMMAND_INFO_ENTRY_RESIZE_CRONTASK,
        COMMAND_INFO_PLACEHOLDER_RESIZE_CRONTASK,
        COMMAND_INFO_PLACEHOLDER_RESIZE_NO_CRONTASK,
        COMMAND_INFO_ENTRY_RESIZE_MESSAGE,
        COMMAND_INFO_PLACEHOLDER_RESIZE_MESSAGE,
        COMMAND_INFO_PLACEHOLDER_RESIZE_NO_MESSAGE,
        COMMAND_MODIFY_FAIL,
        COMMAND_MODIFY_FAIL_NO_PROFILE,
        COMMAND_MODIFY_FAIL_PROFILE_DOESNT_EXIST,
        COMMAND_MODIFY_FAIL_NO_SETTING,
        COMMAND_MODIFY_FAIL_INCORRECT_SETTING,
        COMMAND_MODIFY_FAIL_NO_VALUE,
        COMMAND_MODIFY_FAIL_SAME_VALUE,
        COMMAND_MODIFY_WORLDS_FAIL_WORLD_DOESNT_EXIST,
        COMMAND_MODIFY_WORLDS_SUCCESS,
        COMMAND_MODIFY_BORDER_MIN_SIZE_FAIL_INCORRECT_VALUE,
        COMMAND_MODIFY_BORDER_MIN_SIZE_FAIL_TOO_SMALL,
        COMMAND_MODIFY_BORDER_MIN_SIZE_SUCCESS,
        COMMAND_MODIFY_BORDER_MAX_SIZE_FAIL_INCORRECT_VALUE,
        COMMAND_MODIFY_BORDER_MAX_SIZE_FAIL_TOO_SMALL,
        COMMAND_MODIFY_BORDER_MAX_SIZE_SUCCESS,
        COMMAND_MODIFY_RESIZE_BY_FAIL_INCORRECT_VALUE,
        COMMAND_MODIFY_RESIZE_BY_FAIL_ZERO,
        COMMAND_MODIFY_RESIZE_BY_SUCCESS,
        COMMAND_MODIFY_RESIZE_TIME_FAIL_INCORRECT_VALUE,
        COMMAND_MODIFY_RESIZE_TIME_FAIL_TOO_SMALL,
        COMMAND_MODIFY_RESIZE_TIME_SUCCESS,
        COMMAND_MODIFY_RESIZE_INTERVAL_FAIL_CRONTASK_IN_USE,
        COMMAND_MODIFY_RESIZE_INTERVAL_FAIL_INCORRECT_VALUE,
        COMMAND_MODIFY_RESIZE_INTERVAL_FAIL_TOO_SMALL,
        COMMAND_MODIFY_RESIZE_INTERVAL_SUCCESS,
        COMMAND_MODIFY_CRONTASK_FAIL,
        COMMAND_MODIFY_CRONTASK_FAIL_DOESNT_EXIST,
        COMMAND_MODIFY_CRONTASK_SUCCESS,
        COMMAND_MODIFY_CRONTASK_SUCCESS_REMOVE,
        COMMAND_MODIFY_RESIZE_MESSAGE_SUCCESS,
        COMMAND_VERSION,
        BORDER_RESIZE_MESSAGE;

        /**
         * Gets name/path of this message.
         */
        @Override
        public String getName() {
            return name().toLowerCase().replace('_', '-');
        }

        /**
         * Gets original unformatted message.
         */
        public String get() {
            return Lang.get(getName());
        }

        /**
         * Gets message with parsed:
         * - {prefix} placeholder
         * - additional provided placeholders
         */
        public String get(HashMap<String, Object> placeholders) {
            return Lang.get(getName(), placeholders);
        }

        /**
         * Gets message with parsed:
         * - PlaceholderAPI
         * - {prefix} placeholder
         * - additional provided placeholders
         */
        public String get(CommandSender sender, HashMap<String, Object> placeholders) {
            return ColorFormatter.parsePAPI(sender, Lang.get(getName(), placeholders));
        }

        /**
         * Gets message as component with parsed:
         * - MiniMessage
         * - PlaceholderAPI
         * - {prefix} placeholder
         * - additional provided placeholders
         */
        public Component getComponent(CommandSender sender, HashMap<String, Object> placeholders) {
            return Messenger.parseMessage(sender, Lang.get(getName()), placeholders);
        }

        /**
         * Sends this message to provided sender.<br/>
         * Parses:<br/>
         * - MiniMessage<br/>
         * - PlaceholderAPI<br/>
         * - {prefix} placeholder
         */
        public void send(CommandSender sender) {
            this.send(sender, new HashMap<>());
        }

        /**
         * Sends this message to provided sender.<br/>
         * Parses:<br/>
         * - MiniMessage<br/>
         * - PlaceholderAPI<br/>
         * - {prefix} placeholder<br/>
         * - additional provided placeholders
         */
        public void send(CommandSender sender, HashMap<String, Object> placeholders) {
            Lang.sendMessage(sender, this, placeholders);
        }
    }
}