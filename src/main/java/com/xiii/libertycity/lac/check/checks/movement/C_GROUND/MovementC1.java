package com.xiii.libertycity.lac.check.checks.movement.C_GROUND;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import com.xiii.libertycity.lac.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@CheckInfo(name = "Movement:C-1", category = Category.MOVEMENT, state = CheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 1, maxBuffer = 1, punishVL = 6, silent = false, kickable = true, bannable = false)
public class MovementC1 extends Check {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        final boolean exempt = isExempt(ExemptType.NEAR_VEHICLE, ExemptType.SLIME, ExemptType.TELEPORT, ExemptType.CLIMBABLE, ExemptType.JOINED, ExemptType.STAIRS, ExemptType.SPECTATE, ExemptType.FLYING);

        if(!exempt && data.serverGround != data.playerGround) fail(packet, "Change l'état du sol", "s=§a" + data.serverGround + "\n" + " §8»§f c=§a" + data.playerGround); else removeBuffer();
    }

}
