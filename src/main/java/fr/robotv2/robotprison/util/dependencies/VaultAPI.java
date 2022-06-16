package fr.robotv2.robotprison.util.dependencies;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultAPI {

    private static Economy eco;
    public static boolean initialize(Server server) {
        if (server.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = server.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        eco = rsp.getProvider();
        return true;
    }

    public static boolean hasEnough(OfflinePlayer offlinePlayer, Double value) {
        return eco.has(offlinePlayer, value);
    }

    public static Double getBalance(OfflinePlayer offlinePlayer) {
        return eco.getBalance(offlinePlayer);
    }

    public static void setBalance(OfflinePlayer offlinePlayer, Double value) {
        VaultAPI.giveMoney(offlinePlayer, value - VaultAPI.getBalance(offlinePlayer));
    }

    public static void giveMoney(OfflinePlayer offlinePlayer, Double value) {
        eco.depositPlayer(offlinePlayer, value);
    }

    public static void takeMoney(OfflinePlayer offlinePlayer, Double value) {
        eco.withdrawPlayer(offlinePlayer, value);
    }
}
