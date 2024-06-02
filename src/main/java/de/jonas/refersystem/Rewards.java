package de.jonas.refersystem;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Rewards {

    ReferSystem rs = ReferSystem.INSTACE;

    public void reward1(Player p) {
        ItemStack itemStack = new ItemStack(Material.ACACIA_BUTTON);
        p.getInventory().addItem(itemStack);
    }

    public void reward2(Player p) {
        ItemStack itemStack = new ItemStack(Material.SPORE_BLOSSOM);
        p.getInventory().addItem(itemStack);
    }

    public void reward3(Player p) {
        ItemStack itemStack = new ItemStack(Material.COAL);
        p.getInventory().addItem(itemStack);
    }

}
