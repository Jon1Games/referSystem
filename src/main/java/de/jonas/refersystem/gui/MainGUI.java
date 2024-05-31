package de.jonas.refersystem.gui;

import de.jonas.refersystem.utility.ItemCreator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class MainGUI implements InventoryHolder {

    private Inventory mainGUI;
    ItemCreator itemCreator = new ItemCreator();
    MiniMessage mm = MiniMessage.miniMessage();

    public static NamespacedKey firstReward = new NamespacedKey("refersystem","first_reward");
    public static NamespacedKey secondReward = new NamespacedKey("refersystem","second_reward");
    public static NamespacedKey thirdReward = new NamespacedKey("refersystem","third_reward");

    public MainGUI(Player p) {
        if (p == null) return;
        this.mainGUI = Bukkit.createInventory(this, (3 * 9), Component.text("Nexus"));

        int[] placeholder = {0,1,2,3,4,5,6,7,8,9,17,18,19,20,21,23,24,25,26};
        for (int a : placeholder) {
            mainGUI.setItem(a, itemCreator.createItem(Material.BLACK_STAINED_GLASS_PANE,
                    0, false, Component.text("")));
        }

        Material mF, mS, mT;
        Component nF, nS, nT;
        int cF, cS, cT;
        boolean rF, rS, rT;
        List<Component> lF = new ArrayList<>();
        List<Component> lS = new ArrayList<>();
        List<Component> lT = new ArrayList<>();
        PersistentDataContainer container = p.getPersistentDataContainer();

        if (container.has(firstReward)) {
            mF = Material.RED_WOOL;
            nF = mm.deserialize("<red>Bereits eingegeben</red>");
            cF = 0;
            rF = false;
            lF.add(mm.deserialize("<red>Du hast bereits einen Spieler der dich eingeladen hat gesetzt!</red>"));
        } else {
            mF = Material.GREEN_WOOL;
            nF = mm.deserialize("<green>Eingeladen?</green>");
            cF = 2;
            rF = true;
            lF.add(mm.deserialize("<green>Du kannst hier den Spieler setzen der dich eingeladen " +
                    "hat und dir Belohnungen sichern.</green>"));
        }

        if (container.has(secondReward)) {
            mS = Material.RED_WOOL;
            nS = mm.deserialize("<red>Bereits eingefordert</red>");
            cS = 0;
            rS = false;
            lS.add(mm.deserialize("<red>Du hast diese Belohnung bereits eingefordert!</red>"));
        } else if (container.has(firstReward) && (p.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) > 14400) {
            mS = Material.GREEN_WOOL;
            nS = mm.deserialize("<green>Einforder bar</green>");
            cS = 3;
            rS = true;
            lS.add(mm.deserialize("<green>Du kannst diese Belohnung einfordern.</green>"));
        } else {
            mS = Material.RED_WOOL;
            nS = mm.deserialize("<red>Nicht einforderbar</red>");
            cS = 0;
            rS = false;
            lS.add(mm.deserialize("<red>Du must eine Spielzeit von 4 Stunden haben um " +
                    "diese Belohnung einzufordern!</red>"));
        }

        if (container.has(thirdReward)){
            mT = Material.RED_WOOL;
            nT = mm.deserialize("<red>Bereits eingefordert</red>");
            cT = 0;
            rT = false;
            lT.add(mm.deserialize("<red>Du hast diese Belohnung bereits eingefordert!</red>"));
        } else if (container.has(secondReward) && (p.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) > 43200) {
            mT = Material.GREEN_WOOL;
            nT = mm.deserialize("<green>Einforder bar</green>");
            cT = 4;
            rT = true;
            lT.add(mm.deserialize("<green>Du kannst diese Belohnung einfordern.</green>"));
        } else {
            mT = Material.RED_WOOL;
            nT = mm.deserialize("<red>Nicht einforderbar</red>");
            cT = 0;
            rT = false;
            lT.add(mm.deserialize("<red>Du must eine Spielzeit von 12 Stunden haben um " +
                    "diese Belohnung einzufordern!</red>"));
        }

        mainGUI.setItem(11, itemCreator.createItem(mF, cF, rF, nF, lF));

        mainGUI.setItem(13, itemCreator.createItem(mS, cS, rS, nS, lS));

        mainGUI.setItem(15, itemCreator.createItem(mT, cT, rT, nT, lT));

        mainGUI.setItem(22, itemCreator.createItem(Material.BARRIER, 1, false,
                mm.deserialize("<red>Schließen")));
    }

    @Override
    public @NotNull Inventory getInventory() {
        return mainGUI;
    }
}
