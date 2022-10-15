package com.xiii.libertycity.lac.check.checks.packet.A_POST;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;

@CheckInfo(name = "Packet:A-5", category = Category.PACKET, state = CheckState.STABLE, addBuffer = 0, removeBuffer = 0, maxBuffer = 0, punishVL = 1, silent = false, kickable = true, bannable = false)
public class PacketA5 extends Check {

    public void onPacket(PacketPlayReceiveEvent packet) {
        if (isPost(packet.getPacketId(), PacketType.Play.Client.HELD_ITEM_SLOT)) fail(packet, "Mauvais ordre du packet", "§cHELD_ITEM_SLOT = POST"); else removeBuffer();
    }

}
