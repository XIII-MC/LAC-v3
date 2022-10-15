package com.xiii.libertycity.lac.check.checks.packet.B_BADPACKET;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@CheckInfo(name = "Packet:B-13", category = Category.PACKET, state = CheckState.STABLE, addBuffer = 0, removeBuffer = 0, maxBuffer = 0, punishVL = 6, silent = false, kickable = true, bannable = false)
public class PacketB13 extends Check {

    private double lastWindowTime;
    private double lastUseEntityTime;

    public void onPacket(PacketPlayReceiveEvent packet) {
        if (packet.getPacketId() == -105)
            this.lastWindowTime = System.currentTimeMillis();
        if (packet.getPacketId() == -100)
            this.lastUseEntityTime = System.currentTimeMillis();
        double delay = this.lastUseEntityTime - this.lastWindowTime;
        if (packet.getPacketId() == -100 && delay < 50.0D) fail(packet, "Clique son inventaire trop vite", "delay §c" + delay);
    }

}
