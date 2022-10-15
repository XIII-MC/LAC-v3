package com.xiii.libertycity.lac.check.checks.movement.F_MOTION;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import com.xiii.libertycity.lac.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@CheckInfo(name = "Movement:F-4", category = Category.MOVEMENT, state = CheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 0.22, maxBuffer = 3, punishVL = 12, silent = false, kickable = true, bannable = false)
public class MovementF4 extends Check {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        final boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.CLIMBABLE, ExemptType.STAIRS, ExemptType.SLAB, ExemptType.LIQUID, ExemptType.FULL_LIQUID, ExemptType.VELOCITY, ExemptType.FLYING);
        final double predictedMotion = Math.sqrt(Math.abs(motionY - lastMotionY));
        if(!data.nearBerry && !data.onLowBlock && !exempt && (motionY - predictedMotion < -0.6456 || (motionY - predictedMotion > 0 && motionY - predictedMotion != 0.03858160783192255)) && !data.inAir && data.onSolidGround && !data.nearTrapdoor) fail(packet, "Mouvement verticale répété","difference §9" + (motionY - predictedMotion));
        else removeBuffer();
        if(!data.inAir && motionY > -0.38 && motionY <= 0.42) debug("res=" + (motionY - predictedMotion) + " my=" + motionY + " g=" + data.playerGround + " sG=" + data.onSolidGround + " sG=" + data.serverGround + " iA=" + data.inAir);

    }

}
