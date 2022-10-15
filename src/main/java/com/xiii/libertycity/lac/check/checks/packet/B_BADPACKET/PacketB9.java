package com.xiii.libertycity.lac.check.checks.packet.B_BADPACKET;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;

@CheckInfo(name = "Packet:B-9", category = Category.PACKET, state = CheckState.STABLE, addBuffer = 0, removeBuffer = 0, maxBuffer = 0, punishVL = 1, silent = false, kickable = true, bannable = false)
public class PacketB9 extends Check {

    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.LOOK || packet.getPacketId() == PacketType.Play.Client.POSITION_LOOK) {
            WrappedPacketInFlying wrapper = new WrappedPacketInFlying(packet.getNMSPacket());
            if(wrapper.getPitch() > 90 || wrapper.getPitch() < -90) fail(packet, "Packet invalide", "pitch §c" + wrapper.getPitch()); else removeBuffer();
        }
    }

}
