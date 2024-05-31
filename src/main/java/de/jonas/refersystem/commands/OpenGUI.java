package de.jonas.refersystem.commands;

import de.jonas.refersystem.ReferSystem;
import de.jonas.refersystem.gui.MainGUI;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.configuration.file.FileConfiguration;

public class OpenGUI {

    ReferSystem rs = ReferSystem.INSTACE;
    FileConfiguration conf = rs.getConfig();

    public OpenGUI() {

        new CommandAPICommand("referSystem:opengui")
                .withPermission(conf.getString("OpenGuiCommand.Permission"))
                .withAliases(conf.getStringList("OpenGuiCommand.Aliases").toArray(num -> new String[num]))
                .executesPlayer(((sender, args) -> {
                    sender.openInventory(new MainGUI(sender).getInventory());
                }))
                .register();

    }

}
