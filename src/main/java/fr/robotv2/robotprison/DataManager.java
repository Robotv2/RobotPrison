package fr.robotv2.robotprison;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.robotv2.robotprison.player.PrisonPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.UUID;

public class DataManager {

    private ConnectionSource source;
    private Dao<PrisonPlayer, UUID> prisonPlayerDao;

    protected void initialize(@NotNull ConnectionSource source) throws SQLException {
        this.source = source;
        this.prisonPlayerDao = DaoManager.createDao(source, PrisonPlayer.class);
        TableUtils.createTableIfNotExists(source, PrisonPlayer.class);
    }

    protected void closeConnection() {
        this.source.closeQuietly();
    }

    @Nullable
    public PrisonPlayer getPrisonPlayer(@NotNull UUID playerUUID) {
        try {
            return this.prisonPlayerDao.queryForId(playerUUID);
        } catch (SQLException e) {
            RobotPrison.get().getLogger().warning("Couldn't query data for uuid: " + playerUUID);
            return null;
        }
    }

    public void savePrisonPlayer(@NotNull PrisonPlayer data) {
        try {
            this.prisonPlayerDao.createOrUpdate(data);
        } catch (SQLException e) {
            RobotPrison.get().getLogger().warning("Couldn't save data for uuid:  " + data.getUniqueId());
        }
    }
}
