package com.xiii.libertycity.lac.check.checks.movement.B_SPEED;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import com.xiii.libertycity.lac.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import org.bukkit.Bukkit;

@CheckInfo(name = "Movement:B-1", category = Category.MOVEMENT, state = CheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 1, maxBuffer = 3, punishVL = 12, silent = false, kickable = true, bannable = false)
public class MovementB1 extends Check {

    @Override
    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        //boolean exempt = isExempt(ExemptType.FLYING, ExemptType.SLIME, ExemptType.VELOCITY, ExemptType.ICE);
        double[] values = data.predictionProcessor.predictUrAssOff();
        double threshold = (9.16E-4);
        //Bukkit.broadcastMessage("v=" + values[1] + "/" + threshold + " b=" + buffer);

        if(data.getDeltaXZ() >= values[0] && values[1] > threshold && data.getDeltaXZ() > 0) fail(packet, "Ne suis pas les predictions", "speed §9" + values[1]); else removeBuffer();

    }
}
