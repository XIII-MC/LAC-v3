package com.xiii.libertycity.lac.check;

import com.xiii.libertycity.lac.LAC;
import com.xiii.libertycity.lac.data.PlayerData;
import com.xiii.libertycity.lac.exempt.ExemptType;
import io.github.retrooper.packetevents.event.eventtypes.CancellableNMSPacketEvent;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Check {

    public String name;
    public boolean enabled;
    public Category category;
    public CheckState state;
    private boolean sent = false;
    private long lastFlying;
    private long lastOtherPacket;
    public double buffer = 0;
    public double maxBuffer;
    private double bufferPost;
    public boolean bannable;
    public boolean kickable;
    public boolean silent;
    public double addBuffer;
    public double removeBuffer;
    public int punishVL;
    public PlayerData data;
    public boolean isDebugging;
    public List<Player> debugToPlayers = new ArrayList<>();

    public void onPacket(PacketPlayReceiveEvent packet) {
    }

    public void onPacketSend(PacketPlaySendEvent packet) {
    }

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
    }

    public void fail(CancellableNMSPacketEvent packetEvent, Object information, Object value) {
        if(data != null) {

            if(buffer < maxBuffer + 10 && addBuffer != 0) {
                buffer += addBuffer;
            }
            if(buffer > maxBuffer || addBuffer == 0) {

                CheckInfo info = this.getClass().getAnnotation(CheckInfo.class);
                //sendMessage("fail a=" + addBuffer + " b=" + buffer + " r=" + removeBuffer + " silent=" + silent + " kick=" + kickable + " ban=" + bannable);
                data.flag(this, LAC.configUtils.getIntFromConfig("checks", name + ".Punishments.punishVL", info.punishVL()) - 1, information, value, buffer, maxBuffer, state);
                if(!silent && packetEvent != null) packetEvent.setCancelled(true);
            }
        }
    }

    public boolean mathOnGround(final double posY) {
        return posY % 0.015625 == 0;
    }

    public void debug(String message) {
        if(isDebugging) {
            if(debugToPlayers.size() == 0) {
                sendMessage(message);
            } else {
                for(Player p : debugToPlayers) {
                    //if(p.getName().equals(p.getName())) {
                    p.sendMessage(message);
                    //}
                }
            }
        }
    }

    public boolean isExempt(final ExemptType exemptType) {
        return data.getExempt().isExempt(exemptType);
    }

    public boolean isExempt(ExemptType... type) {
        return data.getExempt().isExempt(type);
    }

    public void removeBuffer() {
        if(removeBuffer <= 0) {
            buffer = 0;
        } else {
            if (buffer >= removeBuffer) {
                buffer -= removeBuffer;
            } else {
                buffer = 0;
            }
        }
        //sendMessage("remove a=" + addBuffer + " b=" + buffer + " r=" + removeBuffer + " silent=" + silent + " kick=" + kickable + " ban=" + bannable);
    }

    public void sendMessage(String Message) {
        if(data != null) {
            if(data.getPlayer() != null) {
                Bukkit.getScheduler().runTask(LAC.INSTANCE, () -> {
                    data.getPlayer().sendMessage(Message);
                });
            }
        }
    }


    public boolean isPost(byte type1, byte type2) {
        if (type1 == PacketType.Play.Client.POSITION || type1 == PacketType.Play.Client.POSITION_LOOK ||type1 == PacketType.Play.Client.LOOK) {
            final long now = System.currentTimeMillis();
            final long delay = now - lastOtherPacket;

            if (sent) {
                if (delay > 40L && delay < 100L) {
                    bufferPost += 0.25;

                    if (bufferPost > 0.5) {
                        return true;
                    }
                } else {
                    bufferPost = Math.max(bufferPost - 0.025, 0);
                }

                sent = false;
            }

            this.lastFlying = now;
        } else if (type1 == type2) {
            final long now = System.currentTimeMillis();
            final long delay = now - lastFlying;

            if (delay < 10L) {
                lastOtherPacket = now;
                sent = true;
            } else {
                bufferPost = Math.max(bufferPost - 0.025, 0.0);
            }
        }

        return false;
    }

}
