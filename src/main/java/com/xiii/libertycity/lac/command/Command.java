package com.xiii.libertycity.lac.command;

import com.xiii.libertycity.lac.LAC;
import com.xiii.libertycity.lac.data.Data;
import com.xiii.libertycity.lac.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("lac")) {
            if (sender.hasPermission("LibertyCity.LAC.command.help")) {
                if (args.length < 1 || args[0].equalsIgnoreCase("help")) {
                    sender.sendMessage("§8§M+--------------------------------------------------+");
                    sender.sendMessage("");
                    sender.sendMessage("§f/lac §7(help) §8| §fMontrer ce message.");
                    sender.sendMessage("§f/lac §6alerts §8| §fActivé/Désactivé les alertes.");
                    sender.sendMessage("§f/lac §6kick §e<Joueur> §8| §fExplusé un joueur pour 'Triche'.");
                    sender.sendMessage("§f/lac §6ban §e<Joueur> §8| §fBanniser un joueur pour 'Triche'.");
                    sender.sendMessage("");
                    sender.sendMessage("§8§M+--------------------------------------------------+");
                } else if (args.length == 1 && args[0].equalsIgnoreCase("alerts")) {
                    if (sender.hasPermission("LibertyCity.LAC.command.alerts")) {
                        PlayerData data = Data.getPlayerData((Player) sender);
                        if (data.alertsToggled) {
                            data.alertsToggled = false;
                            sender.sendMessage("§c§l[AC] §fAlertes §a§nactivées§r §f!");
                        } else {
                            data.alertsToggled = true;
                            sender.sendMessage("§c§l[AC] §fAlertes §c§ndésactivées§r §f!");
                        }
                        return true;
                    } else sender.sendMessage("§c§l[AC] §cPermission Insuffisante.'");
                } else if (args.length == 2 && args[0].equalsIgnoreCase("kick")) {
                    if (sender.hasPermission("LibertyCity.LAC.command.kick")) {
                        Player target = Bukkit.getServer().getPlayer(args[1]);
                        if (target.isOnline()) {
                            Bukkit.getScheduler().runTask(LAC.INSTANCE, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + target.getName() + " §c§l[AC] Triche -s"));
                            sender.sendMessage("§c§l[AC] §e" + target.getName() + " §fa été explusé.");
                        } else sender.sendMessage("§c§l[AC] §cAttention! Ce joueur n'est pas en ligne'");
                    } else sender.sendMessage("§c§l[AC] §cPermission Insuffisante.'");
                } else if (args.length == 2 && args[0].equalsIgnoreCase("ban")) {
                    if (sender.hasPermission("LibertyCity.LAC.command.ban")) {
                        Player target = Bukkit.getServer().getPlayer(args[1]);
                        if (target.isOnline()) {
                            Bukkit.getScheduler().runTask(LAC.INSTANCE, () -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "ban " + target.getName() + " perm Triche -s -ip"));
                            sender.sendMessage("§c§l[AC] §e" + target.getName() + " §fa été banni.");
                        } else sender.sendMessage("§c§l[AC] §cAttention! Ce joueur n'est pas en ligne'");
                    } else sender.sendMessage("§c§l[AC] §cPermission Insuffisante.'");
                }
            }
        }
        return true;
    }
}
