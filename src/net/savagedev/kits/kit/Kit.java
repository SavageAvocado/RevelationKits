package net.savagedev.kits.kit;

import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class Kit {
    private final Map<Integer, ItemStack> items;
    private final String name;
    private final long delay;

    /**
     * Creates a Kit.
     *
     * @param name  The {@link java.lang.String String} name of the Kit.
     * @param items The {@link java.util.Map<java.lang.Integer, org.bukkit.inventory.ItemStack> Map<Integer, ItemStack>} items the Kit will contain.
     * @param delay The {@link long} delay of the Kit.
     */
    Kit(String name, Map<Integer, ItemStack> items, long delay) {
        this.items = items;
        this.delay = delay;
        this.name = name;
    }

    /**
     * Returns a {@link java.util.Map<> Map<>} of {@link org.bukkit.inventory.ItemStack ItemStack} to respective {@link java.lang.Integer Integer} slots.
     *
     * @return {@link java.util.Map<java.lang.Integer, org.bukkit.inventory.ItemStack> Map<Integer, ItemStack>}
     */
    Map<Integer, ItemStack> getItems() {
        return this.items;
    }

    /**
     * Returns the {@link long} delay of the Kit.
     *
     * @return {@link long}
     */
    public long getDelay() {
        return this.delay;
    }

    /**
     * Returns ths {@link java.lang.String String} name of the Kit.
     *
     * @return {@link java.lang.String String}
     */
    public String getName() {
        return this.name;
    }
}
