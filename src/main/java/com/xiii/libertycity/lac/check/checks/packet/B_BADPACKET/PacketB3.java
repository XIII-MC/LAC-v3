package com.xiii.libertycity.lac.check.checks.packet.B_BADPACKET;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.keepalive.WrappedPacketInKeepAlive;

@CheckInfo(name = "Packet:B-3", category = Category.PACKET, state = CheckState.STABLE, addBuffer = 0, removeBuffer = 0, maxBuffer = 0, punishVL = 1, silent = false, kickable = true, bannable = false)
public class PacketB3 extends Check {

    private long lastId = -1;

    public void onPacket(PacketPlayReceiveEvent packet) {
        if (packet.getPacketId() == PacketType.Play.Client.KEEP_ALIVE) {
            WrappedPacketInKeepAlive p = new WrappedPacketInKeepAlive(packet.getNMSPacket());
            if (p.getId() == lastId) {
                fail(null, "Packet invalide", "§cKEEP_ALIVE");
            } else removeBuffer();
            lastId = p.getId();
        }
    }

}
