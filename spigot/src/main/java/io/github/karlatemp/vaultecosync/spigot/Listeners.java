package io.github.karlatemp.vaultecosync.spigot;


import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.sql.SQLException;
import java.util.logging.Level;

public class Listeners implements Listener {
    private final PluginMain core;
    private static final BukkitScheduler SCHEDULER = Bukkit.getScheduler();

    public Listeners(PluginMain core) {
        this.core = core;
        SCHEDULER.runTaskTimerAsynchronously(core, () -> {
            for (var player : Bukkit.getOnlinePlayers())
                try {
                    DatabaseUtils.saveGlobalData(player);
                } catch (SQLException throwables) {
                    core.getLogger().log(Level.SEVERE, "Failed to setup eco.", throwables);
                }
        }, 60 * 20, 60 * 20);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        SCHEDULER.runTaskAsynchronously(core, () -> {
            try {
                DatabaseUtils.setupGlobalData(player);
            } catch (SQLException throwables) {
                core.getLogger().log(Level.SEVERE, "Failed to setup eco.", throwables);
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerExit(PlayerQuitEvent event) {
        var player = event.getPlayer();
        SCHEDULER.runTaskAsynchronously(core, () -> {
            try {
                DatabaseUtils.saveGlobalData(player);
            } catch (SQLException throwables) {
                core.getLogger().log(Level.SEVERE, "Failed to setup eco.", throwables);
            }
        });
    }
}
