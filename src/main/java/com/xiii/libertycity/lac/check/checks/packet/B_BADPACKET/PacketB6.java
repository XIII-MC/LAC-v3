package com.xiii.libertycity.lac.check.checks.packet.B_BADPACKET;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import org.bukkit.GameMode;

@CheckInfo(name = "Packet:B-6", category = Category.PACKET, state = CheckState.STABLE, addBuffer = 0, removeBuffer = 0, maxBuffer = 0, punishVL = 3, silent = false, kickable = true, bannable = false)
public class PacketB6 extends Check {

    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.SPECTATE) {
            if(packet.getPlayer().getGameMode() != GameMode.SPECTATOR) fail(packet, "Packet invalide", "§cSPECTATE"); else removeBuffer();
        }
    }

}
