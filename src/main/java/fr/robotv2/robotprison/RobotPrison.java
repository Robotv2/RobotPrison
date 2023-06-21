package fr.robotv2.robotprison;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.FastInvManager;
import fr.robotv2.robotprison.commands.CurrencyPrisonCommand;
import fr.robotv2.robotprison.commands.RobotPrisonCommand;
import fr.robotv2.robotprison.commands.SellProfileCommand;
import fr.robotv2.robotprison.enchant.PrisonEnchant;
import fr.robotv2.robotprison.enchant.PrisonEnchantManager;
import fr.robotv2.robotprison.enchant.impl.EfficiencyEnchant;
import fr.robotv2.robotprison.enchant.impl.HasteEnchant;
import fr.robotv2.robotprison.enchant.impl.TokenatorEnchant;
import fr.robotv2.robotprison.listeners.PickaxeListeners;
import fr.robotv2.robotprison.listeners.PlayerListener;
import fr.robotv2.robotprison.player.PrisonPlayer;
import fr.robotv2.robotprison.profile.SellManager;
import fr.robotv2.robotprison.profile.SellProfile;
import fr.robotv2.robotprison.util.FileUtil;
import fr.robotv2.robotprison.util.config.ConfigAPI;
import fr.robotv2.robotprison.util.dependencies.VaultAPI;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitCommandHandler;

import java.io.File;
import java.sql.SQLException;
import java.util.Locale;

public final class RobotPrison extends JavaPlugin {

    public static RobotPrison get() {
        return JavaPlugin.getPlugin(RobotPrison.class);
    }

    private PrisonEnchantManager prisonEnchantManager;
    private DataManager dataManager;
    private SellManager sellManager;

    @Override
    public void onEnable() {

        this.prisonEnchantManager = new PrisonEnchantManager();
        this.dataManager = new DataManager();
        this.sellManager = new SellManager();

        this.setupDependencies();
        this.setupFiles();
        this.setupDefaultEnchants();
        this.setupListeners();
        this.setupCommands();
        this.setupGuiManager();
        this.registerProfiles();

        try {
            final File file = FileUtil.createFile(getDataFolder().getPath(), "database.db");
            final String databaseURL = "jdbc:sqlite:".concat(file.getPath());
            final ConnectionSource connectionSource = new JdbcConnectionSource(databaseURL);
            this.dataManager.initialize(connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
            getLogger().warning("Couldn't connect to the database. Shutting down the plugin.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        PrisonPlayer.getPrisonPlayers().forEach(PrisonPlayer::save);
        getDataManager().closeConnection();
    }

    public void onReload() {
        ConfigAPI.getConfig("profiles").reload();
        ConfigAPI.getConfig("configuration").reload();
        ConfigAPI.getConfig("enchants").reload();

        this.setupDefaultEnchants();
        this.registerProfiles();
    }

    // <- LOADERS -->

    private void setupDependencies() {
        VaultAPI.initialize(getServer());
    }

    private void setupListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new PickaxeListeners(this), this);
    }

    private void setupCommands() {
        final BukkitCommandHandler handler = BukkitCommandHandler.create(this);

        handler.setLocale(Locale.FRANCE);
        handler.registerDependency(RobotPrison.class, this);

        handler.getAutoCompleter().registerSuggestion("enchants", getEnchantManager().getEnchantsIds());
        handler.registerValueResolver(PrisonEnchant.class, context -> getEnchantManager().getEnchant(context.pop()));

        handler.register(new RobotPrisonCommand());
        handler.register(new CurrencyPrisonCommand());
        handler.register(new SellProfileCommand(this));
    }

    private void setupFiles() {
        ConfigAPI.init(this);
        ConfigAPI.getConfig("enchants").setup();
        ConfigAPI.getConfig("configuration").setup();
        ConfigAPI.getConfig("profiles").setup();
    }

    private void setupDefaultEnchants() {
        getEnchantManager().clearEnchants();
        getEnchantManager().registerPrisonEnchant(new TokenatorEnchant());
        getEnchantManager().registerPrisonEnchant(new EfficiencyEnchant());
        getEnchantManager().registerPrisonEnchant(new HasteEnchant());
    }

    private void setupGuiManager() {
        FastInvManager.register(this);
    }

    private void registerProfiles() {

        SellProfile.clearProfiles();

        final ConfigurationSection section = ConfigAPI.getConfig("profiles").get().getConfigurationSection("profiles");

        if(section == null) {
            getLogger().warning("No Profile were found in the plugin. Please check again the 'profiles.yml' file.");
            return;
        }

        for(String key : section.getKeys(false)) {

            final ConfigurationSection keySection = section.getConfigurationSection(key);
            final SellProfile profile = new SellProfile(keySection);
            SellProfile.registerProfile(profile);

            final ConfigurationSection materialSection = keySection.getConfigurationSection("materials");

            if(materialSection == null) {
                continue;
            }

            for(String stringMaterial : materialSection.getKeys(false)) {
                final Material material = Material.matchMaterial(stringMaterial);
                if(material == null) continue;
                final double value = materialSection.getDouble(stringMaterial);
                if(value != 0) {
                    profile.addMaterial(material, value);
                }
            }
        }
    }

    // <- GETTERS -->

    public FileConfiguration getEnchantConfiguration() {
        return ConfigAPI.getConfig("enchants").get();
    }

    public PrisonEnchantManager getEnchantManager() {
        return this.prisonEnchantManager;
    }

    public DataManager getDataManager() {
        return this.dataManager;
    }

    public SellManager getSellManager() {
        return this.sellManager;
    }
}
