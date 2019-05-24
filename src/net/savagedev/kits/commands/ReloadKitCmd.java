package net.savagedev.kits.commands;

import net.savagedev.kits.RevelationKits;
import net.savagedev.kits.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;

@SuppressWarnings("Duplicates")
public class ReloadKitCmd implements CommandExecutor {
    private final RevelationKits plugin;

    public ReloadKitCmd(RevelationKits plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender user, @Nonnull Command cmd, @Nonnull String d, @Nonnull String... args) {
        if (!user.hasPermission("revelation.kits.reload")) {
            MessageUtils.message(user, this.plugin.getConfig(RevelationKits.ConfigType.LANG).getString("error.no-permission"));
            return true;
        }

        this.plugin.reload();
        MessageUtils.message(user, this.plugin.getConfig(RevelationKits.ConfigType.LANG).getString("admin.reload"));
        return true;
    }
}
