package com.xiii.libertycity.lac.check.checks.movement.A_FLY;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import com.xiii.libertycity.lac.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@CheckInfo(name = "Movement:A-1", category = Category.MOVEMENT, state = CheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 0, maxBuffer = 3, punishVL = 12, silent = false, kickable = true, bannable = false)
public class MovementA1 extends Check {

    @Override
    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        final boolean exempt = isExempt(ExemptType.FLYING, ExemptType.LIQUID, ExemptType.CLIMBABLE, ExemptType.TELEPORT, ExemptType.GLIDE, ExemptType.NEAR_VEHICLE, ExemptType.PLACE, ExemptType.WEB, ExemptType.TRAPDOOR, ExemptType.VOID);

        final double predictedMotionY = (lastMotionY - 0.08D) * (double)0.98F;

        if(!exempt && !data.onCake && !data.playerGround && (motionY - predictedMotionY > 0.1E-12)) fail(packet, "Ne suis pas les predictions", "predictedMotionY §c" + predictedMotionY + "\n" + " §8»§f motionY §c" + motionY); else if(data.playerGround && data.serverGround) removeBuffer();

    }
}
