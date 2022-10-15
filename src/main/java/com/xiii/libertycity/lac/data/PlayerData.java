package com.xiii.libertycity.lac.data;

import com.xiii.libertycity.lac.LAC;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckManager;
import com.xiii.libertycity.lac.exempt.Exempt;
import com.xiii.libertycity.lac.processor.PredictionProcessor;
import com.xiii.libertycity.lac.utils.BoundingBox;
import com.xiii.libertycity.lac.utils.SampleList;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.FutureTask;

@Getter
@Setter
public class PlayerData {

    public Player player;
    public UUID uuid;

    private HashMap<String, HashMap<String, Integer>> flags = new HashMap<String, HashMap<String, Integer>>();
    public ArrayDeque<org.bukkit.util.Vector> teleports = new ArrayDeque<>();
    public SampleList<Location> targetLocations = new SampleList<>(5, true);
    public SampleList<BoundingBox> targetBoundingBoxes = new SampleList<>(5, true);
    public SampleList<Integer> transactionPackets = new SampleList<>(10, true);
    public ArrayDeque<Integer> sensitivitySamples = new ArrayDeque<>();

    public Exempt exempt = new Exempt(this);
    public CheckManager checkManager = new CheckManager();
    public PredictionProcessor predictionProcessor = new PredictionProcessor(this);

    public boolean alertsToggled, playerGround, serverGround, lastPlayerGround, lastLastPlayerGround, nearEntity, isOnStair,isInLiquid, isInFullLiquid, aboveLiquid, isOnSlab, blockAbove, blockAboveWater, isDead, inAnimation, sentStartDestroy, inWeb, onIce, onSlime, onLowBlock, collidesHorizontally, inAir, onSolidGround, nearTrapdoor, nearPiston, isTeleporting, onClimbable, validVelocityHit, nearBoat, noCheckNextFlying, lastNoCheckNextFlying, lastLastNoCheckNextFlying, onSoulSand, onCake, nearBerry, isCinematic, isDigging;
    public long serverKeepAlive, joined, tpAfterJoin, lastTeleport, weirdTeleport, lastBlockPlaced, lastVelocity, wasDead, lastFlyingTime, brokeBlock, entityHit, lastAttack, lastHurt, lastHurtOther, lastNearBoat, lastCinematic, lastTakeDamage;
    public int join, cancel, ping, kbLevel, sinceSlimeTicks, teleportTickFix, ticks, cinematicTicks, sensitivity;
    public double motionX, motionY, motionZ, lastMotionX, lastMotionY, lastMotionZ, lastHealth, lastFallDistance, lastBlockAbove, lastIce, lastSlime, lastLowBlock, pMotionX, pMotionY, pMotionZ, lastGlide, lastOnSoulSand, lastNearBerry, lastUse = 9999999, lastShootDelay = 9999999, lastShoot = 9999999, shootDelay = 9999999, eatDelay = 9999999, lastEat = 99999999;
    public float deltaYaw, deltaPitch, lastDeltaYaw, lastDeltaPitch, lastAccelYaw, lastAccelPitch, realGCD;
    public Location from, to, sTo, sFrom;
    public Block brokenBlock, blockPlaced;
    public Entity target, lastTarget;
    public WrappedPacketInUseEntity.EntityUseAction useAction;
    public String clientBrand;

    public PlayerData(Player p) {
        this.player = p;
        this.uuid = p.getUniqueId();

        Bukkit.getScheduler().runTaskTimerAsynchronously(LAC.INSTANCE, () -> {
            if(target != null) {
                if(target != lastTarget) targetLocations.clear();
                targetLocations.add(target.getLocation());
            }
        }, 1, 1);

        /*
        Bukkit.getScheduler().runTaskTimerAsynchronously(LAC.INSTANCE, () -> {
            if(target != null) {
                if(target != lastTarget) {
                    targetBoundingBoxes.clear();
                }

                targetBoundingBoxes.add(new BoundingBox(target.getBoundingBox()));
            }
        }, 1, 1);
         */

        alertsToggled = LAC.configUtils.getBooleanFromConfig("config", "testMode", false);
    }

    public void addFlag(String check) {
        HashMap<String, Integer> inner = flags.get(player.getName());
        if(inner == null) inner = new HashMap<>();
        inner.put(check, inner.getOrDefault(check, 0) + 1);
        flags.put(player.getName(), inner);
    }

    public int getFlags(String plname, String type) {
        if (flags.get(plname) == null) return 0;
        return flags.get(plname).getOrDefault(type, 0);
    }

    public void flag(Check check, int threshold, Object... debug) {
        if(player != null) {
            addFlag(check.name);
            String buf = "";
            String Value = "";
            String Info = "";
            String Buffer = "";
            String maxBuffer = "";
            String state = "";
            int i = 0;
            for (Object obj : debug) {
                i++;
                if(i == 1) {
                    Info = obj.toString();
                } else if(i == 2) {
                    Value = obj.toString();
                }else if(i == 3) {
                    Buffer = obj.toString();
                }else if(i == 4) {
                    maxBuffer = obj.toString();
                }else if(i == 5) {
                    state = obj.toString();
                }
                buf += obj.toString() + ", ";
            }
            final String text = "" + "\n" + " §7» §f" + Info + "\n" + " §7» §f" + Value + "\n" + " §7» §c" + Buffer + "§7/§c" + maxBuffer + "\n";
            final String prefix = LAC.configUtils.getStringFromConfig("config", "prefix","§c§l[AC]");
            for (Player p : Bukkit.getOnlinePlayers()) {
                boolean d = LAC.configUtils.getBooleanFromConfig("config", "testMode", false);
                PlayerData gp = Data.getPlayerData(p);
                if ((gp.alertsToggled)) { // TODO: /alerts command test later && (!d && !player.getName().equals(p.getName()))         || (d && !p.getName().equals(player.getName()))
                    TextComponent Flag = new TextComponent(prefix + " §c" + player.getName() + " §7flagged §c" + check.name + " §7Ping: §c" + getPing() + " §7[§c" + getFlags(player.getName(), check.name) + "§7]");
                    Flag.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(text).create()));
                    p.spigot().sendMessage(Flag);
                }
            }
            if (getFlags(player.getName(), check.name) >= threshold) {
                punishPlayer(player, check.name, check.kickable, check.bannable, check.silent);
            }
        }
    }

    public void punishPlayer(Player player, String checkName, boolean checkKickable, boolean checkBannable, boolean broadcastPunish) {

        if(player != null & checkName != null && LAC.configUtils.getBooleanFromConfig("config", "autoPunish", false)) {

            if(checkKickable && !checkBannable && LAC.configUtils.getBooleanFromConfig("config", "allowAutoKick", false)) {

                if(broadcastPunish) {

                    Bukkit.getScheduler().runTask(LAC.INSTANCE, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lac kick " + player.getName()));

                    final String broadcastMode = LAC.configUtils.getStringFromConfig("config", "broadcastPunishMode", "1");

                    if(broadcastMode.equalsIgnoreCase("1")) {
                        Bukkit.broadcastMessage("");
                        Bukkit.broadcastMessage("§c✘ " + player.getName() + " a été retiré du serveur.");
                        Bukkit.broadcastMessage("");
                    } else if(broadcastMode.equalsIgnoreCase("2")) {
                        Bukkit.broadcast("", LAC.configUtils.getStringFromConfig("config", "broadcastPermission", "null"));
                        Bukkit.broadcast("§c✘ " + player.getName() + " a été retiré du serveur.", LAC.configUtils.getStringFromConfig("config", "broadcastPermission", "null"));
                        Bukkit.broadcast("", LAC.configUtils.getStringFromConfig("config", "broadcastPermission", "null"));
                    }

                } else {

                    Bukkit.getScheduler().runTask(LAC.INSTANCE, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lac kick " + player.getName()));

                }

            }

            if(!checkKickable && checkBannable && LAC.configUtils.getBooleanFromConfig("config", "allowAutoBan", false)) {

                if(broadcastPunish) {

                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lac ban " + player.getName());

                    final String broadcastMode = LAC.configUtils.getStringFromConfig("config", "broadcastPunishMode", "1");

                    if(broadcastMode.equalsIgnoreCase("1")) {
                        Bukkit.broadcastMessage("");
                        Bukkit.broadcastMessage("§c✘ " + player.getName() + " a été retiré du serveur.");
                        Bukkit.broadcastMessage("");
                    } else if(broadcastMode.equalsIgnoreCase("2")) {
                        Bukkit.broadcast("", LAC.configUtils.getStringFromConfig("config", "broadcastPermission", "null"));
                        Bukkit.broadcast("§c✘ " + player.getName() + " a été retiré du serveur.", LAC.configUtils.getStringFromConfig("config", "broadcastPermission", "null"));
                        Bukkit.broadcast("", LAC.configUtils.getStringFromConfig("config", "broadcastPermission", "null"));
                    }

                } else {

                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lac ban " + player.getName());

                }

            }

        }

    }

    public double getTPS() {
        return PacketEvents.get().getServerUtils().getTPS();
    }

    public boolean hasPotionEffect(PotionEffectType type) {
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            if(potionEffect.getType().equals(type))
                return true;
        }
        return false;
    }
    public Optional<PotionEffect> getEffectByType(PotionEffectType type) {
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            if(potionEffect.getType().equals(type))
                return Optional.of(potionEffect);
        }
        return Optional.empty();
    }
    public int getPotionEffectAmplifier(PotionEffectType type) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().getName().equals(type.getName())) {
                return (effect.getAmplifier() + 1);
            }
        }
        return 0;
    }

    public int getDepthStriderLevel() {
        if(PacketEvents.get().getServerUtils().getVersion().isNewerThanOrEquals(ServerVersion.v_1_8)) {
            if (player.getInventory().getBoots() != null) {
                return player.getInventory().getBoots().getEnchantmentLevel(Enchantment.DEPTH_STRIDER);
            }
        }
        return 0;
    }

    public float getEyeHeight()
    {
        float f = 1.62F;
        if (player.isSleeping())
        {
            f = 0.2F;
        }
        if (player.isSneaking())
        {
            f -= 0.08F;
        }
        return f;
    }
    public float wrapAngleTo180_float(float value)
    {
        value = value % 360.0F;
        if (value >= 180.0F)
        {
            value -= 360.0F;
        }
        if (value < -180.0F)
        {
            value += 360.0F;
        }
        return value;
    }

    public void doMove() {
        if(from == null) {
            from = to;
        }
        lastMotionX = motionX;
        lastMotionY = motionY;
        lastMotionZ = motionZ;
        lastDeltaPitch = deltaPitch;
        lastDeltaYaw = deltaYaw;
        deltaPitch = to.getPitch() - from.getPitch();
        deltaYaw = yawTo180F(to.getYaw() - from.getYaw());
        motionX = to.getX() - from.getX();
        motionY = to.getY() - from.getY();
        motionZ = to.getZ() - from.getZ();
        nearBoat = getEntitiesInRadius(to.clone(), 1).stream().anyMatch(e -> e instanceof Boat);
        nearEntity = !getEntitiesInRadius(to.clone(), 1).isEmpty();
    }

    public float yawTo180F(float flub) {
        if ((flub %= 360.0f) >= 180.0f) {
            flub -= 360.0f;
        }
        if (flub < -180.0f) {
            flub += 360.0f;
        }
        return flub;
    }

    public void resetFlags(String plname) {
        flags.put(plname, new HashMap<String, Integer>());
    }

    public double getDeltaXZSqrt() {
        return Math.sqrt(motionX * motionX + motionZ * motionZ);
    }

    public double getLastDeltaXZSqrt() {
        return Math.sqrt(lastMotionX * lastMotionX + lastMotionZ * lastMotionZ);
    }

    public double getDeltaXZ() {
        return Math.hypot(motionX, motionZ);
    }

    public double getLastDeltaXZ() {
        return Math.hypot(lastMotionX, lastMotionZ);
    }

    public double getDistance(boolean y) {
        if (sFrom != null) {
            if (y) {
                Location newLocation = sTo.clone();
                newLocation.setY(sFrom.clone().getY());
                return newLocation.distance(sFrom.clone());
            }
            return sTo.clone().distance(sFrom.clone());
        }
        return 0;
    }

    public long getGcd(final long current, final long previous) {
        return (previous <= 16384L) ? current : getGcd(previous, current % previous);
    }

    public List<Entity> getEntitiesInRadius(Location location, double radius) {
        int maxX = (int) Math.floor((location.getX()+radius) / 16.0);
        int minX = (int) Math.floor((location.getX()-radius) / 16.0);
        int maxZ = (int) Math.floor((location.getZ()+radius) / 16.0);
        int minZ = (int) Math.floor((location.getZ()-radius) / 16.0);
        List<Entity> entities = new LinkedList<>();
        for(int i = minX; i <= maxX; i++) {
            for(int i2 = minZ; i2<= maxZ; i2++) {
                if(!location.getWorld().isChunkLoaded(i, i2)) continue;

                for(Entity entity : location.getWorld().getChunkAt(i, i2).getEntities()) {
                    if(entity == null) continue;
                    if(entity.getLocation().distanceSquared(location) > radius * radius) continue;
                    entities.add(entity);
                }
            }
        }
        return entities;
    }

    public double getGCD(final double v1, final double v2) {
        if(v1 < v2) {
            return getGCD(v2, v1);
        }
        if(Math.abs(v2) < 0.001) {
            return v1;
        } else {
            return getGCD(v2, v1 - Math.floor(v1 / v2) * v2);
        }
    }

    public Block getBlock(final Location location) {
        if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            return location.getBlock();
        } else {
            FutureTask<Block> futureTask = new FutureTask<>(() -> {
                location.getWorld().loadChunk(location.getBlockX() >> 4, location.getBlockZ() >> 4);
                return location.getBlock();
            });
            Bukkit.getScheduler().runTask(LAC.INSTANCE, futureTask);
            try {
                return futureTask.get();
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
            return null;
        }
    }
}


