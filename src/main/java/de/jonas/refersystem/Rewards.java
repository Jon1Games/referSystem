package de.jonas.refersystem;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;

public class Rewards {

    public static NamespacedKey firstReward = new NamespacedKey("refersystem","first_reward");
    public static NamespacedKey secondReward = new NamespacedKey("refersystem","second_reward");
    public static NamespacedKey thirdReward = new NamespacedKey("refersystem","third_reward");

    public static void reward1(Player p) {
        ItemStack itemStack = new ItemStack(Material.DIAMOND);
        itemStack.setAmount(4);
        p.getInventory().addItem(itemStack);
        String coin_command = "eco add " + p.getName()+" 200";
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), coin_command);
    }

    public static void reward2(Player p) {
        ItemStack itemStack = new ItemStack(Material.NETHERITE_INGOT);
        p.getInventory().addItem(itemStack);
        String fragment_command = "keyshop:admin add " + p.getName()+" 4";
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), fragment_command);
    }

    public static void reward3(Player p) {
        ItemStack itemStack = new ItemStack(Material.EMERALD_BLOCK);
        itemStack.setAmount(8);
        PlayerData pd = GriefPrevention.instance.dataStore.getPlayerData(p.getUniqueId());
        int remaining = pd.getRemainingClaimBlocks();
        pd.setAccruedClaimBlocks(remaining += 10000);
        GriefPrevention.instance.dataStore.savePlayerData(p.getUniqueId(), pd);
        p.getInventory().addItem(itemStack);
    }

}
