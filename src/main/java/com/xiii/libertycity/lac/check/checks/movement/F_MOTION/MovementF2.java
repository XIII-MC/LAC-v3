package com.xiii.libertycity.lac.check.checks.movement.F_MOTION;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import com.xiii.libertycity.lac.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@CheckInfo(name = "Movement:F-2", category = Category.MOVEMENT, state = CheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 1, maxBuffer = 3.5, punishVL = 12, silent = false, kickable = true, bannable = false)
public class MovementF2 extends Check {

    int airTicks;

    @Override
    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        final boolean exempt = isExempt(ExemptType.GLIDE, ExemptType.FLYING, ExemptType.TELEPORT, ExemptType.JOINED, ExemptType.PLACE, ExemptType.NEAR_VEHICLE, ExemptType.PISTON, ExemptType.SLIME, ExemptType.ICE, ExemptType.BLOCK_ABOVE, ExemptType.VELOCITY);

        if (!exempt && !data.nearEntity) {
            if (!data.playerGround) airTicks++;
            else airTicks = 0;
            if (airTicks > 3) {

                final double strafeValue = ((Math.pow((motionX + motionZ), 8)) - (Math.pow(lastMotionX + lastMotionZ, 8)) * 0.91);

                //if (strafeValue > 0.000086 || strafeValue < -0.00086) fail(packet, "Se déplace en l'air", "strafeValue §c" + strafeValue); else removeBuffer();
            }
        }

    }
}
