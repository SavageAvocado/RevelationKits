package net.savagedev.kits.commands;

import net.savagedev.kits.RevelationKits;
import net.savagedev.kits.utils.MessageUtils;
import net.savagedev.kits.utils.io.FileUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.io.File;

@SuppressWarnings("Duplicates")
public class CreateKitCmd implements CommandExecutor {
    private final RevelationKits plugin;

    public CreateKitCmd(RevelationKits plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String d, @Nonnull String... args) {
        if (!(sender instanceof Player)) {
            MessageUtils.message(sender, "This command may only be executed by players.");
            return true;
        }

        Player user = (Player) sender;

        if (!user.hasPermission("revelation.kits.create")) {
            MessageUtils.message(user, this.plugin.getConfig(RevelationKits.ConfigType.LANG).getString("error.no-permission"));
            return true;
        }

        if (args.length == 0) {
            MessageUtils.message(user, this.plugin.getConfig(RevelationKits.ConfigType.LANG).getString("error.invalid-arguments").replace("%command%", String.format("%s <name> [delay (minutes)]", cmd.getName())));
            return true;
        }

        String name = args[0].toLowerCase();

        if (this.plugin.getKitManager().exists(name)) {
            MessageUtils.message(user, this.plugin.getConfig(RevelationKits.ConfigType.LANG).getString("create.already-exists").replace("%name%", name));
            return true;
        }

        if (!FileUtils.create(new File(this.plugin.getDataFolder(), String.format("kits/%s.yml", name)))) {
            MessageUtils.message(user, this.plugin.getConfig(RevelationKits.ConfigType.LANG).getString("create.file-error").replace("%name%", name));
            return true;
        }

        long delay = 0L;

        if (args.length == 2) {
            if (!this.isLong(args[1])) {
                MessageUtils.message(user, this.plugin.getConfig(RevelationKits.ConfigType.LANG).getString("create.delay-error"));
                return true;
            }

            delay = Long.valueOf(args[1]);
        }

        if (!this.plugin.getKitManager().create(name, user.getInventory(), delay)) {
            MessageUtils.message(user, this.plugin.getConfig(RevelationKits.ConfigType.LANG).getString("create.load-error").replace("%name%", name));
            return true;
        }

        MessageUtils.message(user, this.plugin.getConfig(RevelationKits.ConfigType.LANG).getString("create.success").replace("%name%", name));
        return true;
    }

    /**
     * Checks whether a given {@link java.lang.String String} is a {@link java.lang.Long Long}
     *
     * @param potentialLong The {@link java.lang.String String} to be checked.
     * @return {@link boolean}
     */
    private boolean isLong(String potentialLong) {
        try {
            Long.parseLong(potentialLong);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }
}
