package com.xiii.libertycity.lac.check.checks.packet.B_BADPACKET;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@CheckInfo(name = "Packet:B-14", category = Category.PACKET, state = CheckState.STABLE, addBuffer = 0, removeBuffer = 0, maxBuffer = 0, punishVL = 3, silent = false, kickable = true, bannable = false)
public class PacketB14 extends Check {

    public void onPacket(PacketPlayReceiveEvent packet) {
        if (data.eatDelay < 1300.0D) {
            fail(packet, "Mange trop vite", "delay §c" + data.eatDelay);
            data.eatDelay = 1300.0D;
        }
        if (data.lastShootDelay < 99.0D) {
            fail(packet, "Tire trop vite", "delay §c" + data.lastShootDelay);
            data.lastShootDelay = 99.0D;
        }
        if (data.shootDelay < 299.0D) {
            fail(packet, "Tire trop vite", "delay §c" + data.shootDelay);
            data.shootDelay = 299.0D;
        }
    }

}
