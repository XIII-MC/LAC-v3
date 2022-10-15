package com.xiii.libertycity.lac.check.checks.movement.B_SPEED;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import com.xiii.libertycity.lac.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.entityaction.WrappedPacketInEntityAction;

@CheckInfo(name = "Movement:B-2", category = Category.MOVEMENT, state = CheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 1, maxBuffer = 3, punishVL = 6, silent = false, kickable = true, bannable = false)
public class MovementB2 extends Check {

    int airTicks;
    double accel;
    boolean isSprinting;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        final boolean exempt = isExempt(ExemptType.FLYING, ExemptType.NEAR_VEHICLE, ExemptType.VELOCITY);

        if(!data.playerGround) airTicks++;
        if(data.playerGround) airTicks = 0;

        accel = 1E-12;

        if(System.currentTimeMillis() - data.lastAttack < 1200) accel = 0.04;
        final double predictedMotionXZ = data.getLastDeltaXZ() * 0.91F + (isSprinting ? 0.026 : 0.02);
        final double difference = data.getDeltaXZ() - predictedMotionXZ;

        if (difference > accel && airTicks > 2 && !exempt && !data.isInLiquid) fail(packet, "Friction du mouvement invalide", "predictedMotionXZ §9" + predictedMotionXZ + "\n" + " §8»§f motionXZ §c" + data.getDeltaXZ() + "\n" + " §8»§f difference §c" + difference); else removeBuffer();
    }


    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.ENTITY_ACTION) {
            WrappedPacketInEntityAction action = new WrappedPacketInEntityAction(packet.getNMSPacket());
            if(action.getAction() == WrappedPacketInEntityAction.PlayerAction.START_SPRINTING) {
                isSprinting = true;
            }
            if(action.getAction() == WrappedPacketInEntityAction.PlayerAction.STOP_SPRINTING) {
                isSprinting = false;
            }
        }
    }

}
