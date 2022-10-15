package com.xiii.libertycity.lac.check.checks.movement.E_STEP;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import com.xiii.libertycity.lac.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@CheckInfo(name = "Movement:E-3", category = Category.MOVEMENT, state = CheckState.EXPERIMENTAL, addBuffer = 0, removeBuffer = 0, maxBuffer = 0, punishVL = 12, silent = false, kickable = true, bannable = false)
public class MovementE3 extends Check {

    int internalBuffer;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        final boolean exempt = isExempt(ExemptType.STAIRS, ExemptType.GLIDE, ExemptType.SLAB, ExemptType.SLIME, ExemptType.FLYING, ExemptType.NEAR_VEHICLE, ExemptType.TELEPORT);

        int maxTicks = isExempt(ExemptType.VELOCITY) ? 8 : 5;

        if(motionY > 0.117600002289 && !data.onLowBlock && !exempt) {
            internalBuffer++;
            if(internalBuffer > maxTicks) fail(packet, "Modifiction du temps en l'air", "airTicks §9" + internalBuffer + "§8/§c" + internalBuffer);
        }
        if(motionY <= 0.117600002289 && (!data.isInAir() || data.playerGround)) internalBuffer = 0;

    }

}
