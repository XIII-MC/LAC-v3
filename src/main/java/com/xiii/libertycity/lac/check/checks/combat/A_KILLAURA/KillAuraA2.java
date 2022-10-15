package com.xiii.libertycity.lac.check.checks.combat.A_KILLAURA;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;

@CheckInfo(name = "KillAura:A-2", category = Category.COMBAT, state = CheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 0, maxBuffer = 6, punishVL = 3, silent = false, kickable = true, bannable = false)
public class KillAuraA2 extends Check {

    public double customGCD(float current, float previous) {
        if(current < previous) return customGCD(previous, current);
        if(Math.abs(previous) < 0.001) return current;
        if(Math.abs(previous) > 0.001) return customGCD(previous, (float) (current - Math.floor(current / previous) * previous));
        return 0;
    }

    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.USE_ENTITY) {
            final double GCD = customGCD(Math.abs(data.deltaPitch), Math.abs(data.lastDeltaPitch));
            if(GCD < 0.149999 && GCD != 0 && !data.isCinematic) fail(packet, "Rotation trop précise", "GCD §c" + GCD);
            if(GCD >= 0.149999 && GCD < 10) removeBuffer();
            if(GCD > 10 && !data.isCinematic) fail(packet, "Rotation trop soudaine", "GCD §c" + GCD);
        }
    }

}
