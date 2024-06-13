package de.jonas.refersystem;

import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.jonas.refersystem.commands.OpenGUI;
import de.jonas.refersystem.listener.CheckRewards;
import de.jonas.stuff.Stuff;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;

public final class ReferSystem extends JavaPlugin {

    public static ReferSystem INSTACE;
    public DataBasePool dbPool;
    public Stuff stuff;

    @Override
    public void onLoad() {

        INSTACE = this;
        stuff = Stuff.INSTANCE;
        dbPool = new DataBasePool();

        if (!CommandAPI.isLoaded()) CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
        new OpenGUI();

    }

    @Override
    public void onEnable() {

        dbPool.init();

        try {
            dbPool.createTableRewards();
            dbPool.createTableUUIDS();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        new Events().onEnable();

        CommandAPI.onEnable();

        this.listener();

        this.saveDefaultConfig();

        getLogger().log(Level.INFO, "Plugin activated");

    }

    @Override
    public void onDisable() {

        CommandAPI.onDisable();

        dbPool.shutdown();

        getLogger().log(Level.INFO,"Plugin deactivated");


    }

    public void listener() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new CheckRewards(), this);
    }

}
