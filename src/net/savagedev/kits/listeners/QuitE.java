package net.savagedev.kits.listeners;

import net.savagedev.kits.RevelationKits;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class QuitE implements Listener {
    private final RevelationKits plugin;

    public QuitE(RevelationKits plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuitE(final PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();

        this.plugin.getUserManager().save(uuid);
        this.plugin.getUserManager().unCache(uuid);
    }
}
