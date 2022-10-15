package com.xiii.libertycity.lac.check.checks.movement.B_SPEED;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import com.xiii.libertycity.lac.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Movement:B-3", category = Category.MOVEMENT, state = CheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 1, maxBuffer = 6, punishVL = 12, silent = false, kickable = true, bannable = false)
public class MovementB3 extends Check {

    double maxSpeed = 1;
    double cSpeed = 0;
    int groundTicks = 0;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        final boolean exempt = isExempt(ExemptType.FLYING, ExemptType.TELEPORT);

        cSpeed = data.getDistance(true);

        if(data.playerGround) groundTicks++;
        else groundTicks = 0;

        // BASIC | Ground - Air
        if(groundTicks > 12) {
            maxSpeed = 0.2868198;
        } else maxSpeed = 0.3358;

        // BASIC | HitHead
        if(System.currentTimeMillis() - data.lastBlockAbove < 1200) maxSpeed = 0.5;

        // ICE | Ground - Air
        if(System.currentTimeMillis() - data.lastIce <  1200) {
            if (groundTicks > 22) {
                if(System.currentTimeMillis() - data.lastIce < 50) maxSpeed = 0.2757;
            } else {
                maxSpeed = 0.48;
                if(System.currentTimeMillis() - data.lastBlockAbove < 1200) maxSpeed = 0.9;
            }
        }

        // SLIME | Ground - Air
        if(isExempt(ExemptType.SLIME)) {
            if(groundTicks > 14) {
                if(System.currentTimeMillis() - data.lastSlime < 50) maxSpeed = 0.09;
            } else {
                maxSpeed = 0.45;
                if(System.currentTimeMillis() - data.lastBlockAbove < 1200) maxSpeed = 0.74;
            }
        }

        // COBWEB | Ground - Air
        if(isExempt(ExemptType.WEB)) {
            if(groundTicks > 2) {
                maxSpeed = 0.1;
            } else {
                maxSpeed = 0.1004;
                if(data.blockAbove) maxSpeed = 0.108;
            }
        }

        // STAIRS/SLABS | Ground - Air
        if(isExempt(ExemptType.STAIRS) || isExempt(ExemptType.SLAB)) maxSpeed = 0.4;

        // WATER | Ground - Air
        if(data.isInLiquid && !data.playerGround) {
            maxSpeed = 0.2;
            if(data.getDepthStriderLevel() > 0) {
                if(data.getDepthStriderLevel() == 1) maxSpeed += 0.03;
                if(data.getDepthStriderLevel() == 2) maxSpeed += 0.05;
                if(data.getDepthStriderLevel() == 3) maxSpeed += 0.07;
                if(data.getDepthStriderLevel() > 3) maxSpeed = (0.08 * data.getDepthStriderLevel());
            }
        }

        // SPEED | Ground - Air
        if(data.player.hasPotionEffect(PotionEffectType.SPEED)) {
            if(groundTicks > 2) {
                maxSpeed += (data.getPotionEffectAmplifier(PotionEffectType.SPEED) * .0573);
            } else {
                maxSpeed += (data.getPotionEffectAmplifier(PotionEffectType.SPEED) * .02313);
            }
        }

        // DAMAGE | Air
        if(isExempt(ExemptType.VELOCITY)) {
            maxSpeed = 0.8;
            if(data.kbLevel > 0) {
                maxSpeed = (data.kbLevel * 0.95);
            }
        }

        // /SPEED | Ground - Air
        maxSpeed += data.getPlayer().getWalkSpeed() > 0.2f ? (double) data.getPlayer().getWalkSpeed() : 0;

        debug("speed=" + data.getPlayer().getWalkSpeed());
        debug("cs=" + cSpeed + " ms="+ maxSpeed + " b=" + buffer);

        if(cSpeed > maxSpeed  && !exempt) fail(packet, "Bouge trop vite par rapport à sa vitesse maximale attribué", "speed §9" + cSpeed + "§8/§c" + maxSpeed); else if(cSpeed <= maxSpeed) removeBuffer();
    }

}
