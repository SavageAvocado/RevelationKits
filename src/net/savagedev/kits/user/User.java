package net.savagedev.kits.user;

import net.savagedev.kits.kit.Kit;

import java.util.Map;

public class User {
    private final Map<String, Long> cooldowns;

    /**
     * Creates a User.
     *
     * @param cooldowns The {@link java.util.Map<java.lang.String, java.lang.Long> Map<String, Long>} cooldown map of the User.
     */
    User(Map<String, Long> cooldowns) {
        this.cooldowns = cooldowns;
    }

    /**
     * Not in use. Might become useful later. (Maybe in a future update that allows you to give a User access to a Kit w/o having to use the cooldown bypass perm.)
     * Removes the {@link net.savagedev.kits.kit.Kit Kit} from the cooldown Map.
     *
     * @param kit The {@link net.savagedev.kits.kit.Kit Kit} to be removed.
     */
    public void removeKitCooldown(Kit kit) {
        this.cooldowns.remove(kit.getName());
    }

    /**
     * Sets a Kit on cooldown.
     *
     * @param kit The {@link net.savagedev.kits.kit.Kit Kit} to cooldown.
     */
    public void addKitCooldown(Kit kit) {
        if (this.cooldowns.containsKey(kit.getName())) {
            this.cooldowns.replace(kit.getName(), System.currentTimeMillis());
            return;
        }

        this.cooldowns.putIfAbsent(kit.getName(), System.currentTimeMillis());
    }

    /**
     * Returns whether a {@link net.savagedev.kits.kit.Kit Kit} is currently on cooldown for the User or not.
     *
     * @param kit The {@link net.savagedev.kits.kit.Kit Kit} to be checked.
     * @return {@link boolean}
     */
    public boolean isOnCooldown(Kit kit) {
        if (!this.cooldowns.containsKey(kit.getName())) {
            return false;
        }

        return (System.currentTimeMillis() - this.cooldowns.get(kit.getName())) < (kit.getDelay() * 60000L);
    }

    /**
     * Returns the remaining time {@link long} on a {@link net.savagedev.kits.kit.Kit Kit} cooldown.
     *
     * @param kit The {@link net.savagedev.kits.kit.Kit Kit} to get the cooldown for.
     * @return {@link long}
     */
    public long getRemainingCooldown(Kit kit) {
        if (!this.cooldowns.containsKey(kit.getName())) {
            return 0L;
        }

        return -(System.currentTimeMillis() - (this.cooldowns.get(kit.getName()) + (kit.getDelay() * 60000L)));
    }

    /**
     * Returns the User's cooldown Map.
     *
     * @return {@link java.util.Map<java.lang.String, java.lang.Long> Map<String, Long>}
     */
    Map<String, Long> getCooldowns() {
        return this.cooldowns;
    }
}
