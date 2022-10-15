package com.xiii.libertycity.lac.check.checks.packet.B_BADPACKET;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@CheckInfo(name = "Packet:B-15", category = Category.PACKET, state = CheckState.STABLE, addBuffer = 0, removeBuffer = 0, maxBuffer = 0, punishVL = 6, silent = false, kickable = true, bannable = false)
public class PacketB15 extends Check {

    private double lastUse;
    private int packetD;

    public void onPacket(PacketPlayReceiveEvent packet) {
        if (packet.getPacketId() == -68) {
            if (lastUse - System.currentTimeMillis() > -50.0D && lastUse - System.currentTimeMillis() < -1.0D)
                packetD += 1;
            if (packetD > 3) fail(packet, "Utilise un item trop vite", "delay §c" + (lastUse - System.currentTimeMillis()));
            if (packetD > 3)
                packet.setCancelled(true);
            if (lastUse - System.currentTimeMillis() < -50.0D)
                if (packetD >= 1) packetD -= 1;
            lastUse = System.currentTimeMillis();
        }
    }

}
