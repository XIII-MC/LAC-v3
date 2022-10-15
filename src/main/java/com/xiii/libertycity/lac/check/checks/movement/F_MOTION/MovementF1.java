package com.xiii.libertycity.lac.check.checks.movement.F_MOTION;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import com.xiii.libertycity.lac.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Movement:F-1", category = Category.MOVEMENT, state = CheckState.EXPERIMENTAL, addBuffer = 0, removeBuffer = 0, maxBuffer = 0, punishVL = 3, silent = false, kickable = true, bannable = false)
public class MovementF1 extends Check {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        final boolean exempt = isExempt(ExemptType.CLIMBABLE, ExemptType.FLYING, ExemptType.SLIME, ExemptType.BLOCK_ABOVE, ExemptType.PISTON, ExemptType.LIQUID, ExemptType.NEAR_VEHICLE, ExemptType.TELEPORT, ExemptType.WEB, ExemptType.TRAPDOOR);

        boolean step = mathOnGround(motionY) && mathOnGround(data.from.getY());
        boolean jumped = motionY > 0 && data.from.getY() % (1D/64) == 0 && !data.playerGround && !step;
        double expectedJumpMotion = isExempt(ExemptType.VELOCITY) ? 0.36075F : 0.42F + (double)(data.player.hasPotionEffect(PotionEffectType.JUMP) ? (data.player.getPotionEffect(PotionEffectType.JUMP).getAmplifier() + 1) * 0.1F : 0);

        if (jumped && !exempt && (motionY < expectedJumpMotion)) fail(packet, "Saut impossible", "expectedJumpMotion §c" + expectedJumpMotion + "\n" + " §8»§f motionY §c" + motionY);
    }

}
