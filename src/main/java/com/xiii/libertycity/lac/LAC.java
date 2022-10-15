package com.xiii.libertycity.lac;

import com.xiii.libertycity.lac.command.Command;
import com.xiii.libertycity.lac.data.Data;
import com.xiii.libertycity.lac.listener.ClientBrandListener;
import com.xiii.libertycity.lac.listener.Events;
import com.xiii.libertycity.lac.listener.PacketListener;
import com.xiii.libertycity.lac.utils.ConfigUtils;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.settings.PacketEventsSettings;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;

public final class LAC extends JavaPlugin {

    public static Plugin INSTANCE;
    public static ConfigUtils configUtils;
    public static PacketListener listener;

    @Override
    public void onLoad() {
        PacketEvents.create(this);
        PacketEventsSettings settings = PacketEvents.get().getSettings();
        settings
                .fallbackServerVersion(ServerVersion.v_1_7_10)
                .compatInjector(false)
                .checkForUpdates(false);
        PacketEvents.get().load();
        // PacketEvents.get().loadAsyncNewThread();
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        listener = new PacketListener();
        Data.clearData();
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        Bukkit.getPluginManager().registerEvents(new ClientBrandListener(), this);
        Bukkit.getPluginCommand("lac").setExecutor(new Command());
        configUtils = new ConfigUtils(this);
        PacketEvents.get().init();
        PacketEvents.get().registerListener(LAC.listener);
        final Messenger messenger = Bukkit.getMessenger();
        messenger.registerIncomingPluginChannel(this, "MC|BRAND", new ClientBrandListener());
    }

    @Override
    public void onDisable() {
        PacketEvents.get().terminate();
        Bukkit.getScheduler().cancelTasks(this);
    }
}
