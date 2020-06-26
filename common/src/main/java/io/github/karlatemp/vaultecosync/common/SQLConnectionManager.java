package io.github.karlatemp.vaultecosync.common;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

public class SQLConnectionManager {
    private static final HikariConfig config = new HikariConfig();
    private static HikariDataSource source;

    public static synchronized void initialize(
            @NotNull Consumer<HikariConfig> initializer
    ) {
        if (source != null) throw new ExceptionInInitializerError("Connection Manager initialized");
        initializer.accept(config);
        source = new HikariDataSource(config);
    }

    public static @NotNull HikariDataSource getSource() {
        var s = source;
        if (s == null) throw new RuntimeException("Source not initialized");
        return s;
    }

    public static Connection getConnection() throws SQLException {
        return source.getConnection();
    }

    public static synchronized void shutdown() {
        var source = SQLConnectionManager.source;
        if (source == null) return;
        SQLConnectionManager.source = null;
        source.close();
    }
}
