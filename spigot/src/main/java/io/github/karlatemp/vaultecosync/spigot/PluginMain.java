package io.github.karlatemp.vaultecosync.spigot;

import io.github.karlatemp.vaultecosync.common.SQLConnectionManager;
import io.github.karlatemp.vaultecosync.common.SQLDataInitializer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PluginMain extends JavaPlugin {
    public static Economy vault;
    public static String server;
    public static PluginMain INSTANCE;
    public static Logger logger;

    @Override
    public void onLoad() {
        INSTANCE = this;
        logger = getLogger();
        saveDefaultConfig();
        reloadConfig();
        server = getConfig().getString("server", "Server0");
        SQLConnectionManager.initialize(config -> {
            var jdbc = getConfig().getString("connect.jdbc");
            config.setJdbcUrl(jdbc);
            logger.log(Level.INFO, "Connection url set: " + jdbc);
            config.setUsername(getConfig().getString("connect.user"));
            config.setPassword(getConfig().getString("connect.passwd"));
            config.setMaximumPoolSize(10);
            config.setMaxLifetime(1800000);
            config.setConnectionTimeout(5000);
            config.setMinimumIdle(5);
            config.setIdleTimeout(6000);
            config.setConnectionTimeout(60000);
            config.setValidationTimeout(3000);
            config.setMaxLifetime(60000);
        });
        try {
            SQLDataInitializer.invoke();
        } catch (SQLException throwable) {
            getLogger().log(Level.SEVERE, "Failed to initialize sql.", throwable);
        }
    }

    @Override
    public void onEnable() {
        final var registration = getServer().getServicesManager().getRegistration(Economy.class);
        if (registration == null) {
            logger.log(Level.SEVERE, "Vault Eco not found. disable.");
            disableMe();
            return;
        }
        vault = registration.getProvider();
        getServer().getPluginManager().registerEvents(new Listeners(this), this);
    }

    @Override
    public void onDisable() {
        SQLConnectionManager.shutdown();
    }

    private void disableMe() {
        getServer().getPluginManager().disablePlugin(this);
    }
}
