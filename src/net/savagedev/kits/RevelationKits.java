package net.savagedev.kits;

import net.savagedev.kits.commands.CreateKitCmd;
import net.savagedev.kits.commands.DeleteKitCmd;
import net.savagedev.kits.commands.KitCmd;
import net.savagedev.kits.commands.ReloadKitCmd;
import net.savagedev.kits.kit.KitManager;
import net.savagedev.kits.listeners.JoinE;
import net.savagedev.kits.listeners.QuitE;
import net.savagedev.kits.user.UserManager;
import net.savagedev.kits.utils.io.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public class RevelationKits extends JavaPlugin {
    private FileConfiguration langFile;
    private UserManager userManager;
    private KitManager kitManager;

    /**
     * Enables the plugin.
     *
     * @see #loadConfig()
     * @see #loadUtils()
     * @see #loadCommands()
     * @see #loadListeners()
     */
    @Override
    public void onEnable() {
        this.loadConfig();
        this.loadUtils();
        this.loadCommands();
        this.loadListeners();
    }

    /**
     * Safely reloads the plugin.
     *
     * @see UserManager#reload()
     * @see KitManager#reload()
     * @see #reloadConfig()
     * @see #loadConfig()
     */
    public void reload() {
        this.userManager.reload();
        this.kitManager.reload();
        this.reloadConfig();
        this.loadConfig();
    }

    /**
     * Loads utility classes.
     *
     * @see net.savagedev.kits.user.UserManager
     * @see net.savagedev.kits.kit.KitManager
     */
    private void loadUtils() {
        this.userManager = new UserManager(this);
        this.kitManager = new KitManager(this);
    }

    /**
     * Loads the {@link org.bukkit.configuration.file.FileConfiguration FileConfiguration}
     *
     * @see resources
     */
    private void loadConfig() {
        FileUtils.create("default.yml", new File(this.getDataFolder(), "kits/default.yml"));

        File langFile = new File(this.getDataFolder(), "lang.yml");
        FileUtils.create("lang.yml", langFile);
        this.langFile = FileUtils.load(langFile);

        this.saveDefaultConfig();
    }

    /**
     * Loads the {@link org.bukkit.command.CommandExecutor CommandExecutor}
     *
     * @see net.savagedev.kits.commands.ReloadKitCmd
     * @see net.savagedev.kits.commands.CreateKitCmd
     * @see net.savagedev.kits.commands.DeleteKitCmd
     * @see net.savagedev.kits.commands.KitCmd
     */
    private void loadCommands() {
        Objects.requireNonNull(this.getCommand("reloadkit")).setExecutor(new ReloadKitCmd(this));
        Objects.requireNonNull(this.getCommand("createkit")).setExecutor(new CreateKitCmd(this));
        new DeleteKitCmd(this);
        new KitCmd(this);
    }

    /**
     * Loads the {@link org.bukkit.event.Listener Listener}
     *
     * @see net.savagedev.kits.listeners.JoinE
     * @see net.savagedev.kits.listeners.QuitE
     */
    private void loadListeners() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new JoinE(this), this);
        pluginManager.registerEvents(new QuitE(this), this);
    }

    /**
     * Returns a {@link org.bukkit.configuration.file.FileConfiguration FileConfiguration} based on the
     * {@link net.savagedev.kits.RevelationKits.ConfigType ConfigType} specified.
     *
     * @param type The {@link net.savagedev.kits.RevelationKits.ConfigType ConfigType} required.
     * @return {@link org.bukkit.configuration.file.FileConfiguration FileConfiguration}
     */
    public FileConfiguration getConfig(ConfigType type) {
        if (type == ConfigType.LANG) {
            return this.langFile;
        }

        return this.getConfig();
    }

    /**
     * Returns the {@link net.savagedev.kits.user.UserManager UserManager} instance.
     *
     * @return {@link net.savagedev.kits.user.UserManager UserManager}
     */
    public UserManager getUserManager() {
        return this.userManager;
    }

    /**
     * Returns the {@link net.savagedev.kits.kit.KitManager KitManager} instance.
     *
     * @return {@link net.savagedev.kits.kit.KitManager KitManager}
     */
    public KitManager getKitManager() {
        return this.kitManager;
    }

    /**
     * Represents the type of configuration.
     */
    public enum ConfigType {
        SETTINGS, LANG
    }
}
