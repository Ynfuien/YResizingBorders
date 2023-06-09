package pl.ynfuien.yresizingborders.commands.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.ynfuien.yresizingborders.commands.Subcommand;
import pl.ynfuien.yresizingborders.utils.Lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static pl.ynfuien.yresizingborders.commands.MainCommand.subcommands;

public class HelpSubcommand implements Subcommand {
    @Override
    public String permission() {
        return "yresizingborders.command";
    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public String description() {
        return Lang.Message.COMMAND_HELP_DESCRIPTION.get();
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public void run(CommandSender sender, Command command, String label, String[] args) {
        // Get subcommands that the sender has permissions for
        List<Subcommand> canUse = Arrays.stream(subcommands).filter(cmd -> sender.hasPermission(cmd.permission())).toList();

        // Send top message
        Lang.Message.HELP_TOP.send(sender);

        // If player can't use any command
        if (canUse.size() == 0) {
            // Send help no commands message
            Lang.Message.HELP_NO_COMMANDS.send(sender);
            return;
        }

        // Get help command template
        Lang.Message template = Lang.Message.HELP_COMMAND_TEMPLATE;
        // Send help commands
        for (Subcommand cmd : canUse) {
            String name = cmd.name();
            String description = cmd.description();
            String usage = cmd.usage();

            template.send(sender, new HashMap<>() {{
                put("command", String.format("yrb %s%s", name, (usage != null ? " "+usage : "")));
                put("description", description);
            }});
        }
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
