package net.savagedev.kits.utils.io;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class FileUtils {
    /**
     * Saves a {@link org.bukkit.configuration.file.FileConfiguration FileConfiguration} to a specified {@link java.io.File File}
     *
     * @param configuration The {@link org.bukkit.configuration.file.FileConfiguration FileConfiguration} to be saved.
     * @param file          The {@link java.io.File File} to save it to.
     */
    public static void save(FileConfiguration configuration, File file) {
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a copy of a resource at a specified {@link java.io.File File}
     * an returns if the operation was successful or not.
     *
     * @param name The {@link java.lang.String String} name of the resource to be copied.
     * @param file The {@link java.io.File File} location to save the copy at.
     * @return {@link boolean} whether the file was created or not.
     */
    public static boolean create(String name, File file) {
        if (!create(file)) {
            return false;
        }

        InputStream inputStream = getResource(name);
        if (inputStream == null) {
            Bukkit.getLogger().severe(String.format("Failed to save resource %s. InputStream was null.", name));
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)); FileWriter writer = new FileWriter(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line + "\n");
            }

            writer.flush();
        } catch (IOException e) {
            Bukkit.getLogger().severe(String.format("Failed to save resource %s. %s", name, e.getMessage()));
        }

        return true;
    }

    /**
     * Checks if a {@link java.io.File File} exists.
     *
     * @param file The {@link java.io.File File} to be checked.
     * @return {@link boolean} whether the {@link java.io.File File} exists or not.
     */
    public static boolean exists(File file) {
        return file.exists();
    }

    /**
     * Deletes a {@link java.io.File File}
     *
     * @param file The {@link java.io.File File} to be deleted.
     * @return {@link boolean} whether the file was deleted or not.
     */
    public static boolean delete(File file) {
        if (!exists(file)) {
            return false;
        }

        return file.delete();
    }

    /**
     * Creates a {@link java.io.File File}
     *
     * @param file The {@link java.io.File File} to be created.
     * @return {@link boolean} whether the file was created or not.
     */
    public static boolean create(File file) {
        if (exists(file)) {
            return false;
        }

        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Returns a {@link java.io.File[] File[]}
     *
     * @param path {@link java.io.File File} the directory to get the {@link java.io.File[] File[]} from.
     * @return {@link java.io.File[] File[]}
     */
    public static File[] allFiles(File path) {
        return path.listFiles();
    }

    /**
     * Creates & returns a new {@link org.bukkit.configuration.file.FileConfiguration FileConfiguration} from a specified {@link java.io.File File}
     *
     * @param file The {@link java.io.File File} to be loaded.
     * @return {@link org.bukkit.configuration.file.FileConfiguration FileConfiguration}
     */
    public static FileConfiguration load(File file) {
        try {
            FileConfiguration fileConfig = new YamlConfiguration();
            fileConfig.load(file);
            return fileConfig;
        } catch (InvalidConfigurationException | IOException e) {
            return null;
        }
    }

    /**
     * Returns an {@link java.io.InputStream InputStream} to a resource file.
     *
     * @param name The {@link java.lang.String String} name of the resource.
     * @return {@link java.io.InputStream InputStream}
     */
    private static InputStream getResource(String name) {
        URL resourceStream = FileUtils.class.getClassLoader().getResource(name);

        if (resourceStream == null) {
            return null;
        }

        try {
            URLConnection connection = resourceStream.openConnection();
            connection.setUseCaches(false);

            return connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
