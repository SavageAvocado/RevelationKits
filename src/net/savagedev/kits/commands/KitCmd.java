package net.savagedev.kits.commands;

import net.savagedev.kits.RevelationKits;
import net.savagedev.kits.kit.KitManager;
import net.savagedev.kits.user.User;
import net.savagedev.kits.utils.MessageUtils;
import net.savagedev.kits.utils.TimeUtils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("Duplicates")
public class KitCmd implements CommandExecutor, TabCompleter {
    private final RevelationKits plugin;

    public KitCmd(RevelationKits plugin) {
        this.plugin = plugin;
        this.init();
    }

    private void init() {
        PluginCommand command = this.plugin.getCommand("kit");
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

        if (!user.hasPermission("revelation.kits.use")) {
            MessageUtils.message(user, this.plugin.getConfig(RevelationKits.ConfigType.LANG).getString("error.no-permission"));
            return true;
        }

        if (args.length == 0) {
            List<String> kits = this.plugin.getKitManager().getAvailableKits(user);
            MessageUtils.message(user, this.plugin.getConfig(RevelationKits.ConfigType.LANG).getString("kit.list").replace("%kits%", kits.isEmpty() ? "None." : MessageUtils.listToString(kits, this.plugin.getConfig(RevelationKits.ConfigType.LANG).getString("kit.list-separator"))));
            return true;
        }

        String name = args[0].toLowerCase();

        if (!user.hasPermission(String.format("revelation.kits.use.%s", name))) {
            MessageUtils.message(user, this.plugin.getConfig(RevelationKits.ConfigType.LANG).getString("error.no-permission"));
            return true;
        }

        if (!this.plugin.getKitManager().exists(name)) {
            MessageUtils.message(user, this.plugin.getConfig(RevelationKits.ConfigType.LANG).getString("kit.does-not-exist").replace("%name%", name));
            return true;
        }

        KitManager.GiveResult result = this.plugin.getKitManager().give(user, name);
        User kitUser = this.plugin.getUserManager().get(user.getUniqueId());

        switch (result) {
            case ON_COOL_DOWN:
                MessageUtils.message(user, this.plugin.getConfig(RevelationKits.ConfigType.LANG).getString("kit.on-cooldown").replace("%name%", name).replace("%cooldown%", TimeUtils.formatTime(kitUser.getRemainingCooldown(this.plugin.getKitManager().get(name)), TimeUtils.TimeLengthFormat.valueOf(Objects.requireNonNull(this.plugin.getConfig(RevelationKits.ConfigType.SETTINGS).getString("delay-time-format")).toUpperCase()))));
                break;
            case DROPPED:
                MessageUtils.message(user, this.plugin.getConfig(RevelationKits.ConfigType.LANG).getString("kit.dropped").replace("%name%", name));
                break;
            case SUCCESS:
                MessageUtils.message(user, this.plugin.getConfig(RevelationKits.ConfigType.LANG).getString("kit.success").replace("%name%", name));
                break;
        }

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
