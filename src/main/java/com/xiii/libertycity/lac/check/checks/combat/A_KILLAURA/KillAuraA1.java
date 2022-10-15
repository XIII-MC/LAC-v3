package com.xiii.libertycity.lac.check.checks.combat.A_KILLAURA;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

@CheckInfo(name = "KillAura:A-1", category = Category.COMBAT, state = CheckState.EXPERIMENTAL, addBuffer = 0, removeBuffer = 0, maxBuffer = 0, punishVL = 3, silent = false, kickable = true, bannable = false)
public class KillAuraA1 extends Check {

    boolean cSwing;
    int attacks = 0;

    public void onPacket(PacketPlayReceiveEvent packet) {
        if (packet.getPacketId() == PacketType.Play.Client.ARM_ANIMATION) {
            cSwing = true;
            attacks = 0;
        }

        if (packet.getPacketId() == PacketType.Play.Client.USE_ENTITY) {
            WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getNMSPacket());
            attacks++;
            if(attacks > 3 && !cSwing && wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) fail(packet, "Mauvais ordre des packets", "§cARM_ANIMATION = INVALID : USE_ENTITY = VALID");
            else removeBuffer();
            cSwing = false;
        }
    }

}
