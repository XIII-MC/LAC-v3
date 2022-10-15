package com.xiii.libertycity.lac.listener;

import com.xiii.libertycity.lac.LAC;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.data.Data;
import com.xiii.libertycity.lac.data.PlayerData;
import com.xiii.libertycity.lac.utils.BoundingBox;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.PacketListenerAbstract;
import io.github.retrooper.packetevents.event.PacketListenerPriority;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockdig.WrappedPacketInBlockDig;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import io.github.retrooper.packetevents.packetwrappers.play.in.pong.WrappedPacketInPong;
import io.github.retrooper.packetevents.packetwrappers.play.in.transaction.WrappedPacketInTransaction;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import io.github.retrooper.packetevents.packetwrappers.play.out.animation.WrappedPacketOutAnimation;
import io.github.retrooper.packetevents.packetwrappers.play.out.entityvelocity.WrappedPacketOutEntityVelocity;
import io.github.retrooper.packetevents.packetwrappers.play.out.position.WrappedPacketOutPosition;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.function.Predicate;

public class PacketListener extends PacketListenerAbstract {

    public PacketListener() {
        super(PacketListenerPriority.HIGHEST);
    }

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        Player p = event.getPlayer();
        if(Data.doesPlayerDataExist(p)) {
            PlayerData data = Data.getPlayerData(p);
            NMSPacket packet = event.getNMSPacket();

            if (data != null) {
                if (event.getPacketId() == PacketType.Play.Client.KEEP_ALIVE) {
                    data.ping = (int) (System.currentTimeMillis() - data.serverKeepAlive);
                }
                if (p.isDead()) {
                    data.isDead = true;
                    data.wasDead = System.currentTimeMillis();
                } else {
                    if (data.isDead) {
                        data.wasDead = System.currentTimeMillis();
                    }
                    data.isDead = false;
                }


                if (event.getPacketId() == PacketType.Play.Client.USE_ENTITY) {
                    WrappedPacketInUseEntity ue = new WrappedPacketInUseEntity(packet);
                    data.target = ue.getEntity();
                    data.useAction = ue.getAction();
                    if (ue.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                        data.lastAttack = System.currentTimeMillis();
                    }
                }
                for (Check c : data.getCheckManager().checks) {
                    c.data = data;
                    c.onPacket(event);
                }
                if (event.getPacketId() == PacketType.Play.Client.BLOCK_DIG) {
                    WrappedPacketInBlockDig dig = new WrappedPacketInBlockDig(packet);
                    if (dig.getDigType().toString().contains("START_DESTROY_BLOCK")) {
                        data.sentStartDestroy = true;
                    }
                    if (dig.getDigType().toString().contains("ABORT_DESTROY_BLOCK")) {
                        data.sentStartDestroy = false;
                    }
                    if (dig.getDigType().toString().contains("STOP_DESTROY_BLOCK")) {
                        if (data.sentStartDestroy) {
                            if (dig.getDirection().toString().contains("UP")) {
                                data.brokeBlock = System.currentTimeMillis();
                            }
                        }
                        data.sentStartDestroy = false;
                    }
                }
                if (event.getPacketId() == PacketType.Play.Client.POSITION || event.getPacketId() == PacketType.Play.Client.POSITION_LOOK) {
                    WrappedPacketInFlying ps = new WrappedPacketInFlying(packet);
                    Location from = new Location(p.getWorld(), ps.getPosition().getX(), ps.getPosition().getY(), ps.getPosition().getZ());

                    if (data.to != null) {
                        if (PacketEvents.get().getPlayerUtils().getClientVersion(p).isNewerThanOrEquals(ClientVersion.v_1_17) && ps.isPosition() && ps.isLook() && sameLocation(data, ps)) {
                            data.noCheckNextFlying = true;
                        }
                    }

                    if (!data.noCheckNextFlying) {
                        if (ps.isRotating()) {
                            from.setYaw(ps.getYaw());
                            from.setPitch(ps.getPitch());
                        } else {
                            from.setYaw(p.getLocation().getYaw());
                            from.setPitch(p.getLocation().getPitch());
                        }

                        if (data.teleports.size() > 0) {
                            data.teleportTickFix = 2;
                            data.isTeleporting = true;
                        }

                        if (data.teleports.size() == 0) {
                            if (--data.teleportTickFix < 0) {
                                data.isTeleporting = false;
                            }
                        }
                        if (data.isTeleporting) {
                            data.ticks++;
                        } else {
                            data.ticks = 0;
                        }

                        data.lastLastPlayerGround = data.lastPlayerGround;
                        data.lastPlayerGround = data.playerGround;
                        data.playerGround = ps.isOnGround();
                        data.serverGround = from.clone().getY() % 0.015625 == 0.0;
                        data.from = data.to;
                        data.to = from;
                        data.sFrom = data.sTo;
                        data.sTo = from;
                        data.doMove();
                        if (ps.isRotating()) {
                            float yawAccelAccel = Math.abs(data.lastAccelYaw - Math.abs(Math.abs(data.deltaYaw) - Math.abs(data.lastDeltaYaw)));
                            float pitchAccelAccel = Math.abs(data.lastAccelPitch - Math.abs(Math.abs(data.deltaPitch) - Math.abs(data.lastDeltaPitch)));
                            if ((String.valueOf(yawAccelAccel).contains("E") || String.valueOf(pitchAccelAccel).contains("E")) && data.sensitivity < 100) {
                                data.cinematicTicks += 3;
                            } else if (yawAccelAccel < .06 && yawAccelAccel > 0 || pitchAccelAccel < .06 && pitchAccelAccel > 0) {
                                data.cinematicTicks += 1;
                            } else if (data.cinematicTicks > 0) data.cinematicTicks--;
                            if (data.cinematicTicks > 20) data.cinematicTicks--;
                            data.isCinematic = data.cinematicTicks > 8 || System.currentTimeMillis() - data.lastCinematic < 250;
                            if (data.cinematicTicks > 8 && data.isCinematic) data.lastCinematic = System.currentTimeMillis();
                            data.lastAccelPitch = Math.abs(Math.abs(data.deltaPitch) - Math.abs(data.lastDeltaPitch));
                            data.lastAccelYaw = Math.abs(Math.abs(data.deltaYaw) - Math.abs(data.lastDeltaYaw));
                            if (Math.abs(data.deltaPitch) > 0 && Math.abs(data.deltaPitch) < 30) {
                                float gcd = (float) data.getGCD(Math.abs(data.deltaPitch), Math.abs(data.lastDeltaPitch));
                                double modifier = Math.cbrt(0.8333 * gcd);
                                double nextStep = (modifier / .6) - 0.3333;
                                double lastStep = nextStep * 200;
                                data.sensitivitySamples.add((int) lastStep);
                                if (data.sensitivitySamples.size() >= 20) {
                                    data.sensitivity = getMode(data.sensitivitySamples);
                                    float gcdOne = (data.sensitivity / 200F) * 0.6F + 0.2F;
                                    data.realGCD = gcdOne * gcdOne * gcdOne * 1.2F;
                                    data.sensitivitySamples.clear();
                                }
                            }
                        }
                        handleBlocks(data);
                        if (data.onSlime) {
                            data.sinceSlimeTicks = 0;
                        } else {
                            data.sinceSlimeTicks++;
                        }
                        if (data.getPlayer().isFlying()) {
                            data.lastFlyingTime = System.currentTimeMillis();
                        }
                        if (data.teleports.size() > 150) {
                            data.teleports.remove(0);
                        }
                        if (data.ticks > 50) {
                            data.teleports.clear();
                        }
                        if (!data.inAnimation) {
                            try {
                                for (Vector vector : data.teleports) {
                                    final double dx = Math.abs(from.getX() - vector.getX());
                                    final double dy = Math.abs(from.getY() - vector.getY());
                                    final double dz = Math.abs(from.getZ() - vector.getZ());

                                    if (dx == 0 && dy == 0 && dz == 0) {
                                        data.teleports.remove(vector);
                                    }
                                }
                            } catch (ConcurrentModificationException ignored) {
                            }
                        } else {
                            data.teleports.clear();
                            data.isTeleporting = true;
                        }

                        for (Check c : data.getCheckManager().checks) {
                            c.data = data;
                            c.onMove(event, data.motionX, data.motionY, data.motionZ, data.lastMotionX, data.lastMotionY, data.lastMotionZ, data.deltaYaw, data.deltaPitch, data.lastDeltaYaw, data.lastDeltaPitch);
                        }
                    }
                    data.noCheckNextFlying = false;
                    data.lastHealth = p.getHealth();
                }
                data.lastFallDistance = p.getFallDistance();
            }
        } else Data.addPlayerData(p);
    }

    @Override
    public void onPacketPlaySend(PacketPlaySendEvent event) {
        Player p = event.getPlayer();
        if(p != null) {
            Data.addPlayerData(p);
            PlayerData data = Data.getPlayerData(p);
            if(data != null) {
                if(event.getPacketId() == PacketType.Play.Server.ANIMATION) {
                    WrappedPacketOutAnimation animation = new WrappedPacketOutAnimation(event.getNMSPacket());

                    if(animation.getAnimationType() == WrappedPacketOutAnimation.EntityAnimationType.TAKE_DAMAGE) {
                        data.lastTakeDamage = System.currentTimeMillis();
                    }
                }
                if(event.getPacketId() == PacketType.Play.Server.KEEP_ALIVE) {

                    data.serverKeepAlive = System.currentTimeMillis();
                }
                if(event.getPacketId() == PacketType.Play.Server.POSITION) {
                    WrappedPacketOutPosition ps = new WrappedPacketOutPosition(event.getNMSPacket());
                    final Vector teleportVector = new Vector(
                            ps.getPosition().getX(),
                            ps.getPosition().getY(),
                            ps.getPosition().getZ()
                    );
                    data.teleports.add(teleportVector);
                }
                if(event.getPacketId() == PacketType.Play.Server.ENTITY_VELOCITY) {
                    WrappedPacketOutEntityVelocity velo = new WrappedPacketOutEntityVelocity(event.getNMSPacket());
                    if(velo.getEntityId() == p.getEntityId())
                        data.lastVelocity = System.currentTimeMillis();
                }
                for (Check c : data.getCheckManager().checks) {
                    c.data = data;
                    c.onPacketSend(event);
                }
            }
        }
    }

    private void handleBlocks(PlayerData data) {
        final BoundingBox boundingBox = new BoundingBox(data.getPlayer()).expandOther(0, 0, 0.55, 0.6, 0, 0);
        final double minX = boundingBox.getMinX();
        final double minY = boundingBox.getMinY();
        final double minZ = boundingBox.getMinZ();
        final double maxX = boundingBox.getMaxX();
        final double maxY = boundingBox.getMaxY();
        final double maxZ = boundingBox.getMaxZ();
        List<Block> b = new ArrayList<>();
        for (double x = minX; x <= maxX; x += (maxX - minX)) {
            for (double y = minY; y <= maxY + 0.01; y += (maxY - minY) / 5) {
                for (double z = minZ; z <= maxZ; z += (maxZ - minZ)) {
                    final Location location = new Location(data.getPlayer().getWorld(), x, y, z);
                    final Block block = this.getBlock(location);
                    b.add(block);
                }
            }
        }
        final BoundingBox boundingBox2 = new BoundingBox(data.getPlayer()).expandOther(0, 0, 0.01, 0, 0, 0);

        final double minX2 = boundingBox2.getMinX();
        final double minY2 = boundingBox2.getMinY();
        final double minZ2 = boundingBox2.getMinZ();
        final double maxX2 = boundingBox2.getMaxX();
        final double maxY2 = boundingBox2.getMaxY();
        final double maxZ2 = boundingBox2.getMaxZ();
        List<Block> b3 = new ArrayList<>();
        for (double x = minX2; x <= maxX2; x += (maxX2 - minX2)) {
            for (double y = minY2; y <= maxY2 + 0.01; y += (maxY2 - minY2) / 5) {
                for (double z = minZ2; z <= maxZ2; z += (maxZ2 - minZ2)) {
                    final Location location = new Location(data.getPlayer().getWorld(), x, y, z);
                    final Block block = this.getBlock(location);
                    b3.add(block);
                }
            }
        }

        BoundingBox boundingBox3 = new BoundingBox(data.getPlayer()).expandOther(0, 0, 0.01, -1.8, 0, 0);

        double minX3 = boundingBox3.getMinX();
        double minY3 = boundingBox3.getMinY();
        double minZ3 = boundingBox3.getMinZ();
        double maxX3 = boundingBox3.getMaxX();
        double maxY3 = boundingBox3.getMaxY();
        double maxZ3 = boundingBox3.getMaxZ();
        List<Block> b4 = new ArrayList<>();
        for (double x = minX3; x <= maxX3; x += (maxX3 - minX3)) {
            for (double y = minY3; y <= maxY3 + 0.01; y += (maxY3 - minY3) / 5) {
                for (double z = minZ3; z <= maxZ3; z += (maxZ3 - minZ3)) {
                    final Location location = new Location(data.getPlayer().getWorld(), x, y, z);
                    final Block block = this.getBlock(location);
                    b4.add(block);
                }
            }
        }
        boundingBox3 = boundingBox3.expandOther(0, 0, 0.05, -0.01, 0, 0);
        minX3 = boundingBox3.getMinX();
        minY3 = boundingBox3.getMinY();
        minZ3 = boundingBox3.getMinZ();
        maxX3 = boundingBox3.getMaxX();
        maxY3 = boundingBox3.getMaxY();
        maxZ3 = boundingBox3.getMaxZ();
        List<Block> b5 = new ArrayList<>();
        for (double x = minX3; x <= maxX3; x += (maxX3 - minX3)) {
            for (double y = minY3; y <= maxY3 + 0.01; y += (maxY3 - minY3) / 5) {
                for (double z = minZ3; z <= maxZ3; z += (maxZ3 - minZ3)) {
                    final Location location = new Location(data.getPlayer().getWorld(), x, y, z);
                    final Block block = this.getBlock(location);
                    b5.add(block);
                }
            }
        }



        data.isInLiquid = b.stream().anyMatch(Block::isLiquid);
        data.isInFullLiquid = b4.stream().allMatch(Block::isLiquid);
        data.aboveLiquid = b5.stream().allMatch(Block::isLiquid);
        data.inWeb = b3.stream().anyMatch(block -> block.getType().toString().contains("WEB"));
        data.inAir = b.stream().allMatch(block -> block.getType() == Material.AIR);
        data.onIce = b.stream().anyMatch(block -> block.getType().toString().contains("ICE"));
        if(data.onIce) data.lastIce = System.currentTimeMillis();
        data.onSolidGround = b.stream().anyMatch(block -> block.getType().isSolid());
        data.isOnSlab = b.stream().anyMatch(block -> block.getType().toString().contains("STEP") || block.getType().toString().contains("SLAB"));
        data.isOnStair = b.stream().anyMatch(block -> block.getType().toString().contains("STAIR"));
        data.nearTrapdoor = this.isCollidingAtLocation(data,1.801, material -> material.toString().contains("TRAP_DOOR"));
        data.nearTrapdoor = b.stream().anyMatch(block -> block.getType().toString().contains("TRAPDOOR"));
        data.blockAbove = b.stream().filter(block -> block.getLocation().getY() - data.to.getY() > 1.5)
                .anyMatch(block -> block.getType() != Material.AIR) || data.nearTrapdoor;
        if(data.blockAbove) data.lastBlockAbove = System.currentTimeMillis();
        data.blockAboveWater = b.stream().filter(block -> block.getLocation().getY() - data.to.getY() > 1.5)
                .allMatch(block -> block.isLiquid() || block.getType() == Material.AIR) || data.nearTrapdoor;
        data.onSlime = b.stream().anyMatch(block -> block.getType().toString().equalsIgnoreCase("SLIME_BLOCK"));
        data.onSoulSand = b.stream().anyMatch(block -> block.getType().toString().contains("SOUL"));
        data.onCake = b.stream().anyMatch(block -> block.getType().toString().contains("CAKE"));
        data.nearBerry = b.stream().anyMatch(block -> block.getType().toString().contains("BUSH"));
        data.nearPiston = b.stream().anyMatch(block -> block.getType().toString().contains("PISTON"));
        data.onLowBlock = b.stream().anyMatch(block -> block.getType().toString().contains("TRAP_DOOR") || block.getType().toString().contains("BED") || block.getType().toString().contains("CARPET") || block.getType().toString().contains("REPEATER") || block.getType().toString().contains("COMPARATOR") || block.getType().toString().contains("SLAB") || block.getType().toString().contains("SNOW") || block.getType().toString().contains("CAULDRON") || block.getType().toString().contains("BREWING") || block.getType().toString().contains("HOPPER") || block.getType().toString().contains("DETECTOR") || block.getType().toString().contains("ENCHANTING") || block.getType().toString().contains("END_PORTAL") || block.getType().toString().contains("POT") || block.getType().toString().contains("SOUL") || block.getType().toString().contains("STAIRS") || block.getType().toString().contains("SLAB") || block.getType().toString().contains("CAKE") || block.getType().toString().contains("STEP")  || block.getType().toString().contains("BED")  || block.getType().toString().contains("HEAD")  || block.getType().toString().contains("FENCE")  || block.getType().toString().contains("WALL")  || block.getType().toString().contains("PISTON")  || block.getType().toString().contains("SLIME") || block.getType().toString().contains("CANDLE"));
        if(data.onLowBlock) data.lastLowBlock = System.currentTimeMillis();
        if(data.onSoulSand) data.lastOnSoulSand = System.currentTimeMillis();
        if(data.nearBerry) data.lastNearBerry = System.currentTimeMillis();
        if(System.currentTimeMillis() - data.lastLowBlock < 600) data.onLowBlock = true;
        if(System.currentTimeMillis() - data.lastOnSoulSand < 600) data.onSoulSand = true;
        if(System.currentTimeMillis() - data.lastNearBerry < 300) data.nearBerry = true;
        final Location location = data.getPlayer().getLocation();
        final int var1 = NumberConversions.floor(location.getX());
        final int var2 = NumberConversions.floor(location.getY());
        final int var3 = NumberConversions.floor(location.getZ());
        final Block var4 = this.getBlock(new Location(location.getWorld(), var1, var2, var3));

        BoundingBox box = new BoundingBox(var4);
        final BoundingBox bb = new BoundingBox(data.getPlayer())
                .expandOther(0.2, 0.2, 0, 0, 0.2, 0.2);
        box.expand(Math.abs(data.motionX) + 0.14, 0,
                Math.abs(data.motionZ) + 0.14);
        final double mx = bb.getMinX();
        final double my = bb.getMinY();
        final double mz = bb.getMinZ();
        final double max = bb.getMaxX();
        final double may = bb.getMaxY();
        final double maz = bb.getMaxZ();
        List<Block> b2 = new ArrayList<>();
        for (double x = mx; x <= max; x += (max - mx)) {
            for (double y = my; y <= may + 0.01; y += (may - my) / 5) { //Expand max by 0.01 to compensate shortly for precision issues due to FP.
                for (double z = mz; z <= maz; z += (maz - mz)) {
                    final Location loc = new Location(data.getPlayer().getWorld(), x, y, z);
                    final Block block = this.getBlock(loc);
                    b2.add(block);
                }
            }
        }
        if (!b2.stream().allMatch(block -> block.getType().toString().contains("AIR")))
            data.collidesHorizontally = true;
        else
            data.collidesHorizontally = false;
        data.onClimbable = var4.getType() == Material.LADDER || var4.getType() == Material.VINE;
    }

    public boolean isCollidingAtLocation(PlayerData data,double drop, Predicate<Material> predicate) {
        final ArrayList<Material> materials = new ArrayList<>();

        for (double x = -0.3; x <= 0.3; x += 0.3) {
            for (double z = -0.3; z <= 0.3; z+= 0.3) {
                final Material material = getBlock(data.getPlayer().getLocation().clone().add(x, drop, z)).getType();
                if (material != null) {
                    materials.add(material);
                }
            }
        }

        return materials.stream().allMatch(predicate);
    }

    public int getMode(Collection<? extends Number> list) {
        int mode = (int) list.toArray()[0];
        int maxCount = 0;
        for (Number value : list) {
            int count = 1;
            for (Number value2 : list) {
                if (value2.equals(value))
                    count++;
                if (count > maxCount) {
                    mode = (int) value;
                    maxCount = count;
                }
            }
        }
        return mode;
    }

    //Taken from Fiona
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

    public boolean sameLocation(PlayerData data, WrappedPacketInFlying flying) {
        return data.to.getX() == flying.getPosition().getX() && data.to.getY() == flying.getPosition().getY() && data.to.getZ() == flying.getPosition().getZ();
    }
}

