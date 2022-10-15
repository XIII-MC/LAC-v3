package com.xiii.libertycity.lac.check.checks.movement.C_GROUND;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import com.xiii.libertycity.lac.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@CheckInfo(name = "Movement:C-2", category = Category.MOVEMENT, state = CheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 0.25, maxBuffer = 3, punishVL = 6, silent = false, kickable = true, bannable = false)
public class MovementC2 extends Check {

    double startY;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        final boolean exempt = isExempt(ExemptType.NEAR_VEHICLE, ExemptType.SLIME, ExemptType.TELEPORT, ExemptType.CLIMBABLE, ExemptType.JOINED, ExemptType.STAIRS, ExemptType.VELOCITY, ExemptType.FLYING);

        double predictedMotionY = (lastMotionY - 0.08D) * (double)0.98F;

        if(motionY >= 0 || !data.inAir) {
            startY = data.to.clone().getY();
        } else {
            if(Math.abs(data.player.getFallDistance() - Math.abs(data.to.clone().getY() - startY)) - Math.abs(predictedMotionY) > 1.4 && !exempt && !data.onLowBlock) {
                fail(packet, "Modifie sa distance de chute", "fallDistance §c" + (Math.abs(data.player.getFallDistance() - Math.abs(data.to.clone().getY() - startY)) - Math.abs(predictedMotionY)));
            } else removeBuffer();
        }
    }

}
