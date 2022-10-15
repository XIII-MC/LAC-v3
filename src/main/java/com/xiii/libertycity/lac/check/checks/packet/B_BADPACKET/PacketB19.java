package com.xiii.libertycity.lac.check.checks.packet.B_BADPACKET;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.entityaction.WrappedPacketInEntityAction;

@CheckInfo(name = "Packet:B-19", category = Category.PACKET, state = CheckState.STABLE, addBuffer = 0, removeBuffer = 0, maxBuffer = 0, punishVL = 1, silent = false, kickable = true, bannable = false)
public class PacketB19 extends Check {

    int sneakBuffer = 0;
    boolean sentsneak;

    public void onPacket(PacketPlayReceiveEvent packet) {
        if (packet.getPacketId() == PacketType.Play.Client.ENTITY_ACTION) {
            WrappedPacketInEntityAction action = new WrappedPacketInEntityAction(packet.getNMSPacket());
            if (action.getAction() == WrappedPacketInEntityAction.PlayerAction.STOP_SNEAKING) {
                if (!sentsneak) {
                    sneakBuffer += 1;
                    if (sneakBuffer > 3) fail(packet, "Packet invalide", "§cSTOP_SNEAKING");
                } else {
                    sentsneak = false;
                    sneakBuffer -= 1;
                }
            }
            if (action.getAction() == WrappedPacketInEntityAction.PlayerAction.START_SNEAKING) {
                if (sentsneak) {
                    fail(packet, "Packet invalide", "§cSTART_SNEAKING");
                } else {
                    sentsneak = true;
                    sneakBuffer -= 1;
                }
            }
        }
    }

}
