package com.xiii.libertycity.lac.check.checks.packet.B_BADPACKET;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import com.xiii.libertycity.lac.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.abilities.WrappedPacketInAbilities;
import org.bukkit.GameMode;

@CheckInfo(name = "Packet:B-4", category = Category.PACKET, state = CheckState.STABLE, addBuffer = 1, removeBuffer = 0, maxBuffer = 1, punishVL = 3, silent = false, kickable = true, bannable = false)
public class PacketB4 extends Check {

    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.ABILITIES) {
            WrappedPacketInAbilities wrapper = new WrappedPacketInAbilities(packet.getNMSPacket());
            boolean exempt = isExempt(ExemptType.FLYING);
            if(((wrapper.isFlying() && !data.getPlayer().isFlying() && !data.getPlayer().getAllowFlight()) || (!data.getPlayer().getAllowFlight() && wrapper.isFlightAllowed().isPresent() ? wrapper.isFlightAllowed().get() : true)) && !exempt && data.player.getGameMode() != GameMode.CREATIVE && data.player.getGameMode() != GameMode.SPECTATOR) fail(packet, "Packet invalide", "§cABILITIES");
            if(wrapper.isFlying() || data.getPlayer().isFlying() || data.getPlayer().getAllowFlight()) removeBuffer();
        }
    }

}
