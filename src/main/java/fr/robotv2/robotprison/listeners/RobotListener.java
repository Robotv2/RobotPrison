package fr.robotv2.robotprison.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class RobotListener implements Listener {

    private final JavaPlugin instance;
    public RobotListener(JavaPlugin plugin) {
        this.instance = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public JavaPlugin getInstance() {
        return instance;
    }
}
