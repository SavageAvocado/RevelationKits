package net.savagedev.kits.listeners;

import net.savagedev.kits.RevelationKits;
import net.savagedev.kits.utils.io.FileUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.util.UUID;

public class JoinE implements Listener {
    private final RevelationKits plugin;

    public JoinE(RevelationKits plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoinE(final PlayerJoinEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();

        FileUtils.create(new File(this.plugin.getDataFolder(), String.format("storage/%s.yml", uuid.toString())));
        this.plugin.getUserManager().cache(uuid);
    }
}
