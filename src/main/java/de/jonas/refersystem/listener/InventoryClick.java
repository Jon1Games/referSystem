package de.jonas.refersystem.listener;

import de.jonas.refersystem.DataBasePool;
import de.jonas.refersystem.ReferSystem;
import de.jonas.refersystem.Rewards;
import de.jonas.refersystem.gui.MainGUI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InventoryClick implements Listener {

    public static final NamespacedKey isInventoryClickEvent = new NamespacedKey("keyshop", "is_inventory_click_event");

    MiniMessage mm = MiniMessage.miniMessage();
    ReferSystem rs = ReferSystem.INSTACE;
    FileConfiguration conf = rs.getConfig();
    DataBasePool db = rs.dbPool;
    Rewards rewards = new Rewards();

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) return;
        if (!e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(isInventoryClickEvent,
                PersistentDataType.INTEGER)) return;
        switch (e.getCurrentItem().getItemMeta().getPersistentDataContainer().
                get(isInventoryClickEvent, PersistentDataType.INTEGER)) {
            default:
                break;
            case 0:
                e.setCancelled(true);
                break;
            case 1:
                e.setCancelled(true);
                e.getWhoClicked().closeInventory();
                break;
            case 2:
                e.setCancelled(true);
                e.getWhoClicked().sendMessage(mm.deserialize(conf.getString("Messages.HowSetInviter")));
                break;
            case 3:
                e.setCancelled(true);
                UUID uuid3 = getTableFromDBRewards(db, e.getWhoClicked().getUniqueId());
                OfflinePlayer p3 = Bukkit.getOfflinePlayer(uuid3);
                if (p3.isOnline()) {
                    rewards.reward2(p3.getPlayer());
                } else {
                    setTableFromDBRewards(db, uuid3, 2);
                }
                rewards.reward2((Player) e.getWhoClicked());
                e.getWhoClicked().getPersistentDataContainer().set(MainGUI.secondReward, PersistentDataType.BOOLEAN, true);
                e.getWhoClicked().closeInventory();
                e.getWhoClicked().openInventory(new MainGUI((Player) e.getWhoClicked()).getInventory());
                break;
            case 4:
                e.setCancelled(true);
                UUID uuid4 = getTableFromDBRewards(db, e.getWhoClicked().getUniqueId());
                OfflinePlayer p4 = Bukkit.getOfflinePlayer(uuid4);
                if (p4.isOnline()) {
                    rewards.reward3(p4.getPlayer());
                } else {
                    setTableFromDBRewards(db, uuid4, 3);
                }
                rewards.reward3((Player) e.getWhoClicked());
                e.getWhoClicked().getPersistentDataContainer().set(MainGUI.thirdReward, PersistentDataType.BOOLEAN, true);
                e.getWhoClicked().closeInventory();
                e.getWhoClicked().openInventory(new MainGUI((Player) e.getWhoClicked()).getInventory());
                break;
        }
    }

    public static void setTableFromDBRewards(DataBasePool pool, UUID uuid, int reward) {
        String querry = "INSERT INTO `rewards` (`uuid`, `reward`) VALUES (?, ?);";

        try {
            Connection con = pool.getConnection();
            PreparedStatement sel = con.prepareStatement(querry);
            sel.setObject(1, uuid);
            sel.setObject(2, reward);
            sel.executeUpdate();
            sel.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static UUID getTableFromDBRewards(DataBasePool pool, UUID uuid) {
        String querry = "SELECT `uuids`.`uuid` FROM `uuids` WHERE `uuids`.`InvitedUUID` = ?;";

        try {
            Connection con = pool.getConnection();
            PreparedStatement sel = con.prepareStatement(querry);
            sel.setObject(1, uuid);
            ResultSet res = sel.executeQuery();
            res.first();
            UUID inv = (UUID) res.getObject("uuid");
            sel.close();
            con.close();
            return inv;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
