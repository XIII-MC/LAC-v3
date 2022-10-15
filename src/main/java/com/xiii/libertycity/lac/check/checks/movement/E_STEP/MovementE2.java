package com.xiii.libertycity.lac.check.checks.movement.E_STEP;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import com.xiii.libertycity.lac.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@CheckInfo(name = "Movement:E-2", category = Category.MOVEMENT, state = CheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 1, maxBuffer = 4, punishVL = 6, silent = false, kickable = true, bannable = false)
public class MovementE2 extends Check {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        final boolean exempt = isExempt(ExemptType.FLYING, ExemptType.SLIME, ExemptType.TELEPORT, ExemptType.JOINED, ExemptType.NEAR_VEHICLE, ExemptType.CLIMBABLE, ExemptType.LIQUID, ExemptType.STAIRS, ExemptType.SLAB, ExemptType.WEB);

        if(lastMotionY - motionY < 0.01)  {
            if(!exempt && motionY != 0 && !data.onLowBlock && motionY > -3.45) fail(packet, "Movement verticale répété", "lastMotionY §c" + lastMotionY + "\n" + " §8»§f motionY §c" + motionY);
        }
        if(lastMotionY - motionY > 0.01) removeBuffer();
    }

}
