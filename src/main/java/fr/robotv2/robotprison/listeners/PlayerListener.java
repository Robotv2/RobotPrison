package fr.robotv2.robotprison.listeners;

import fr.robotv2.robotprison.RobotPrison;
import fr.robotv2.robotprison.enchant.PrisonItem;
import fr.robotv2.robotprison.player.PrisonPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.UUID;

public class PlayerListener extends RobotListener {

    public PlayerListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void dataLoader(PlayerJoinEvent event) {

        // Loading data from db.
        final UUID playerUUID = event.getPlayer().getUniqueId();
        PrisonPlayer player = RobotPrison.get().getDataManager().getPrisonPlayer(playerUUID);

        if(player == null) {
            player = new PrisonPlayer(playerUUID);
        }

        PrisonPlayer.registerPlayer(player);

        //Checking and registering every prison item in his inventory.
        Arrays.stream(event.getPlayer().getInventory().getContents())
                .filter(PrisonItem::isPrisonItem)
                .forEach(PrisonItem::toPrisonItem);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent event) {

        final Player player = event.getPlayer();

        //Save player data to db.
        PrisonPlayer.getPrisonPlayer(player).save();

        //Save player prison items.
        Arrays.stream(player.getInventory().getContents())
                .filter(PrisonItem::isPrisonItem)
                .map(PrisonItem::toPrisonItem)
                .forEach(PrisonItem::saveItem);
    }
}
