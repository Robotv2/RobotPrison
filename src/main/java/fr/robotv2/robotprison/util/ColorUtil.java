package fr.robotv2.robotprison.util;

import org.bukkit.ChatColor;

public class ColorUtil {

    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
