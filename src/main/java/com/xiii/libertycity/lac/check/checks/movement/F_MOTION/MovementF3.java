package com.xiii.libertycity.lac.check.checks.movement.F_MOTION;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import com.xiii.libertycity.lac.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@CheckInfo(name = "Movement:F-3", category = Category.MOVEMENT, state = CheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 0, maxBuffer = 5, punishVL = 12, silent = false, kickable = true, bannable = false)
public class MovementF3 extends Check {

    double speed;

    @Override
    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        final boolean exempt = isExempt(ExemptType.FLYING, ExemptType.INSIDE_VEHICLE, ExemptType.WEB, ExemptType.CLIMBABLE);

        speed = Math.sqrt(Math.pow(Math.abs(motionX), 2) + Math.pow(Math.abs(motionZ), 2));
        speed = Math.round(speed * 10000000);

        if(!exempt && String.valueOf(speed).contains("000")) fail(packet, "Mouvement arrondis", "speed §c" + speed);
        if(!String.valueOf(speed).contains("000")) removeBuffer();

    }
}
