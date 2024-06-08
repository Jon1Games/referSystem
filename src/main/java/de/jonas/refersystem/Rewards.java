package de.jonas.refersystem;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Rewards {

    public static NamespacedKey firstReward = new NamespacedKey("refersystem","first_reward");
    public static NamespacedKey secondReward = new NamespacedKey("refersystem","second_reward");
    public static NamespacedKey thirdReward = new NamespacedKey("refersystem","third_reward");

    public static void reward1(Player p) {
        ItemStack itemStack = new ItemStack(Material.ACACIA_BUTTON);
        p.getInventory().addItem(itemStack);
    }

    public static void reward2(Player p) {
        ItemStack itemStack = new ItemStack(Material.SPORE_BLOSSOM);
        p.getInventory().addItem(itemStack);
    }

    public static void reward3(Player p) {
        ItemStack itemStack = new ItemStack(Material.COAL);
        p.getInventory().addItem(itemStack);
    }

}
