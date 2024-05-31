package de.jonas.refersystem;

import de.jonas.refersystem.commands.Input;
import de.jonas.refersystem.commands.OpenGUI;
import de.jonas.refersystem.listener.CheckRewards;
import de.jonas.refersystem.listener.FirstJoin;
import de.jonas.refersystem.listener.InventoryClick;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Level;

public final class ReferSystem extends JavaPlugin {

    public static ReferSystem INSTACE;
    public DataBasePool dbPool;

    @Override
    public void onLoad() {

        INSTACE = this;

        dbPool = new DataBasePool();

        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
        new OpenGUI();
        new Input();

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

        CommandAPI.onEnable();

        this.listener();

        this.saveDefaultConfig();

        getLogger().log(Level.INFO, "Plugin activated");

    }

    @Override
    public void onDisable() {

        CommandAPI.onEnable();

        dbPool.shutdown();

        getLogger().log(Level.INFO,"Plugin deactivated");


    }

    public void listener() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new InventoryClick(), this);
        pm.registerEvents(new CheckRewards(), this);
        pm.registerEvents(new FirstJoin(), this);
    }

}