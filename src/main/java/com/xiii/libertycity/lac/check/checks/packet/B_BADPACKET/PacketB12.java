package com.xiii.libertycity.lac.check.checks.packet.B_BADPACKET;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

@CheckInfo(name = "Packet:B-12", category = Category.PACKET, state = CheckState.STABLE, addBuffer = 0, removeBuffer = 0, maxBuffer = 0, punishVL = 3, silent = false, kickable = true, bannable = false)
public class PacketB12 extends Check {

    private int hits;

    public void onPacket(PacketPlayReceiveEvent packet) {
        if (packet.getPacketId() == PacketType.Play.Client.USE_ENTITY) {
            WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getNMSPacket());

            if (wrapper.getAction() != WrappedPacketInUseEntity.EntityUseAction.ATTACK) return;

            if (++hits > 2) fail(packet, "Packet invalide", "hits §c" + hits);
        } else if(packet.getPacketId() == PacketType.Play.Client.ARM_ANIMATION) {
            hits = 0;
        }
    }

}
