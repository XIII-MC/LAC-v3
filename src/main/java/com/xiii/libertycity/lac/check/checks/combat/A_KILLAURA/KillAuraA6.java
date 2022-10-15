package com.xiii.libertycity.lac.check.checks.combat.A_KILLAURA;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@CheckInfo(name = "KillAura:A-6", category = Category.COMBAT, state = CheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 0, maxBuffer = 4, punishVL = 12, silent = false, kickable = true, bannable = false)
public class KillAuraA6 extends Check {

    public double customGCD(float current, float previous) {
        if(current < previous) return customGCD(previous, current);
        if(Math.abs(previous) < 0.001) return current;
        if(Math.abs(previous) > 0.001) return customGCD(previous, (float) (current - Math.floor(current / previous) * previous));
        return 0;
    }

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        if(deltaPitch != 0) {
            final double GCD = customGCD(Math.abs(deltaPitch), Math.abs(lastDeltaPitch));
            if(GCD < 0.054 && !data.isCinematic) fail(packet, "Ne suis pas un GCD valide", "GCD §c" + GCD);
            else removeBuffer();
        }

    }

}
