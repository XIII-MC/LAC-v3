package com.xiii.libertycity.lac.exempt;

import com.xiii.libertycity.lac.data.PlayerData;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import lombok.Getter;
import org.bukkit.GameMode;

import java.util.function.Function;

@Getter
public enum ExemptType {
    TELEPORT(data -> data.isTeleporting || System.currentTimeMillis() - data.joined < 2000L),
    AIR(data -> data.inAir),
    GROUND(data -> data.onSolidGround && data.to.getY() % 0.015625 == 0.0),
    PACKET_GROUND(data -> data.playerGround),
    TPS(data -> data.getTPS() > 18.5D),
    JOINED(data -> System.currentTimeMillis() - data.joined < 5000L),
    TRAPDOOR(data -> data.nearTrapdoor),
    CHUNK(data -> !data.getPlayer().getWorld().isChunkLoaded(data.getPlayer().getLocation().getBlockX() << 4, data.getPlayer().getLocation().getBlockZ() << 4)),
    STEPPED(data -> data.playerGround && data.motionY > 0),
    SLAB(data -> data.isOnSlab),
    STAIRS(data -> data.isOnStair),
    WEB(data -> data.inWeb),
    CLIMBABLE(data -> data.onClimbable),
    SLIME(data -> data.sinceSlimeTicks < 30),
    NEAR_VEHICLE(data -> data.nearBoat),
    INSIDE_VEHICLE(data -> System.currentTimeMillis() - data.lastNearBoat < 20),
    LIQUID(data -> data.isInLiquid),
    FULL_LIQUID(data -> data.isInFullLiquid),
    BLOCK_ABOVE(data -> data.blockAbove),
    PISTON(data -> data.nearPiston),
    VOID(data -> data.getPlayer().getLocation().getY() < 4),
    DEPTH_STRIDER(data -> data.getDepthStriderLevel() > 0),
    SPECTATE(data -> data.player.getGameMode().equals(GameMode.SPECTATOR)),
    FLYING(data -> System.currentTimeMillis() - data.lastFlyingTime < 3000L),
    VELOCITY(data -> System.currentTimeMillis() - data.entityHit < 800L || System.currentTimeMillis() - data.lastVelocity < 800L),
    HEALTH_CHANGE(data -> data.lastHealth > data.getPlayer().getHealth()),
    GLIDE(data -> System.currentTimeMillis() - data.lastGlide < 4000),
    ICE(data -> System.currentTimeMillis() - data.lastIce < 2000),
    RESPAWN(data -> System.currentTimeMillis() - data.wasDead < 300L),
    SOULSAND(data -> data.onSoulSand),
    PLACE(data -> System.currentTimeMillis() - data.lastBlockPlaced < 600L);

    private final Function<PlayerData, Boolean> exception;

    ExemptType(final Function<PlayerData, Boolean> exception) {
        this.exception = exception;
    }

}
