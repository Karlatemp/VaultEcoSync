package io.github.karlatemp.vaultecosync.common;

import java.sql.SQLException;

public class SQLDataInitializer {
    public static void invoke() throws SQLException {
        var connection = SQLConnectionManager.getConnection();
        try (var s = connection.createStatement()) {
            s.execute(
                    "CREATE TABLE IF NOT EXISTS `vault-eco-sync-servers` (" +
                            "  `server` VARCHAR(60) NOT NULL," +
                            "  `uuid` VARCHAR(45) NOT NULL," +
                            "  `eco` DOUBLE NOT NULL," +
                            "  PRIMARY KEY (`server`, `uuid`));"
            );
        }
        try (var s = connection.createStatement()) {
            s.execute(
                    "CREATE TABLE IF NOT EXISTS `vault-eco-sync-players` (" +
                            "  `uuid` VARCHAR(45) NOT NULL," +
                            "  `eco` DOUBLE NOT NULL," +
                            "  PRIMARY KEY (`uuid`))"
            );
        }
    }
}
