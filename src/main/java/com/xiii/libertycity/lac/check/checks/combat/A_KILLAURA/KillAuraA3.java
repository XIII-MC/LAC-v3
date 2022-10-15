package com.xiii.libertycity.lac.check.checks.combat.A_KILLAURA;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;

@CheckInfo(name = "KillAura:A-3", category = Category.COMBAT, state = CheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 0, maxBuffer = 3, punishVL = 3, silent = false, kickable = true, bannable = false)
public class KillAuraA3 extends Check {

    public double customGCD(float current, float previous) {
        if(current < previous) return customGCD(previous, current);
        if(Math.abs(previous) < 0.001) return current;
        if(Math.abs(previous) > 0.001) return customGCD(previous, (float) (current - Math.floor(current / previous) * previous));
        return 0;
    }

    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.USE_ENTITY) {
            double GCD = customGCD(Math.abs(data.deltaPitch), Math.abs(data.lastDeltaPitch));
            if(String.valueOf(GCD).length() < 16 && GCD != 0) fail(packet, "Rotation impossiblement précise", "GCD §9" + GCD);
            if(String.valueOf(GCD).length() >= 16) removeBuffer();
        }
    }

}
