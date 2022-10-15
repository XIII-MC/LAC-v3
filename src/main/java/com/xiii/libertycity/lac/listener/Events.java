package com.xiii.libertycity.lac.listener;

import com.xiii.libertycity.lac.LAC;
import com.xiii.libertycity.lac.data.Data;
import com.xiii.libertycity.lac.data.PlayerData;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;

public class Events implements Listener {

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(LAC.INSTANCE, () -> {
            Data.addPlayerData(e.getPlayer());
            PlayerData gp = Data.getPlayerData(e.getPlayer());
            if (e.getCause() != PlayerTeleportEvent.TeleportCause.UNKNOWN) {
                if (System.currentTimeMillis() - gp.joined < 800) {
                    gp.tpAfterJoin = System.currentTimeMillis();
                }
                if (System.currentTimeMillis() - gp.wasDead < 300) {
                    gp.tpAfterJoin = System.currentTimeMillis();
                }
                gp.lastTeleport = System.currentTimeMillis();

            } else {
                //Bukkit.broadcastMessage(e.getPlayer().getName() + " " + e.getTo().distanceSquared(e.getFrom()));
                if (e.getTo().distanceSquared(e.getFrom()) < 0.09) {
                    gp.weirdTeleport = System.currentTimeMillis();
                }
            }
        });
    }



    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(LAC.INSTANCE, () -> {
            Data.addPlayerData(e.getEntity());
            PlayerData gp = Data.getPlayerData(e.getEntity());
            gp.isDead = true;
            gp.wasDead = System.currentTimeMillis();
        });
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(LAC.INSTANCE, () -> {
            Data.addPlayerData(e.getPlayer());
            PlayerData gp = Data.getPlayerData(e.getPlayer());
            gp.wasDead = System.currentTimeMillis();
        });
    }


    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(LAC.INSTANCE, () -> {
            Data.addPlayerData(e.getPlayer());
            PlayerData gp = Data.getPlayerData(e.getPlayer());
            gp.lastBlockPlaced = System.currentTimeMillis();
            gp.blockPlaced = e.getBlock();
        });
    }

    @EventHandler
    public void onDMG(EntityDamageEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(LAC.INSTANCE, () -> {
            if (e.getEntity() instanceof Player) {
                Player p = (Player) e.getEntity();
                Data.addPlayerData(p);
                PlayerData gp = Data.getPlayerData(p);
                if(e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)
                    gp.validVelocityHit = true;
                else if(PacketEvents.get().getServerUtils().getVersion().isNewerThanOrEquals(ServerVersion.v_1_9)) {
                    gp.validVelocityHit = e.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK;
                } else {
                    gp.validVelocityHit = false;
                }

                gp.lastHurt = System.currentTimeMillis();
                if(e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK)
                    gp.entityHit = System.currentTimeMillis();

                if(PacketEvents.get().getServerUtils().getVersion().isNewerThanOrEquals(ServerVersion.v_1_9)) {
                    if(e.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
                        gp.entityHit = System.currentTimeMillis();
                    }
                }


                if (e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
                    gp.lastHurtOther = System.currentTimeMillis();
            }

        });
    }

    @EventHandler
    public void onEntityDMG(EntityDamageByEntityEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(LAC.INSTANCE, () -> {
            if (e.getEntity() instanceof Player) {
                Player p = (Player) e.getEntity();
                Data.addPlayerData(p);
                PlayerData gp = Data.getPlayerData(p);
                if(e.getDamager() instanceof Player) {
                    if(!((LivingEntity) e.getDamager()).getEquipment().getItemInMainHand().getType().equals(Material.AIR)) {
                        if (((LivingEntity) e.getDamager()).getEquipment().getItemInMainHand().containsEnchantment(Enchantment.KNOCKBACK))
                            gp.kbLevel = ((LivingEntity) e.getDamager()).getEquipment().getItemInMainHand().getEnchantmentLevel(Enchantment.KNOCKBACK);

                        if (((LivingEntity) e.getDamager()).getEquipment().getItemInMainHand().containsEnchantment(Enchantment.ARROW_KNOCKBACK))
                            gp.kbLevel = ((LivingEntity) e.getDamager()).getEquipment().getItemInMainHand().getEnchantmentLevel(Enchantment.ARROW_KNOCKBACK);
                    }
                }
            }

        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(LAC.INSTANCE, () -> {
            Data.addPlayerDataJoin(e.getPlayer());
            PlayerData gp = Data.getPlayerData(e.getPlayer());
            gp.joined = System.currentTimeMillis();
            gp.join = 0;
            if(e.getPlayer().hasPermission("LibertyCity.LAC.autoalerts")) {
                e.getPlayer().sendMessage(LAC.configUtils.getStringFromConfig("config", "prefix","§c§l[AC]") + " §fAlertes §a§nactivées§r §7(Automatique)");
                gp.alertsToggled = true;
            }

            Bukkit.getScheduler().runTaskLaterAsynchronously(LAC.INSTANCE, () -> {
                if(!LAC.configUtils.getBooleanFromConfig("config", "testMode", false)) {
                    if (!gp.clientBrand.equalsIgnoreCase("fml,forge") || !PacketEvents.get().getPlayerUtils().getClientVersion(e.getPlayer()).name().replaceAll("_", ".").substring(2).equals("1.12.2"))
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lac kick " + e.getPlayer().getName());
                }
            }, 80);

        });

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        PacketEvents.get().getInjector().ejectPlayer(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(LAC.INSTANCE, () -> {
            Data.addPlayerData(e.getPlayer());
            PlayerData gp = Data.getPlayerData(e.getPlayer());
            gp.brokenBlock = e.getBlock();
            if(e.isCancelled()) {
                gp.cancel = 0;
            }
        });

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(LAC.INSTANCE, () -> {
            PlayerData data = Data.getPlayerData(e.getPlayer());
            data.lastUse = System.currentTimeMillis();
        });
    }

    @EventHandler
    public void onShoot(ProjectileLaunchEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(LAC.INSTANCE, () -> {
            if (e.getEntity() instanceof Arrow) {
                Arrow arrow = (Arrow) e.getEntity();
                if (arrow.getShooter() != null && arrow.getShooter() instanceof Player) {
                    Player player = (Player) arrow.getShooter();
                    PlayerData data = Data.getPlayerData(player);
                    data.lastShootDelay = System.currentTimeMillis() - data.lastUse;
                    data.shootDelay = System.currentTimeMillis() - data.lastShoot;
                    data.lastShoot = System.currentTimeMillis();
                }
            }
        });
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(LAC.INSTANCE, () -> {
            PlayerData data = Data.getPlayerData(e.getPlayer());
            data.eatDelay = System.currentTimeMillis() - data.lastUse;
            if (data.eatDelay < 1400) e.setCancelled(true);
            data.lastEat = System.currentTimeMillis();
        });
    }
}

