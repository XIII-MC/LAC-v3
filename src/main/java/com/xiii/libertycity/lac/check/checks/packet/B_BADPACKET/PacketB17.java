package com.xiii.libertycity.lac.check.checks.packet.B_BADPACKET;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockdig.WrappedPacketInBlockDig;

import static io.github.retrooper.packetevents.packetwrappers.play.in.blockdig.WrappedPacketInBlockDig.PlayerDigType.RELEASE_USE_ITEM;

@CheckInfo(name = "Packet:B-17", category = Category.PACKET, state = CheckState.STABLE, addBuffer = 0, removeBuffer = 0, maxBuffer = 0, punishVL = 12, silent = false, kickable = true, bannable = false)
public class PacketB17 extends Check {

    private int countH = 0;

    public void onPacket(PacketPlayReceiveEvent packet) {
        final boolean digging = packet.getPacketId() == PacketType.Play.Client.BLOCK_DIG;
        final boolean flying = packet.getPacketId() == PacketType.Play.Client.FLYING;
        if (digging) {
            final WrappedPacketInBlockDig p = new WrappedPacketInBlockDig(packet.getNMSPacket());

            handle:
            {
                if (p.getDigType() != RELEASE_USE_ITEM) break handle;

                final boolean invalid = ++countH > 20;

                if (invalid) fail(packet, "Packet invalide", "§cBLOCK_IN_DIG_REPEATED " + countH);
            }
        } else if (flying) {
            countH = 0;
        }
    }

}
