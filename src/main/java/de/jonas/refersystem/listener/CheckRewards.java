package de.jonas.refersystem.listener;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.jonas.refersystem.DataBasePool;
import de.jonas.refersystem.ReferSystem;
import de.jonas.refersystem.Rewards;

public class CheckRewards implements Listener {

    DataBasePool db = ReferSystem.INSTACE.dbPool;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        List<Integer> list = DataBasePool.getRewardsLeft(db, p.getUniqueId());

        for (int reward : list) {
            switch (reward) {
                case 1 -> Rewards.reward1(p);
                case 2 -> Rewards.reward2(p);
                case 3 -> Rewards.reward3(p);
            }
        }

    }

}
