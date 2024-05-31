package de.jonas.refersystem.listener;

import de.jonas.refersystem.ReferSystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;


public class FirstJoin implements Listener {

    public static NamespacedKey firstJoin = new NamespacedKey("refersystem", "is_first_join");

    MiniMessage mm = MiniMessage.miniMessage();
    ReferSystem rs = ReferSystem.INSTACE;
    FileConfiguration conf = rs.getConfig();

    @EventHandler
    public void onFirstJoin(PlayerJoinEvent e) {
        if (e.getPlayer().getPersistentDataContainer().has(firstJoin)) return;
        e.getPlayer().getPersistentDataContainer().set(firstJoin, PersistentDataType.BOOLEAN, true);

        final Component mainTitle = mm.deserialize(conf.getString("FirstJoinTitle.Title"));
        final Component subtitle = mm.deserialize(conf.getString("FirstJoinTitle.SubTitle"));

        final Title title = Title.title(mainTitle, subtitle);

        e.getPlayer().showTitle(title);

    }

}
