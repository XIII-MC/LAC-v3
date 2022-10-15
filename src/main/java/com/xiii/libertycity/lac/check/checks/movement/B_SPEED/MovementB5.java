package com.xiii.libertycity.lac.check.checks.movement.B_SPEED;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import com.xiii.libertycity.lac.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import org.bukkit.attribute.Attribute;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Movement:B-5", category = Category.MOVEMENT, state = CheckState.EXPERIMENTAL, addBuffer = 0, removeBuffer = 0, maxBuffer = 0, punishVL = 12, silent = false, kickable = true, bannable = false)
public class MovementB5 extends Check {

    int invalidA;
    double maxSpeed;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.JOINED, ExemptType.FLYING, ExemptType.INSIDE_VEHICLE);
        boolean step = mathOnGround(motionY) && mathOnGround(data.from.getY());
        if(data.playerGround) invalidA++;
        if(!data.playerGround) invalidA = 0;
        if(invalidA >= 8) maxSpeed = 0.2898 + (data.getPotionEffectAmplifier(PotionEffectType.SPEED) > 0 ? (data.getPotionEffectAmplifier(PotionEffectType.SPEED) * 0.0573) : 0);;
        if(invalidA < 8) maxSpeed = 0.628 + (data.getPotionEffectAmplifier(PotionEffectType.SPEED) > 0 ? (data.getPotionEffectAmplifier(PotionEffectType.SPEED) * .02313 + 0.2) : 0);
        if(step && (isExempt(ExemptType.STAIRS) || isExempt(ExemptType.SLAB))) maxSpeed += 0.2;
        if(isExempt(ExemptType.VELOCITY)) {
            maxSpeed += data.kbLevel;
            maxSpeed += 0.45;
        }
        if(data.player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() != 0.10000000149011612) {
            double timesModified = data.player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() / 0.10000000149011612;
            timesModified = Math.ceil(timesModified * 100.0) / 100.0;
        }
        if(System.currentTimeMillis() - data.lastIce < 1800) maxSpeed += 0.35;
        if(isExempt(ExemptType.SLIME)) maxSpeed += 0.3;
        if(isExempt(ExemptType.BLOCK_ABOVE))
            maxSpeed += 0.5;
        maxSpeed += data.getPlayer().getWalkSpeed() > 0.2f ? (double) data.getPlayer().getWalkSpeed() : 0;
        if(data.getDeltaXZ() >= maxSpeed && !exempt) fail(packet, "Mouvement de téléportation impossible", "speed §c" + data.getDeltaXZ() + "§8/§c" + maxSpeed); else removeBuffer();
    }

}
