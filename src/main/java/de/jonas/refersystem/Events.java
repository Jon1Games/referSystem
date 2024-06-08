package de.jonas.refersystem;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataType;
import java.util.*;
import de.jonas.refersystem.gui.MainGUI;
import de.jonas.stuff.Stuff;
import de.jonas.stuff.interfaced.ClickEvent;
import de.jonas.stuff.utility.UseNextChatInput;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class Events {
 
    private static final ClickEvent close = Events::closeI;
    private static final ClickEvent cancel = Events::cancelI;
    private static final ClickEvent first = Events::firstI;
    private static final ClickEvent seacond = Events::seacondI;
    private static final ClickEvent third = Events::thirdI;
    
    public void onEnable() {
        Stuff.INSTANCE.itemBuilderManager.addClickEvent(close, "referSystem:close_inv");
        Stuff.INSTANCE.itemBuilderManager.addClickEvent(cancel, "referSystem:cancel_event");
        Stuff.INSTANCE.itemBuilderManager.addClickEvent(first, "referSystem:first_reward");
        Stuff.INSTANCE.itemBuilderManager.addClickEvent(seacond, "referSystem:seacond_reward");
        Stuff.INSTANCE.itemBuilderManager.addClickEvent(third, "referSystem:third_reward");
    }

    private static void cancelI(InventoryClickEvent e) {
		e.setCancelled(true);
        e.getWhoClicked().closeInventory();
	}

    private static void closeI(InventoryClickEvent e) {
		e.setCancelled(true);
        e.getWhoClicked().closeInventory();
	}

    private static void firstI(InventoryClickEvent e) {
        FileConfiguration conf = ReferSystem.INSTACE.getConfig();
        MiniMessage mm = MiniMessage.miniMessage();
        DataBasePool db = ReferSystem.INSTACE.dbPool;
        e.getWhoClicked().closeInventory();
        new UseNextChatInput((Player) e.getWhoClicked())
        .sendMessage("Schreibe den Namen des Spielers der dich eingeladen hat in den Chat.<br>Nutze \"exit\" um abzubrechen.")
        .setChatEvent((sender, message) -> {
            OfflinePlayer player = Bukkit.getOfflinePlayer(message);

            if (message.equalsIgnoreCase("exit")) {
                sender.sendMessage("Vorgang abgebrochen");
                return;
            }

            if (sender == player) {
                sender.sendMessage(mm.deserialize(conf.getString("Messages.SamePlayerError")));
                return;
            }

            if (player.isOnline()) {
                Player p = player.getPlayer();
                Rewards.reward1(p);
            } else {
                DataBasePool.setTableFromDBRewards(db, player.getUniqueId());
                DataBasePool.setTableFromDBUUIDS(db, player.getUniqueId(), sender.getUniqueId());
            }

            Rewards.reward1(sender);
            sender.sendMessage(mm.deserialize(conf.getString("Messages.SetInviter")));
            sender.getPersistentDataContainer().set(Rewards.firstReward, PersistentDataType.BOOLEAN, true);

        })
        .capture();
    }

    private static void seacondI(InventoryClickEvent e) {
        DataBasePool db = ReferSystem.INSTACE.dbPool;
        e.setCancelled(true);
        UUID uuid = DataBasePool.getTableFromDBRewards(db, e.getWhoClicked().getUniqueId());
        OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
        if (p.isOnline()) {
            Rewards.reward2(p.getPlayer());
        } else {
            DataBasePool.setTableFromDBRewards(db, uuid, 2);
        }
        Rewards.reward2((Player) e.getWhoClicked());
        e.getWhoClicked().getPersistentDataContainer().set(Rewards.secondReward, PersistentDataType.BOOLEAN, true);
        e.getWhoClicked().closeInventory();
        e.getWhoClicked().openInventory(new MainGUI((Player) e.getWhoClicked()).getInventory());
    }

    private static void thirdI(InventoryClickEvent e) {
        DataBasePool db = ReferSystem.INSTACE.dbPool;
        e.setCancelled(true);
        UUID uuid = DataBasePool.getTableFromDBRewards(db, e.getWhoClicked().getUniqueId());
        OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
        if (p.isOnline()) {
            Rewards.reward3(p.getPlayer());
        } else {
            DataBasePool.setTableFromDBRewards(db, uuid, 3);
        }
        Rewards.reward3((Player) e.getWhoClicked());
        e.getWhoClicked().getPersistentDataContainer().set(Rewards.thirdReward, PersistentDataType.BOOLEAN, true);
        e.getWhoClicked().closeInventory();
        e.getWhoClicked().openInventory(new MainGUI((Player) e.getWhoClicked()).getInventory());
    }

}
