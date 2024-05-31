package de.jonas.refersystem.listener;

import de.jonas.refersystem.DataBasePool;
import de.jonas.refersystem.ReferSystem;
import de.jonas.refersystem.Rewards;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CheckRewards implements Listener {

    ReferSystem rs = ReferSystem.INSTACE;
    DataBasePool db = rs.dbPool;
    Rewards rewards = new Rewards();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        List<Integer> list = getTableFromDBRewards(db, p.getUniqueId());

        for (int reward : list) {
            switch (reward) {
                case 1:
                    rewards.reward1(p);
                    break;
                case 2:
                    rewards.reward2(p);
                    break;
                case 3:
                    rewards.reward3(p);
                    break;
            }
        }

    }



    public static List<Integer> getTableFromDBRewards(DataBasePool pool, UUID uuid) {
        String querry = "DELETE FROM `rewards` WHERE `rewards`.`uuid` = ? RETURNING `rewards`.`uuid`, `rewards`.`reward`;";

        try {
            Connection con = pool.getConnection();
            PreparedStatement sel = con.prepareStatement(querry);
            sel.setObject(1, uuid);
            ResultSet res = sel.executeQuery();
            List<Integer> list = new ArrayList<>();
            for (boolean a = res.first(); a; a = res.next() ) {
                list.add(res.getInt("reward"));
            }
            sel.close();
            con.close();
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
