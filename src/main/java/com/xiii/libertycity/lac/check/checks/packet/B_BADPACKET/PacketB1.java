package com.xiii.libertycity.lac.check.checks.packet.B_BADPACKET;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.helditemslot.WrappedPacketInHeldItemSlot;

@CheckInfo(name = "Packet:B-1", category = Category.PACKET, state = CheckState.STABLE, addBuffer = 0, removeBuffer = 0, maxBuffer = 0, punishVL = 1, silent = false, kickable = true, bannable = false)
public class PacketB1 extends Check {

    private int lastSlot = -1;

    public void onPacket(PacketPlayReceiveEvent packet) {
        if (packet.getPacketId() == PacketType.Play.Client.HELD_ITEM_SLOT) {
            WrappedPacketInHeldItemSlot p = new WrappedPacketInHeldItemSlot(packet.getNMSPacket());
            if (p.getCurrentSelectedSlot() == lastSlot) fail(packet, "Packet invalide", "§cHELD_ITEM_SLOT = INVALID"); else removeBuffer();
            lastSlot = p.getCurrentSelectedSlot();
        }
    }

}
