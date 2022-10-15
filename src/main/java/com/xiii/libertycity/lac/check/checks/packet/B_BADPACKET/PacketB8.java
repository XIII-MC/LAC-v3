package com.xiii.libertycity.lac.check.checks.packet.B_BADPACKET;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.steervehicle.WrappedPacketInSteerVehicle;

@CheckInfo(name = "Packet:B-8", category = Category.PACKET, state = CheckState.STABLE, addBuffer = 0, removeBuffer = 0, maxBuffer = 0, punishVL = 1, silent = false, kickable = true, bannable = false)
public class PacketB8 extends Check {

    public void onPacket(PacketPlayReceiveEvent packet) {
        if (packet.getPacketId() == PacketType.Play.Client.STEER_VEHICLE) {
            WrappedPacketInSteerVehicle wrapper = new WrappedPacketInSteerVehicle(packet.getNMSPacket());
            float forwards = Math.abs(wrapper.getForwardValue());
            float sideways = Math.abs(wrapper.getSideValue());
            if (forwards > 0.98f || sideways > 0.98f) fail(packet, "Packet invalide", "forwards §c" + forwards + "\n" + " §8»§f sideways §c" + sideways); else removeBuffer();
        }
    }

}
