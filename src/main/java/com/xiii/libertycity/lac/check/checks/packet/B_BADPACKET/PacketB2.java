package com.xiii.libertycity.lac.check.checks.packet.B_BADPACKET;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import com.xiii.libertycity.lac.exempt.ExemptType;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.entityaction.WrappedPacketInEntityAction;

@CheckInfo(name = "Packet:B-2", category = Category.PACKET, state = CheckState.STABLE, addBuffer = 0, removeBuffer = 0, maxBuffer = 0, punishVL = 1, silent = false, kickable = true, bannable = false)
public class PacketB2 extends Check {

    private int count = 0;
    private WrappedPacketInEntityAction.PlayerAction lastAction;

    public void onPacket(PacketPlayReceiveEvent packet) {
        if (packet.getPacketId() == PacketType.Play.Client.ENTITY_ACTION) {
            boolean exempt = isExempt(ExemptType.RESPAWN, ExemptType.TELEPORT);
            WrappedPacketInEntityAction p = new WrappedPacketInEntityAction(packet.getNMSPacket());
            final boolean invalid = ++count > 1 && p.getAction() == lastAction;
            if (invalid && !exempt) fail(packet, "Packet invalide", "§cENTITY_ACTION"); else removeBuffer();
            this.lastAction = p.getAction();
        } else if (packet.getPacketId() == PacketType.Play.Client.FLYING) count = 0;
    }

}
