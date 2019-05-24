package net.savagedev.kits.kit;

import net.savagedev.kits.RevelationKits;
import net.savagedev.kits.user.User;
import net.savagedev.kits.utils.io.FileUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class KitManager {
    private final Map<String, Kit> kits;
    private final RevelationKits plugin;

    /**
     * Creates and initializes a KitManager instance.
     *
     * @param plugin {@link net.savagedev.kits.RevelationKits RevelationKits} A reference the the main plugin instance.
     * @see #init()
     */
    public KitManager(RevelationKits plugin) {
        this.kits = new HashMap<>();
        this.plugin = plugin;
        this.init();
    }

    /**
     * Loads all existing kits into memory.
     *
     * @see #load(java.lang.String, org.bukkit.configuration.file.FileConfiguration)
     */
    private void init() {
        File[] files = FileUtils.allFiles(new File(this.plugin.getDataFolder(), "kits"));

        for (File file : files) {
            FileConfiguration kitFile = FileUtils.load(file);
            String fileName = file.getName();

            String name = fileName.substring(0, fileName.length() - 4);

            if (kitFile == null) {
                this.plugin.getServer().getLogger().log(Level.WARNING, String.format("[RevelationKits] Unable to load kit \"%s.\" File is null.", name));
                continue;
            }

            this.load(name, kitFile);
        }
    }

    /**
     * Clears all kits currently in memory, and reload them.
     *
     * @see #init()
     */
    public void reload() {
        this.kits.clear();
        this.init();
    }

    /**
     * Unloads a kit from memory.
     *
     * @param name The name of the {@link net.savagedev.kits.kit.Kit Kit} to unload.
     */
    public void remove(String name) {
        this.kits.remove(name);
    }

    /**
     * Loads an existing {@link net.savagedev.kits.kit.Kit Kit} into memory.
     *
     * @param name The name th refer to the {@link net.savagedev.kits.kit.Kit Kit} by.
     * @param file The file where the {@link net.savagedev.kits.kit.Kit Kit} is located.
     */
    private void load(String name, FileConfiguration file) {
        Map<Integer, ItemStack> items = new HashMap<>();

        ConfigurationSection contents = file.getConfigurationSection("contents");

        if (contents == null) {
            this.plugin.getServer().getLogger().log(Level.WARNING, String.format("[RevelationKits] An error occurred while loading kit \"%s.\" File contents were null.", name));
            return;
        }

        for (String slotStr : contents.getKeys(false)) {
            items.putIfAbsent(Integer.valueOf(slotStr), (ItemStack) file.get(String.format("contents.%s", slotStr)));
        }

        this.kits.putIfAbsent(name, new Kit(name, items, file.getLong("delay")));
    }

    /**
     * Creates a new {@link net.savagedev.kits.kit.Kit Kit} and loads it into memory.
     *
     * @param name      The name you would like the {@link net.savagedev.kits.kit.Kit Kit} to have.
     * @param inventory the {@link org.bukkit.inventory.Inventory Inventory} to model the {@link net.savagedev.kits.kit.Kit Kit} after.
     * @param delay     The delay in minutes you would like the {@link net.savagedev.kits.kit.Kit Kit} to have.
     * @return {@link boolean} whether the kit was successfully created or not.
     */
    public boolean create(String name, Inventory inventory, long delay) {
        File file = new File(this.plugin.getDataFolder(), String.format("kits/%s.yml", name));
        FileConfiguration kitFile = FileUtils.load(file);

        if (kitFile == null) {
            this.plugin.getServer().getLogger().log(Level.WARNING, String.format("[RevelationKits] An error occurred creating kit \"%s.\" File was null.", name));
            return false;
        }

        kitFile.set("delay", delay);

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);

            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            kitFile.set(String.format("contents.%s", String.valueOf(i)), item);
        }

        FileUtils.save(kitFile, file);
        this.load(name, kitFile);
        return true;
    }

    /**
     * Gives a {@link org.bukkit.entity.Player Player} a kit
     * and returns the {@link net.savagedev.kits.kit.KitManager.GiveResult GiveResult}
     * of the action.
     *
     * @param user The name of the {@link org.bukkit.entity.Player Player} who will receive the kit.
     * @param name The name of the {@link net.savagedev.kits.kit.Kit Kit} the player will receive.
     * @return {@link net.savagedev.kits.kit.KitManager.GiveResult GiveResult}
     */
    public GiveResult give(Player user, String name) {
        User kitUser = this.plugin.getUserManager().get(user.getUniqueId());
        Inventory inventory = user.getInventory();
        Kit kit = this.get(name);

        GiveResult success = GiveResult.SUCCESS;

        if (!user.hasPermission("revelation.kits.cooldown.bypass") && kitUser.isOnCooldown(kit)) {
            return GiveResult.ON_COOL_DOWN;
        }

        for (int slot : kit.getItems().keySet()) {
            ItemStack itemAtSlot = inventory.getItem(slot);
            ItemStack kitItem = kit.getItems().get(slot);

            if (itemAtSlot == null) {
                inventory.setItem(slot, kitItem);
                continue;
            }

            if (itemAtSlot.getType() == Material.AIR) {
                inventory.setItem(slot, kitItem);
                continue;
            }

            if (inventory.firstEmpty() > -1) {
                inventory.addItem(kitItem);
                continue;
            }

            Objects.requireNonNull(user.getLocation().getWorld()).dropItemNaturally(user.getLocation(), kitItem);
            success = GiveResult.DROPPED;
        }

        kitUser.addKitCooldown(kit);
        this.plugin.getUserManager().save(user.getUniqueId());
        return success;
    }

    /**
     * Returns whether or not a specified kit exists.
     *
     * @param name The name of the {@link net.savagedev.kits.kit.Kit Kit}
     * @return {@link boolean}
     */
    public boolean exists(String name) {
        return this.kits.containsKey(name);
    }

    /**
     * Returns a list of kits that the {@link org.bukkit.entity.Player Player}
     * has permission to use.
     *
     * @param user The target {@link org.bukkit.entity.Player Player}
     * @return {@link java.util.List<java.lang.String>}
     */
    public List<String> getAvailableKits(Player user) {
        List<String> kits = new ArrayList<>();

        for (String kit : this.kits.keySet()) {
            if (user.hasPermission(String.format("revelation.kits.use.%s", kit))) {
                kits.add(kit);
            }
        }

        return kits;
    }

    /**
     * Returns a {@link net.savagedev.kits.kit.Kit Kit}
     * with the name provided.
     *
     * @param name The name of the {@link net.savagedev.kits.kit.Kit Kit}
     * @return {@link net.savagedev.kits.kit.Kit Kit}
     */
    public Kit get(String name) {
        return this.kits.get(name);
    }

    /**
     * Represents the result of the give kit action.
     */
    public enum GiveResult {
        ON_COOL_DOWN,
        DROPPED,
        SUCCESS
    }
}
