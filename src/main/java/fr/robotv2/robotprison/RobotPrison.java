package fr.robotv2.robotprison;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import fr.robotv2.robotprison.commands.CurrencyPrisonCommand;
import fr.robotv2.robotprison.commands.RobotPrisonCommand;
import fr.robotv2.robotprison.enchant.PrisonEnchant;
import fr.robotv2.robotprison.enchant.PrisonEnchantManager;
import fr.robotv2.robotprison.enchant.impl.EfficiencyEnchant;
import fr.robotv2.robotprison.enchant.impl.HasteEnchant;
import fr.robotv2.robotprison.enchant.impl.TokenatorEnchant;
import fr.robotv2.robotprison.listeners.PickaxeListeners;
import fr.robotv2.robotprison.listeners.PlayerListener;
import fr.robotv2.robotprison.listeners.RobotListener;
import fr.robotv2.robotprison.player.PrisonPlayer;
import fr.robotv2.robotprison.ui.GuiManager;
import fr.robotv2.robotprison.ui.impl.PickaxeEnchantGui;
import fr.robotv2.robotprison.util.FileUtil;
import fr.robotv2.robotprison.util.config.ConfigAPI;
import fr.robotv2.robotprison.util.dependencies.VaultAPI;
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

    private GuiManager guiManager;
    private PrisonEnchantManager prisonEnchantManager;
    private DataManager dataManager;

    @Override
    public void onEnable() {

        this.prisonEnchantManager = new PrisonEnchantManager();
        this.dataManager = new DataManager();
        this.guiManager = new GuiManager(this);

        this.setupDependencies();
        this.setupFiles();
        this.setupDefaultEnchants();
        this.setupListeners();
        this.setupCommands();
        this.setupGuiManager();

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
        ConfigAPI.getConfig("configuration").reload();
        ConfigAPI.getConfig("enchants").reload();
        this.setupDefaultEnchants();
    }

    // <- LOADERS -->

    private void setupDependencies() {
        VaultAPI.initialize(getServer());
    }

    private void setupListeners() {
        new RobotListener(this);
        new PlayerListener(this);
        new PickaxeListeners(this);
    }

    private void setupCommands() {
        final BukkitCommandHandler handler = BukkitCommandHandler.create(this);

        handler.setLocale(Locale.FRANCE);
        handler.registerDependency(RobotPrison.class, this);

        handler.getAutoCompleter().registerSuggestion("enchants", getEnchantManager().getEnchantsIds());
        handler.registerValueResolver(PrisonEnchant.class, context -> getEnchantManager().getEnchant(context.pop()));

        handler.register(new RobotPrisonCommand());
        handler.register(new CurrencyPrisonCommand());
    }

    private void setupFiles() {
        ConfigAPI.init(this);
        ConfigAPI.getConfig("enchants").setup();
        ConfigAPI.getConfig("configuration").setup();
    }

    private void setupDefaultEnchants() {
        getEnchantManager().clearEnchants();
        getEnchantManager().registerPrisonEnchant(new TokenatorEnchant());
        getEnchantManager().registerPrisonEnchant(new EfficiencyEnchant());
        getEnchantManager().registerPrisonEnchant(new HasteEnchant());
    }

    private void setupGuiManager() {
        getServer().getPluginManager().registerEvents(this.guiManager, this);
        GuiManager.addMenu(new PickaxeEnchantGui(this));
    }

    // <- GETTERS -->

    public FileConfiguration getEnchantConfiguration() {
        return ConfigAPI.getConfig("enchants").get();
    }

    public PrisonEnchantManager getEnchantManager() {
        return prisonEnchantManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}
