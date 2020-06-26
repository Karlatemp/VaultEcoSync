package io.github.karlatemp.vaultecosync.spigot;

import io.github.karlatemp.vaultecosync.common.SQLConnectionManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

import static io.github.karlatemp.vaultecosync.spigot.PluginMain.logger;

public class DatabaseUtils {
    public static void setupGlobalData(OfflinePlayer player) throws SQLException {
        try (var connection = SQLConnectionManager.getConnection()) {
            double global, current = PluginMain.vault.getBalance(player), server;
            try (var ps = connection.prepareStatement("SELECT eco FROM `vault-eco-sync-players` " +
                    "WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                var result = ps.executeQuery();
                if (!result.next()) {
                    logger.log(Level.WARNING, "Global data of " + player.getUniqueId() + " not found. create with " + current);
                    try (var ps1 = connection.prepareStatement(
                            "INSERT INTO `vault-eco-sync-players` (uuid, eco) VALUES (?, ?)"
                    )) {
                        ps1.setString(1, player.getUniqueId().toString());
                        ps1.setDouble(2, global = current);
                        ps1.executeUpdate();
                    }
                } else {
                    global = result.getDouble(1);
                }
            }


            try (var ps = connection.prepareStatement(
                    "SELECT eco FROM `vault-eco-sync-servers` WHERE uuid = ? AND server = ?"
            )) {
                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, PluginMain.server);
                var result = ps.executeQuery();
                if (result.next()) {
                    server = result.getDouble(1);
                } else {
                    try (var ps1 = connection.prepareStatement(
                            "INSERT INTO `vault-eco-sync-servers` (server, uuid, eco) VALUES (?, ?, ?)"
                    )) {
                        ps1.setString(1, PluginMain.server);
                        ps1.setString(2, player.getUniqueId().toString());
                        ps1.setDouble(3, server = current);
                        ps1.executeUpdate();
                    }
                }
            }
            var target = global + current - server;

            logger.log(Level.INFO, "Global [" + player.getUniqueId() + "] = " + global);
            logger.log(Level.INFO, "Current[" + player.getUniqueId() + "] = " + current);
            logger.log(Level.INFO, "Server [" + player.getUniqueId() + "] = " + server);
            logger.log(Level.INFO, "Target [" + player.getUniqueId() + "] = " + target);

            if (saveGlobalDataV2(player, global, target)) {
                Bukkit.getScheduler().runTask(PluginMain.INSTANCE, () -> {
                    if (target < current) {
                        PluginMain.vault.withdrawPlayer(player, current - target);
                    } else {
                        PluginMain.vault.depositPlayer(player, target - current);
                    }
                });
            }
        }
    }

    public static boolean saveGlobalDataV2(OfflinePlayer player, double old, double target) throws SQLException {
        try (var connection = SQLConnectionManager.getConnection()) {
            // The current same as server
            // diff is zero.
            logger.log(Level.INFO, "Try updating server [" + player.getUniqueId() + "] = " + target);
            try (var ps = connection.prepareStatement(
                    "UPDATE `vault-eco-sync-players` SET `eco` = ? WHERE uuid = ? AND eco = ?"
            )) {
                ps.setDouble(1, target);
                ps.setString(2, player.getUniqueId().toString());
                ps.setDouble(3, old);
                if (ps.executeUpdate() != 1) {
                    logger.log(Level.INFO, "Update server failed. Try agent.");
                    setupGlobalData(player);
                    return false;
                }
            }
            logger.log(Level.INFO, "Update global successful. Updating server.");
            updateServer(connection, player, target);
        }
        return true;
    }

    private static void updateServer(Connection connection, OfflinePlayer player, double eco) throws SQLException {
        logger.log(Level.INFO, "Update server[" + player.getUniqueId() + "] = " + eco);
        try (var ps = connection.prepareStatement(
                "UPDATE `vault-eco-sync-servers` SET `eco` = ? WHERE uuid = ? AND server = ?"
        )) {
            ps.setDouble(1, eco);
            ps.setString(2, player.getUniqueId().toString());
            ps.setString(3, PluginMain.server);
            ps.executeUpdate();
        }
    }

    public static void saveGlobalData(OfflinePlayer player) throws SQLException {
        try (var connection = SQLConnectionManager.getConnection()) {
            var eco = PluginMain.vault.getBalance(player);
            try (var ps = connection.prepareStatement(
                    "UPDATE `vault-eco-sync-players` SET `eco` = ? WHERE uuid = ?"
            )) {
                ps.setDouble(1, eco);
                ps.setString(2, player.getUniqueId().toString());
                ps.executeUpdate();
            }
            updateServer(connection, player, eco);
        }
    }
}
