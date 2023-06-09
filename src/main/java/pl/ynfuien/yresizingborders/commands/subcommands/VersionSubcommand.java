package pl.ynfuien.yresizingborders.commands.subcommands;

import io.papermc.paper.plugin.configuration.PluginMeta;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.ynfuien.yresizingborders.YResizingBorders;
import pl.ynfuien.yresizingborders.commands.Subcommand;
import pl.ynfuien.yresizingborders.utils.Lang;

import java.util.HashMap;
import java.util.List;

public class VersionSubcommand implements Subcommand {
    @Override
    public String permission() {
        return "yresizingborders.command."+name();
    }

    @Override
    public String name() {
        return "version";
    }

    @Override
    public String description() {
        return Lang.Message.COMMAND_VERSION_DESCRIPTION.get();
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public void run(CommandSender sender, Command command, String label, String[] args) {
        PluginMeta info = YResizingBorders.getInstance().getPluginMeta();

        HashMap<String, Object> placeholders = new HashMap<>() {{
            put("name", info.getName());
            put("version", info.getVersion());
            put("author", info.getAuthors().get(0));
            put("description", info.getDescription());
            put("website", info.getWebsite());
        }};

        Lang.Message.COMMAND_VERSION.send(sender, placeholders);
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return null;
    }
}
