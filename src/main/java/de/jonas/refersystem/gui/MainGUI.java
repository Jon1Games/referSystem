package de.jonas.refersystem.gui;

import de.jonas.refersystem.DataBasePool;
import de.jonas.refersystem.ReferSystem;
import de.jonas.stuff.interfaced.ClickEvent;
import de.jonas.stuff.utility.ItemBuilder;
import de.jonas.stuff.utility.UseNextChatInput;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import de.jonas.refersystem.Rewards;
import de.jonas.refersystem.gui.MainGUI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.persistence.PersistentDataType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MainGUI implements InventoryHolder {

    private Inventory mainGUI;
    MiniMessage mm = MiniMessage.miniMessage();
    ReferSystem rs = ReferSystem.INSTACE;
    FileConfiguration conf = rs.getConfig();
    DataBasePool db = rs.dbPool;
    Rewards rw = new Rewards();

    public static NamespacedKey firstReward = new NamespacedKey("refersystem","first_reward");
    public static NamespacedKey secondReward = new NamespacedKey("refersystem","second_reward");
    public static NamespacedKey thirdReward = new NamespacedKey("refersystem","third_reward");

    ClickEvent canceEvent = ev -> {
        ev.setCancelled(true);
    };

    ClickEvent first = ev -> {
        ev.getWhoClicked().closeInventory();
        new UseNextChatInput((Player) ev.getWhoClicked())
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
                rw.reward1(p);
            } else {
                setTableFromDBRewards(db, player.getUniqueId());
                setTableFromDBUUIDS(db, player.getUniqueId(), sender.getUniqueId());
            }

            rw.reward1(sender);
            sender.sendMessage(mm.deserialize(conf.getString("Messages.SetInviter")));
            sender.getPersistentDataContainer().set(MainGUI.firstReward, PersistentDataType.BOOLEAN, true);

        })
        .capture();
    };

    ClickEvent second = ev -> {
        ev.setCancelled(true);
        UUID uuid = getTableFromDBRewards(db, ev.getWhoClicked().getUniqueId());
        OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
        if (p.isOnline()) {
            rw.reward2(p.getPlayer());
        } else {
            setTableFromDBRewards(db, uuid, 2);
        }
        rw.reward2((Player) ev.getWhoClicked());
        ev.getWhoClicked().getPersistentDataContainer().set(MainGUI.secondReward, PersistentDataType.BOOLEAN, true);
        ev.getWhoClicked().closeInventory();
        ev.getWhoClicked().openInventory(new MainGUI((Player) ev.getWhoClicked()).getInventory());
    };

    ClickEvent third = ev -> {
        ev.setCancelled(true);
        UUID uuid = getTableFromDBRewards(db, ev.getWhoClicked().getUniqueId());
        OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
        if (p.isOnline()) {
            rw.reward3(p.getPlayer());
        } else {
            setTableFromDBRewards(db, uuid, 3);
        }
        rw.reward3((Player) ev.getWhoClicked());
        ev.getWhoClicked().getPersistentDataContainer().set(MainGUI.thirdReward, PersistentDataType.BOOLEAN, true);
        ev.getWhoClicked().closeInventory();
        ev.getWhoClicked().openInventory(new MainGUI((Player) ev.getWhoClicked()).getInventory());
    };

    ClickEvent closeInv = ev -> {
        ev.setCancelled(true);
        ev.getWhoClicked().closeInventory();
    };


    public MainGUI(Player p) {
        if (p == null) return;
        this.mainGUI = Bukkit.createInventory(this, (3 * 9), Component.text("Nexus"));

        int[] placeholder = {0,1,2,3,4,5,6,7,8,9,17,18,19,20,21,23,24,25,26};
        for (int a : placeholder) {
            mainGUI.setItem(a, 
                new ItemBuilder()
                    .setMaterial(Material.BLACK_STAINED_GLASS_PANE)
                    .setName("")
                    .whenClicked(canceEvent)
                    .build());
        }

        PersistentDataContainer container = p.getPersistentDataContainer();
        if (container.has(firstReward)) {
            mainGUI.setItem(11, 
            new ItemBuilder()
                .setMaterial(Material.RED_WOOL)
                .setGlint(false)
                .addLoreLine("<red>Du hast bereits einen Spieler angegeben der dich eingeladen hat!</red>")
                .setName("<red>Bereits eingegeben</red>")
                .whenClicked(canceEvent)
                .build());
        } else {
            mainGUI.setItem(11, 
            new ItemBuilder()
                .setMaterial(Material.GREEN_WOOL)
                .setGlint(true)
                .addLoreLine("<green>Gib an wer dich eingeladen hat um Belohnungen zu erhalten.</green>")
                .setName("<green>Eingeladen?</green>")
                .whenClicked(first)
                .build());
        }

        if (container.has(secondReward)) {
            mainGUI.setItem(13, 
            new ItemBuilder()
                .setMaterial(Material.RED_WOOL)
                .setGlint(false)
                .addLoreLine("<red>Du hast diese Belohnung bereits eingefordert!</red>")
                .setName("<red>Bereits eingefordert</red>")
                .whenClicked(canceEvent)
                .build());
        } else if (container.has(firstReward) && (p.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) > 14400) {
            mainGUI.setItem(13, 
            new ItemBuilder()
                .setMaterial(Material.GREEN_WOOL)
                .setGlint(true)
                .addLoreLine("<green>klicke um deine Belohnung zu erhalten.</green>")
                .setName("<green>Erledigt</green>")
                .whenClicked(second)
                .build());
        } else {
            mainGUI.setItem(13, 
            new ItemBuilder()
                .setMaterial(Material.RED_WOOL)
                .setGlint(false)
                .addLoreLine("<red>Spiele 4 Stunden um diese Belohnung zu erhalten!</red>")
                .setName("<red>Die ersten Stunden</red>")
                .whenClicked(canceEvent)
                .build());
        }

        if (container.has(thirdReward)){
            mainGUI.setItem(15, 
            new ItemBuilder()
                .setMaterial(Material.RED_WOOL)
                .setGlint(false)
                .addLoreLine("<red>Du hast diese Belohnung bereits eingefordert!</red>")
                .setName("<red>Bereits eingefordert</red>")
                .whenClicked(canceEvent)
                .build());
        } else if (container.has(secondReward) && (p.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) > 43200) {
            mainGUI.setItem(15, 
            new ItemBuilder()
                .setMaterial(Material.GREEN_WOOL)
                .setGlint(true)
                .addLoreLine("<green>klicke um deine Belohnung zu erhalten.</green>")
                .setName("<green>Erledigt</green>")
                .whenClicked(third)
                .build());
        } else {
            mainGUI.setItem(15,
            new ItemBuilder()
                .setMaterial(Material.RED_WOOL)
                .setGlint(false)
                .addLoreLine("<red>Spiele 12 Stunden um diese Belohnung zu erhalten!</red>")
                .setName("<red>Deine Reise beginnt Stunden</red>")
                .whenClicked(canceEvent)
                .build());
        }

        mainGUI.setItem(22, 
            new ItemBuilder()
                .setMaterial(Material.BARRIER)
                .setName("<red>Schließen</red>")
                .whenClicked(closeInv)
                .build());

    }

    @Override
    public @NotNull Inventory getInventory() {
        return mainGUI;
    }

        public static void setTableFromDBRewards(DataBasePool pool, UUID uuid) {
        String querry = "INSERT INTO `rewards` (`uuid`, `reward`) VALUES (?, 1);";

        try {
            Connection con = pool.getConnection();
            PreparedStatement sel = con.prepareStatement(querry);
            sel.setObject(1, uuid);
            sel.executeUpdate();
            sel.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setTableFromDBUUIDS(DataBasePool pool, UUID uuid, UUID invitedUuid) {
        String querry = "INSERT INTO `uuids` (`uuid`, `InvitedUUID`) VALUES (?, ?);";

        try {
            Connection con = pool.getConnection();
            PreparedStatement sel = con.prepareStatement(querry);
            sel.setObject(1, uuid);
            sel.setObject(2, invitedUuid);
            sel.executeUpdate();
            sel.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
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
