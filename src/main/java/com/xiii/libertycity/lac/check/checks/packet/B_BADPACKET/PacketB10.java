package com.xiii.libertycity.lac.check.checks.packet.B_BADPACKET;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@CheckInfo(name = "Packet:B-10", category = Category.PACKET, state = CheckState.STABLE, addBuffer = 1, removeBuffer = 0.05, maxBuffer = 20, punishVL = 12, silent = false, kickable = true, bannable = false)
public class PacketB10 extends Check {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        if(data.getPlayer().isSprinting() && data.getPlayer().getFoodLevel() <= 6) fail(packet, "Packet invalide", "foodLevel §c" + data.getPlayer().getFoodLevel()); else removeBuffer();
    }

}
