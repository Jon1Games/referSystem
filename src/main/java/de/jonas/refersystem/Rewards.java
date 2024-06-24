package de.jonas.refersystem;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class Rewards {

    public static NamespacedKey firstReward = new NamespacedKey("refersystem","first_reward");
    public static NamespacedKey secondReward = new NamespacedKey("refersystem","second_reward");
    public static NamespacedKey thirdReward = new NamespacedKey("refersystem","third_reward");

    public static void reward1(Player p) {
        MiniMessage mm = MiniMessage.miniMessage();
        ItemStack itemStack = new ItemStack(Material.DIAMOND);
        itemStack.setAmount(6);
        p.getInventory().addItem(itemStack);
        p.sendMessage(mm.deserialize("Du hast <aqua>4 Diamanten</aqua> erhalten."));
    }

    public static void reward2(Player p) {
        MiniMessage mm = MiniMessage.miniMessage();
        ItemStack itemStack = new ItemStack(Material.NETHERITE_INGOT);
        p.getInventory().addItem(itemStack);
        String fragment_command = "keyshop:admin add " + p.getName()+" 4";
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), fragment_command);
        p.sendMessage(mm.deserialize("Du hast <black>1 Netherite</black> und <light_purple>4 Schlüsselfragmente</light_purple> erhalten."));
    }

    public static void reward3(Player p) {
        MiniMessage mm = MiniMessage.miniMessage();
        ItemStack itemStack = new ItemStack(Material.EMERALD_BLOCK);
        itemStack.setAmount(8);
        PlayerData pd = GriefPrevention.instance.dataStore.getPlayerData(p.getUniqueId());
        int remaining = pd.getRemainingClaimBlocks();
        pd.setAccruedClaimBlocks(remaining += 10000);
        GriefPrevention.instance.dataStore.savePlayerData(p.getUniqueId(), pd);
        p.sendMessage(mm.deserialize("Du hast <green>10.000 Claimblöcke</green> und <green>8 Emerald Blöcke</green> erhalten."));
        p.getInventory().addItem(itemStack);
    }

}
