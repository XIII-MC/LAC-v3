package com.xiii.libertycity.lac.check.checks.packet.C_TIMER;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import com.xiii.libertycity.lac.exempt.ExemptType;
import com.xiii.libertycity.lac.utils.SampleList;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent;
import io.github.retrooper.packetevents.packettype.PacketType;

@CheckInfo(name = "Packet:C-1", category = Category.PACKET, state = CheckState.STABLE, addBuffer = 1, removeBuffer = 0.04, maxBuffer = 2, punishVL = 12, silent = false, kickable = true, bannable = false)
public class PacketC1 extends Check {

    double bal;
    double lastBal;
    long lastMS;
    long joinTime;
    boolean wasFirst;
    SampleList<Long> balls = new SampleList<>(3);

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        //if(packet.getPacketId() == PacketType.Play.Client.POSITION || packet.getPacketId() == PacketType.Play.Client.POSITION_LOOK || packet.getPacketId() == PacketType.Play.Client.LOOK || packet.getPacketId() == PacketType.Play.Client.FLYING) {
        long now = System.currentTimeMillis();
        long rate = (System.currentTimeMillis() - lastMS);
        bal += 50 - rate;
        if(now - data.weirdTeleport < 1000 || isExempt(ExemptType.TELEPORT)) {
            bal = -50;
        }
        if(!wasFirst) {
            wasFirst = true;
            joinTime = now;
        }
        if(now - joinTime < 3000) {
            bal = -40;
        }
        if(String.valueOf(bal).contains("E")) {
            bal = -5;
        }
        if(Math.abs(rate) >= 48 && Math.abs(rate) <= 52 && bal < -10) {
            balls.add(rate);
            if(balls.isCollected()) {
                if (Math.abs(balls.getAverageLong(balls) - Math.abs(rate)) < 20) {
                    bal = -10;
                }
            }
        }
        if(bal > 15) {
            fail(packet, "Envoie des packets trop rapidement", "bal §c" + bal);
            bal = -2;
        } else removeBuffer();
        lastMS = now;
        lastBal = bal;
        //}
    }

    @Override
    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.FLYING || packet.getPacketId() == PacketType.Play.Client.LOOK) {
            long now = System.currentTimeMillis();
            double rate = (System.currentTimeMillis() - lastMS);
            bal += 50 - rate;
            if(now - data.weirdTeleport < 1000 || isExempt(ExemptType.TELEPORT)) {
                bal = -50;
            }
            if(!wasFirst) {
                wasFirst = true;
                joinTime = now;
            }
            if(now - joinTime < 6000) {
                bal = -40;
            }
            if(bal > 15) {
                fail(packet, "Envoie trop de packets de mouvement", "bal §c" + bal);
                bal = -5;
            }else removeBuffer();
            lastMS = now;
        }
    }


    public void onPacketSend(PacketPlaySendEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Server.POSITION) {
            bal -= 50;
        }
    }

}
