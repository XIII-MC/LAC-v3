package com.xiii.libertycity.lac.check.checks.movement.B_SPEED;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import com.xiii.libertycity.lac.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import org.bukkit.Bukkit;

@CheckInfo(name = "Movement:B-4", category = Category.MOVEMENT, state = CheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 1, maxBuffer = 2, punishVL = 6, silent = false, kickable = true, bannable = false)
public class MovementB4 extends Check {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastmotionX, double lastmotionY, double lastmotionZ, float deltaYaw, float deltaPitch, float lastdeltaYaw, float lastdeltaPitch) {

        final boolean exempt = isExempt(ExemptType.FLYING, ExemptType.TELEPORT, ExemptType.VELOCITY);

        final double predictedMotionXZ = data.getLastDeltaXZ() * (double) 0.91F + (data.player.isSprinting() ? 0.026 : 0.02);
        final double difference = data.getDeltaXZ() - predictedMotionXZ;
        final double threshold = data.playerGround ? 0.08 : 0.006;

        if(difference > threshold && !exempt) fail(packet, "Ne suis pas les predictions", "predictedMotionXZ §c" + predictedMotionXZ + "\n" + " §8»§f motionXZ §c" + data.getDeltaXZ() + "\n" + " §8»§f difference §c" + difference);
        else removeBuffer();
    }

}
