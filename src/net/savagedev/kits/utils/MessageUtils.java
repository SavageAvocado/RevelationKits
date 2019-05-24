package net.savagedev.kits.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class MessageUtils {
    /**
     * Sends a specified message to a {@link org.bukkit.command.CommandSender CommandSender}
     *
     * @param user    The {@link org.bukkit.command.CommandSender CommandSender} whom you want to send the message to.
     * @param message The {@link java.lang.String String} message you want to send.
     */
    public static void message(CommandSender user, String message) {
        if (message == null || message.equals("none")) {
            return;
        }

        user.sendMessage(color(message));
    }

    /**
     * Not in use at the moment, just kind of exists for no reason. (Potentially part of a future update that adds GUIs?)
     * Translates {@link org.bukkit.ChatColor ChatColor} for {@link java.util.List<java.lang.String> List<String>}
     *
     * @param messages A {@link java.util.List<java.lang.String> List<String>} of messages to be "colored".
     * @return {@link java.util.List<java.lang.String> List<String>} of colored messages.
     */
    public static List<String> color(List<String> messages) {
        List<String> colored = new ArrayList<>();

        for (String message : messages) {
            colored.add(color(message));
        }

        return colored;
    }

    /**
     * Converts a {@link java.util.List<java.lang.String> List<String>} into a {@link java.lang.String String}
     *
     * @param messages  A {@link java.util.List<java.lang.String> List<String>} to be converted.
     * @param separator The {@link java.lang.String String} that will separate each element of the {@link java.util.List<java.lang.String> List<String>}
     * @return {@link java.lang.String}
     */
    public static String listToString(List<String> messages, String separator) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < messages.size(); i++) {
            if (!listHasNext(messages, i)) {
                stringBuilder.append(messages.get(i));
                return stringBuilder.toString().trim();
            }

            stringBuilder.append(messages.get(i)).append(separator);
        }

        return stringBuilder.toString().trim();
    }

    /**
     * Returns whether a {@link java.util.List<java.lang.String> List<String>} has a next element from a specified index.
     *
     * @param list         The {@link java.util.List<java.lang.String> List<String>} to be checked.
     * @param currentIndex The {@link int} index to start at.
     * @return {@link boolean}
     */
    private static boolean listHasNext(List<String> list, int currentIndex) {
        try {
            list.get(currentIndex + 1);
            return true;
        } catch (IndexOutOfBoundsException ignored) {
            return false;
        }
    }

    /**
     * "Colors" an inputted {@link java.lang.String String}
     *
     * @param message The {@link java.lang.String String} message to be "colored."
     * @return The "colored" {@link java.lang.String String}
     */
    private static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
