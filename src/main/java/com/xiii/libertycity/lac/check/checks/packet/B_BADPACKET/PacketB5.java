package com.xiii.libertycity.lac.check.checks.packet.B_BADPACKET;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;

@CheckInfo(name = "Packet:B-5", category = Category.PACKET, state = CheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 0, maxBuffer = 5, punishVL = 1, silent = false, kickable = true, bannable = false)
public class PacketB5 extends Check {

    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.STEER_VEHICLE) {
            if(!packet.getPlayer().isInsideVehicle()) fail(packet, "Packet invalide", "§cSTEER_VEHICLE_NULL"); else removeBuffer();
        }
    }

}
