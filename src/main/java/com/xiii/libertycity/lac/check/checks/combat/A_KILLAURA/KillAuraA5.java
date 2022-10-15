package com.xiii.libertycity.lac.check.checks.combat.A_KILLAURA;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import com.xiii.libertycity.lac.exempt.ExemptType;
import com.xiii.libertycity.lac.utils.MathUtils;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@CheckInfo(name = "KillAura:A-5", category = Category.COMBAT, state = CheckState.EXPERIMENTAL, addBuffer = 0, removeBuffer = 0, maxBuffer = 0, punishVL = 12, silent = false, kickable = true, bannable = false)
public class KillAuraA5 extends Check {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        if(deltaPitch != 0) {

            double cPitch = deltaPitch * 16777216;
            double cLastPitch = lastDeltaPitch * 16777216;
            final double GCD = MathUtils.getGcd(cPitch, cLastPitch);
            final double moduloGCD = GCD % data.getPlayer().getLocation().getPitch();

            //if(String.valueOf(moduloGCD).length() <= 3 && !isExempt(ExemptType.TELEPORT)) fail(packet, "Rotation trop arrondis", "moduloGCD §9" + moduloGCD);
            //else removeBuffer();
        }
    }

}
