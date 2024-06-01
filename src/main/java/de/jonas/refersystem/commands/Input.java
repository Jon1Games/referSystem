package de.jonas.refersystem.commands;

import de.jonas.refersystem.DataBasePool;
import de.jonas.refersystem.ReferSystem;
import de.jonas.refersystem.Rewards;
import de.jonas.refersystem.gui.MainGUI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class Input {

    ReferSystem rs = ReferSystem.INSTACE;
    FileConfiguration conf = rs.getConfig();
    DataBasePool db = rs.dbPool;
    MiniMessage mm = MiniMessage.miniMessage();
    Rewards rw = new Rewards();

    public Input() {

        new CommandAPICommand("referSystem:input")
                .withAliases(conf.getStringList("InputCommand.Aliases").toArray(num -> new String[num]))
                .withArguments(new OfflinePlayerArgument(conf.getString("InputCommand.suggestionName.OfflinePlacer")))
                .executesPlayer(((sender, args) -> {
                    OfflinePlayer player = (OfflinePlayer) args.get(conf.
                            getString("InputCommand.suggestionName.OfflinePlacer"));

                    if (sender.getPersistentDataContainer().has(MainGUI.firstReward)) {
                        sender.sendMessage(mm.deserialize(conf.getString("Messages.AlreadyInviter")));
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

                }))
                .register();

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
}
