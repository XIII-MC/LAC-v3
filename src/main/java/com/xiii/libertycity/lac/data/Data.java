package com.xiii.libertycity.lac.data;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Data {

    public static List<PlayerData> registeredPlayers = new ArrayList<>();

    public static void addPlayerData(Player player) {
        if(player == null) return;
        if(!doesPlayerDataExist(player)) registeredPlayers.add(new PlayerData(player));
    }
    public static void addPlayerDataJoin(Player player) {
        if(player == null) return;
        if(!doesPlayerDataExist(player)) {
            registeredPlayers.add(new PlayerData(player));
        } else {
            registeredPlayers.remove(getPlayerData(player));
            addPlayerData(player);
        }
    }
    public static void removePlayerData(Player player) {
        if(player == null) return;
        if(!doesPlayerDataExist(player)) return;
        for(int i = 0; i < registeredPlayers.size(); i++) {
            PlayerData data = registeredPlayers.get(i);
            if(data.getUuid() == player.getUniqueId()) registeredPlayers.remove(i);
        }
    }

    public static PlayerData getPlayerData(Player player) {
        if(player == null) return null;
        for(int i = 0; i < registeredPlayers.size(); i++) {
            PlayerData data = registeredPlayers.get(i);
            if(data.getUuid() == player.getUniqueId()) return data;
        }
        return null;
    }

    public static boolean doesPlayerDataExist(Player player) {
        if(player == null) return true;
        for(int i = 0; i < registeredPlayers.size(); i++) {
            PlayerData data = registeredPlayers.get(i);
            if(data.getUuid() == player.getUniqueId()) return true;
        }
        return false;
    }

    public static void clearData() { registeredPlayers.clear(); }

}
