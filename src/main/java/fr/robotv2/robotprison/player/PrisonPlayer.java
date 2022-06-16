package fr.robotv2.robotprison.player;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.robotv2.robotprison.RobotPrison;
import fr.robotv2.robotprison.events.CurrencyValueChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@DatabaseTable(tableName = "player-data")
public final class PrisonPlayer {

    @DatabaseField(columnName = "player_uuid", id = true, unique = true)
    private UUID playerUUID;

    @DatabaseField(columnName = "auto_sell")
    private boolean autoSell;

    @DatabaseField(columnName = "currencies", dataType = DataType.SERIALIZABLE)
    private final HashMap<Currency, Double> currencies = new HashMap<>();

    public PrisonPlayer() {}

    public PrisonPlayer(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public UUID getUniqueId() {
        return playerUUID;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerUUID);
    }

    public boolean hasEnoughCurrency(Currency currency, double value) {
        return getCurrency(currency) > value;
    }

    public double getCurrency(Currency currency) {
        return currencies.getOrDefault(currency, 0D);
    }

    public void setCurrency(Currency currency, double value, CurrencyValueChangeEvent.CurrencyChangeReason reason) {

        final CurrencyValueChangeEvent event = new CurrencyValueChangeEvent(currency, reason,this, getCurrency(currency), value);
        Bukkit.getPluginManager().callEvent(event);

        if(!event.isCancelled()) {
            this.currencies.put(currency, event.getTo());
        }
    }

    public void setCurrency(Currency currency, double value) {
        setCurrency(currency, value, CurrencyValueChangeEvent.CurrencyChangeReason.UNKNOWN);
    }

    // AUTO SELL

    public boolean hasAutoSell() {
        return autoSell;
    }

    public void setAutoSell(boolean value) {
        this.autoSell = value;
    }

    /**
     * Save player to database.
     */
    public void save() {
        RobotPrison.get().getDataManager().savePrisonPlayer(this);
    }


    // <<- STATIC METHOD ->>

    static Map<UUID, PrisonPlayer> players = new ConcurrentHashMap<>();

    public static void registerPlayer(PrisonPlayer player) {
        players.put(player.playerUUID, player);
    }

    public static Collection<PrisonPlayer> getPrisonPlayers() {
        return players.values();
    }

    public static PrisonPlayer getPrisonPlayer(Player player) {
        return getPrisonPlayer(player.getUniqueId(), false);
    }

    public static PrisonPlayer getPrisonPlayer(UUID playerUUID, boolean force) {

        if(players.containsKey(playerUUID)) {
            return players.get(playerUUID);
        }

        if(!force) {
            return null;
        }

        return RobotPrison.get().getDataManager().getPrisonPlayer(playerUUID);
    }
}
