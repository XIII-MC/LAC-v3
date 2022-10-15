package com.xiii.libertycity.lac.check.checks.combat.A_KILLAURA;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import com.xiii.libertycity.lac.exempt.ExemptType;
import com.xiii.libertycity.lac.utils.MathUtils;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@CheckInfo(name = "KillAura:A-4", category = Category.COMBAT, state = CheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 0.01, maxBuffer = 2, punishVL = 3, silent = false, kickable = true, bannable = false)
public class KillAuraA4 extends Check {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        if(deltaPitch != 0) {

            final boolean exempt = isExempt(ExemptType.TELEPORT);
            double cPitch = deltaPitch * MathUtils.EXPANDER;
            double cLastPitch = lastDeltaPitch * MathUtils.EXPANDER;
            final double GCD = MathUtils.getGcd(cPitch, cLastPitch);
            final double moduloGCD = Math.log(GCD % data.getPlayer().getLocation().getPitch());

            if(!exempt && Math.abs(moduloGCD) > 20) fail(null, "Rotation trop arrondis", "moduloGCD §9" + moduloGCD);
            else removeBuffer();
        }

    }

}
