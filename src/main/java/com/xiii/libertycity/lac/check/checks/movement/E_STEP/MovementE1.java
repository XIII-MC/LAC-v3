package com.xiii.libertycity.lac.check.checks.movement.E_STEP;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import com.xiii.libertycity.lac.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@CheckInfo(name = "Movement:E-1", category = Category.MOVEMENT, state = CheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 0, maxBuffer = 1, punishVL = 6, silent = false, kickable = true, bannable = false)
public class MovementE1 extends Check {

    int airTicks;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.BLOCK_ABOVE, ExemptType.CLIMBABLE, ExemptType.FLYING, ExemptType.GLIDE, ExemptType.PLACE, ExemptType.NEAR_VEHICLE, ExemptType.FLYING, ExemptType.PISTON, ExemptType.STAIRS, ExemptType.SLAB, ExemptType.WEB, ExemptType.VELOCITY);
        if(!exempt) {
            if (motionY > 0) {
                if (airTicks < 4) {
                    airTicks++;
                }
            } else {
                if (motionY == 0 && data.playerGround) {
                    if (airTicks < 4 && airTicks != 0 && airTicks > 0) {
                        if(!data.onLowBlock && !isExempt(ExemptType.STAIRS) && !isExempt(ExemptType.SLAB)) fail(packet, "Modification du temps en l'air", "airTicks §9" + airTicks);
                    } else {
                        if (airTicks != 0 && airTicks > 0)
                            removeBuffer();
                    }
                    airTicks = 0;
                }
            }
        }
        if(exempt || data.onLowBlock) {
            airTicks -= 2;
            removeBuffer();
        }

    }

}
