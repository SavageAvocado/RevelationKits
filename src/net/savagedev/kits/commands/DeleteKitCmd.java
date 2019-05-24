package net.savagedev.kits.commands;

import net.savagedev.kits.RevelationKits;
import net.savagedev.kits.utils.MessageUtils;
import net.savagedev.kits.utils.io.FileUtils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class DeleteKitCmd implements CommandExecutor, TabCompleter {
    private final RevelationKits plugin;

    public DeleteKitCmd(RevelationKits plugin) {
        this.plugin = plugin;
        this.init();
    }

    private void init() {
        PluginCommand command = this.plugin.getCommand("deletekit");
        command.setTabCompleter(this);
        command.setExecutor(this);
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String d, @Nonnull String... args) {
        if (!(sender instanceof Player)) {
            MessageUtils.message(sender, "This command may only be executed by players.");
            return true;
        }

        Player user = (Player) sender;

        if (!user.hasPermission("revelation.kits.delete")) {
            MessageUtils.message(user, this.plugin.getConfig(RevelationKits.ConfigType.LANG).getString("error.no-permission"));
            return true;
        }

        if (args.length == 0) {
            MessageUtils.message(user, this.plugin.getConfig(RevelationKits.ConfigType.LANG).getString("error.invalid-arguments").replace("%command%", String.format("%s <name>", cmd.getName())));
            return true;
        }

        String name = args[0].toLowerCase();
        File kitFile = new File(this.plugin.getDataFolder(), String.format("kits/%s.yml", name));

        if (!FileUtils.exists(kitFile)) {
            MessageUtils.message(user, this.plugin.getConfig(RevelationKits.ConfigType.LANG).getString("delete.does-not-exist").replace("%name%", name));
            return true;
        }

        if (!FileUtils.delete(kitFile)) {
            MessageUtils.message(user, this.plugin.getConfig(RevelationKits.ConfigType.LANG).getString("delete.file-error").replace("%name%", name));
            return true;
        }

        this.plugin.getKitManager().remove(name);
        MessageUtils.message(user, this.plugin.getConfig(RevelationKits.ConfigType.LANG).getString("delete.success").replace("%name%", name));
        return true;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String d, @Nonnull String... args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        Player user = (Player) sender;

        List<String> kits = this.plugin.getKitManager().getAvailableKits(user);

        if (args.length == 0) {
            return kits;
        }

        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();

            for (String kit : kits) {
                if (args[0].toLowerCase().startsWith(kit)) {
                    suggestions.add(kit);
                }
            }

            return suggestions;
        }

        return null;
    }
}
