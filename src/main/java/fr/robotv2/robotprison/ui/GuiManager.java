package fr.robotv2.robotprison.ui;

import fr.robotv2.robotprison.RobotPrison;
import fr.robotv2.robotprison.listeners.RobotListener;
import fr.robotv2.robotprison.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class GuiManager extends RobotListener {

    private static final Map<Class<? extends Gui>, Gui> menus = new HashMap<>();

    public GuiManager(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        Player player = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        InventoryHolder holder = e.getInventory().getHolder();

        if(item == null) return;
        if(!(holder instanceof Gui menu)) return;

        e.setCancelled(true);
        menu.onClick(player, e.getInventory(), item, e.getRawSlot(), e.getClick());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        InventoryHolder holder = e.getInventory().getHolder();
        if(holder instanceof Gui menu) {
            menu.onClose(player, e);
        }
    }

    public static void addMenu(Gui gui){
        menus.put(gui.getClass(), gui);
    }

    public static void open(Player player, Class<? extends Gui> gClass, Object... objects){
        if(!menus.containsKey(gClass)) {
            throw new IllegalArgumentException("gui not registered");
        }

        Gui menu = menus.get(gClass);
        Inventory inv = Bukkit.createInventory(null, menu.getSize(), ColorUtil.colorize(menu.getName(player, objects)));
        menu.startContents(player, inv, objects);

        Bukkit.getScheduler().runTaskLater(RobotPrison.get(), () -> player.openInventory(inv), 2L);
    }
}
