package com.xiii.libertycity.lac.listener;

import com.xiii.libertycity.lac.LAC;
import com.xiii.libertycity.lac.data.Data;
import com.xiii.libertycity.lac.data.PlayerData;
import io.github.retrooper.packetevents.PacketEvents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.nio.charset.StandardCharsets;

public class ClientBrandListener implements PluginMessageListener, Listener {

    @Override
    public void onPluginMessageReceived(final String channel, final Player player, final byte[] msg) {
        try {
            PlayerData data = Data.getPlayerData(player);
            if (msg.length == 0) return;
            final String clientBrand = new String(msg, StandardCharsets.UTF_8).length() > 0 ? new String(msg, StandardCharsets.UTF_8).substring(1) : new String(msg, StandardCharsets.UTF_8);
            data.clientBrand = clientBrand;
            for(Player p : Bukkit.getOnlinePlayers()) if(p.hasPermission("LibertyCity.LAC.joinalerts")) p.sendMessage(LAC.configUtils.getStringFromConfig("config", "prefix","§c§l[AC]") + " §c" + player.getName() + " §7a rejoint avec §c" + clientBrand + " §7en §c" + PacketEvents.get().getPlayerUtils().getClientVersion(player).name().replaceAll("_", ".").substring(2));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        addChannel(event.getPlayer());
    }

    private void addChannel(Player player) {
        try {
            player.getClass().getMethod("addChannel", String.class).invoke(player, "MC|BRAND");
        } catch (final Exception e) {
            try {
                player.getClass().getMethod("addChannel", String.class).invoke(player, "mc|brand");
            } catch (final Exception e2) {
                try {
                    player.getClass().getMethod("addChannel", String.class).invoke(player, "MC:BRAND");
                } catch (final Exception e3) {
                    try {
                        player.getClass().getMethod("addChannel", String.class).invoke(player, "mc:brand");
                    } catch (final Exception e4) {
                        try {
                            player.getClass().getMethod("addChannel", String.class).invoke(player, "minecraft:brand");
                        } catch (final Exception e5) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
