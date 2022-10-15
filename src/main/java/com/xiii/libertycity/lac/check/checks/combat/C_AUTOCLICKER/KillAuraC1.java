package com.xiii.libertycity.lac.check.checks.combat.C_AUTOCLICKER;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import com.xiii.libertycity.lac.utils.SampleList;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockdig.WrappedPacketInBlockDig;

@CheckInfo(name = "KillAura:C-1", category = Category.COMBAT, state = CheckState.EXPERIMENTAL, addBuffer = 0, removeBuffer = 0, maxBuffer = 0, punishVL = 12, silent = false, kickable = true, bannable = false)
public class KillAuraC1 extends Check {

    boolean isBlockDig;
    SampleList<Long> delay = new SampleList<>(20);
    long lastDelay;
    double lastStd;
    double lastAvg;
    long lastBlockBreak;
    boolean isExempt = false;

    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.ARM_ANIMATION) {
            if(!isBlockDig) {
                long now = System.currentTimeMillis();
                delay.add((now - lastDelay));
                if(System.currentTimeMillis() - lastBlockBreak < 200) delay.clear();
                if(delay.isCollected() && !isExempt) {
                    if(Math.abs(delay.getStandardDeviation(delay) - lastStd) < 10 && Math.abs(delay.getAverageLong(delay) - lastAvg) < 5 && System.currentTimeMillis() - lastBlockBreak > 200) {
                        fail(null, "Cliques en paterne suspect", "std §c" + delay.getStandardDeviation(delay) + "\n" + " §8»§f avg §c" + delay.getAverageLong(delay));
                        debug("std=" + delay.getStandardDeviation(delay) + " avg=" + delay.getAverageLong(delay));
                    }
                    lastStd = delay.getStandardDeviation(delay);
                    lastAvg = delay.getAverageLong(delay);
                }
                lastDelay = now;
            } else {
                if(!delay.isEmpty())
                    delay.removeFirst();
            }
        }

        if(packet.getPacketId() == PacketType.Play.Client.WINDOW_CLICK) {
            isExempt = true;
        } else isExempt = false;

        if(packet.getPacketId() == PacketType.Play.Client.BLOCK_DIG) {
            WrappedPacketInBlockDig dig = new WrappedPacketInBlockDig(packet.getNMSPacket());
            if(dig.getDigType() == WrappedPacketInBlockDig.PlayerDigType.START_DESTROY_BLOCK) {
                isBlockDig = true;

            }
            if(dig.getDigType() == WrappedPacketInBlockDig.PlayerDigType.STOP_DESTROY_BLOCK ||dig.getDigType() == WrappedPacketInBlockDig.PlayerDigType.ABORT_DESTROY_BLOCK) {
                if(isBlockDig) {
                    lastBlockBreak = System.currentTimeMillis();
                }
                isBlockDig = false;
            }
        }
    }

}
