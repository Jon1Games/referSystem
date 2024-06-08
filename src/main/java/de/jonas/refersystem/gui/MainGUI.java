package de.jonas.refersystem.gui;

import de.jonas.stuff.utility.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import de.jonas.refersystem.Rewards;
import de.jonas.refersystem.gui.MainGUI;

public class MainGUI implements InventoryHolder {

    private Inventory mainGUI;

    public MainGUI(Player p) {
        if (p == null) return;
        this.mainGUI = Bukkit.createInventory(this, (3 * 9), Component.text("Nexus"));

        int[] placeholder = {0,1,2,3,4,5,6,7,8,9,10,12,14,16,17,18,19,20,21,23,24,25,26};
        for (int a : placeholder) {
            mainGUI.setItem(a, 
                new ItemBuilder()
                    .setMaterial(Material.GRAY_STAINED_GLASS_PANE)
                    .setName("")
                    .whenClicked("referSystem:cancel_event")
                    .build());
        }

        PersistentDataContainer container = p.getPersistentDataContainer();
        if (container.has(Rewards.firstReward)) {
            mainGUI.setItem(11, 
            new ItemBuilder()
                .setMaterial(Material.RED_WOOL)
                .setGlint(false)
                .addLoreLine("<red>Du hast bereits einen Spieler angegeben der dich eingeladen hat!</red>")
                .setName("<red>Bereits eingegeben</red>")
                .whenClicked("referSystem:cancel_event")
                .build());
        } else {
            mainGUI.setItem(11, 
            new ItemBuilder()
                .setMaterial(Material.GREEN_WOOL)
                .setGlint(true)
                .addLoreLine("<green>Gib an wer dich eingeladen hat um Belohnungen zu erhalten.</green>")
                .setName("<green>Eingeladen?</green>")
                .whenClicked("referSystem:first_reward")
                .build());
        }

        if (container.has(Rewards.secondReward)) {
            mainGUI.setItem(13, 
            new ItemBuilder()
                .setMaterial(Material.RED_WOOL)
                .setGlint(false)
                .addLoreLine("<red>Du hast diese Belohnung bereits eingefordert!</red>")
                .setName("<red>Bereits eingefordert</red>")
                .whenClicked("referSystem:cancel_event")
                .build());
        } else if (container.has(Rewards.firstReward) && (p.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) > 14400) {
            mainGUI.setItem(13, 
            new ItemBuilder()
                .setMaterial(Material.GREEN_WOOL)
                .setGlint(true)
                .addLoreLine("<green>klicke um deine Belohnung zu erhalten.</green>")
                .setName("<green>Erledigt</green>")
                .whenClicked("referSystem:seacond_reward")
                .build());
        } else {
            mainGUI.setItem(13, 
            new ItemBuilder()
                .setMaterial(Material.RED_WOOL)
                .setGlint(false)
                .addLoreLine("<red>Spiele 4 Stunden um diese Belohnung zu erhalten!</red>")
                .setName("<red>Die ersten Stunden</red>")
                .whenClicked("referSystem:cancel_event")
                .build());
        }

        if (container.has(Rewards.thirdReward)){
            mainGUI.setItem(15, 
            new ItemBuilder()
                .setMaterial(Material.RED_WOOL)
                .setGlint(false)
                .addLoreLine("<red>Du hast diese Belohnung bereits eingefordert!</red>")
                .setName("<red>Bereits eingefordert</red>")
                .whenClicked("referSystem:cancel_event")
                .build());
        } else if (container.has(Rewards.secondReward) && (p.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) > 43200) {
            mainGUI.setItem(15, 
            new ItemBuilder()
                .setMaterial(Material.GREEN_WOOL)
                .setGlint(true)
                .addLoreLine("<green>klicke um deine Belohnung zu erhalten.</green>")
                .setName("<green>Erledigt</green>")
                .whenClicked("referSystem:third_reward")
                .build());
        } else {
            mainGUI.setItem(15,
            new ItemBuilder()
                .setMaterial(Material.RED_WOOL)
                .setGlint(false)
                .addLoreLine("<red>Spiele 12 Stunden um diese Belohnung zu erhalten!</red>")
                .setName("<red>Deine Reise beginnt Stunden</red>")
                .whenClicked("referSystem:cancel_event")
                .build());
        }

        mainGUI.setItem(22, 
            new ItemBuilder()
                .setMaterial(Material.BARRIER)
                .setName("<red>Schließen</red>")
                .whenClicked("referSystem:close_inv")
                .build());

    }

    @Override
    public @NotNull Inventory getInventory() {
        return mainGUI;
    }

}
