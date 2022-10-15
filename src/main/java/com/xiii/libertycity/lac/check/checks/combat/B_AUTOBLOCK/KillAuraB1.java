package com.xiii.libertycity.lac.check.checks.combat.B_AUTOBLOCK;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

@CheckInfo(name = "KillAura:B-1", category = Category.COMBAT, state = CheckState.EXPERIMENTAL, addBuffer = 0, removeBuffer = 0, maxBuffer = 0, punishVL = 3, silent = false, kickable = true, bannable = false)
public class KillAuraB1 extends Check {

    double lastUse;

    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.USE_ENTITY) {
            WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getNMSPacket());
            if((System.currentTimeMillis() - lastUse < 86) && wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) fail(packet, "Mauvais ordre des packets", "difference §9" + (System.currentTimeMillis() - lastUse));
        } else if(packet.getPacketId() == PacketType.Play.Client.BLOCK_PLACE && packet.getPlayer().getInventory().getItemInMainHand().getType().toString().contains("SWORD")) lastUse = System.currentTimeMillis();
    }

}
