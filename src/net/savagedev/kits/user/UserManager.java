package net.savagedev.kits.user;

import net.savagedev.kits.RevelationKits;
import net.savagedev.kits.utils.io.FileUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class UserManager {
    private final Map<UUID, User> users;
    private final RevelationKits plugin;

    /**
     * Creates and initializes a UserManager instance.
     *
     * @param plugin {@link net.savagedev.kits.RevelationKits RevelationKits} A reference the the main plugin instance.
     * @see #init()
     */
    public UserManager(RevelationKits plugin) {
        this.users = new HashMap<>();
        this.plugin = plugin;
        this.init();
    }

    /**
     * Loads all data for online players into memory.
     *
     * @see #cache(java.util.UUID)
     */
    private void init() {
        if (!this.plugin.getServer().getOnlinePlayers().isEmpty()) {
            for (Player user : this.plugin.getServer().getOnlinePlayers()) {
                this.cache(user.getUniqueId());
            }
        }
    }

    /**
     * Clears all userdata currently in memory, and reload it.
     *
     * @see #init()
     */
    public void reload() {
        this.users.clear();
        this.init();
    }

    /**
     * Saves a user's data to a file.
     *
     * @param uuid The {@link java.util.UUID UUID} of the user to save.
     */
    public void save(UUID uuid) {
        Map<String, Long> cooldowns = this.get(uuid).getCooldowns();

        if (cooldowns.isEmpty()) {
            return;
        }

        File file = new File(this.plugin.getDataFolder(), String.format("storage/%s.yml", uuid.toString()));
        FileConfiguration storageFile = FileUtils.load(file);

        if (storageFile == null) {
            this.plugin.getServer().getLogger().log(Level.WARNING, String.format("[RevelationKits] An error occurred saving file of \"%s.\" File was null.", uuid.toString()));
            return;
        }

        for (String kit_name : cooldowns.keySet()) {
            storageFile.set(String.format("cooldowns.%s", kit_name), cooldowns.get(kit_name));
        }

        FileUtils.save(storageFile, file);
    }

    /**
     * Loads a user's data into memory.
     *
     * @param uuid The {@link java.util.UUID UUID} of the user to be cached.
     */
    public void cache(UUID uuid) {
        Map<String, Long> cooldowns = new HashMap<>();

        File file = new File(this.plugin.getDataFolder(), String.format("storage/%s.yml", uuid.toString()));
        FileConfiguration storageFile = FileUtils.load(file);

        if (storageFile == null) {
            this.plugin.getServer().getLogger().log(Level.WARNING, String.format("[RevelationKits] An error occurred loading file of \"%s.\" File was null.", uuid.toString()));
            return;
        }

        ConfigurationSection cooldownSection = storageFile.getConfigurationSection("cooldowns");

        if (cooldownSection == null) {
            this.users.putIfAbsent(uuid, new User(cooldowns));
            return;
        }

        for (String kit_name : cooldownSection.getKeys(false)) {
            cooldowns.putIfAbsent(kit_name, storageFile.getLong(String.format("cooldowns.%s", kit_name)));
        }

        this.users.putIfAbsent(uuid, new User(cooldowns));
    }

    /**
     * Unloads a user's data from memory.
     *
     * @param uuid The {@link java.util.UUID UUID} of the user to be un-cached.
     */
    public void unCache(UUID uuid) {
        this.users.remove(uuid);
    }

    /**
     * Returns a {@link net.savagedev.kits.user.User User} that is currently cached.
     *
     * @param uuid The {@link java.util.UUID UUID} of the user to return.
     * @return {@link net.savagedev.kits.user.User User}
     */
    public User get(UUID uuid) {
        return this.users.get(uuid);
    }
}
