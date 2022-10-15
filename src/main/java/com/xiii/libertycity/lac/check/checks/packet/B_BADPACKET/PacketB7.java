package com.xiii.libertycity.lac.check.checks.packet.B_BADPACKET;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.keepalive.WrappedPacketInKeepAlive;
import io.github.retrooper.packetevents.packetwrappers.play.out.keepalive.WrappedPacketOutKeepAlive;

@CheckInfo(name = "Packet:B-7", category = Category.PACKET, state = CheckState.STABLE, addBuffer = 0, removeBuffer = 0, maxBuffer = 0, punishVL = 1, silent = false, kickable = true, bannable = false)
public class PacketB7 extends Check {

    private long id;

    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.KEEP_ALIVE) {
            WrappedPacketInKeepAlive keepalive = new WrappedPacketInKeepAlive(packet.getNMSPacket());
            if(id != keepalive.getId()) {
                fail(null, "Packet invalide", "ID §c" + id + "\n" + " §8»§f cID §c" + keepalive.getId());
            } else removeBuffer();
        }
    }

    public void onPacketSend(PacketPlaySendEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Server.KEEP_ALIVE) {
            WrappedPacketOutKeepAlive keepalive = new WrappedPacketOutKeepAlive(packet.getNMSPacket());
            id = keepalive.getId();
        }
    }

}
