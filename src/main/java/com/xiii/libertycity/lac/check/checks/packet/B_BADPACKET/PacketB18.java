package com.xiii.libertycity.lac.check.checks.packet.B_BADPACKET;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;

@CheckInfo(name = "Packet:B-18", category = Category.PACKET, state = CheckState.STABLE, addBuffer = 0, removeBuffer = 0, maxBuffer = 0, punishVL = 12, silent = false, kickable = true, bannable = false)
public class PacketB18 extends Check {

    private int streak = 0;

    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.VEHICLE_MOVE) {
            if (packet.getPacketId() == PacketType.Play.Client.FLYING) {
                WrappedPacketInFlying p = new WrappedPacketInFlying(packet.getNMSPacket());
                if (!p.hasPositionChanged() && packet.getPlayer().getVehicle() == null) {
                    if (++streak > 20) fail(packet, "Packet invalide", "§cFLYING_VEHICULE");
                } else {
                    streak = 0;
                }
            }
        } else if (packet.getPacketId() == PacketType.Play.Client.STEER_VEHICLE) {
            streak = 0;
        }
    }

}
