package com.xiii.libertycity.lac.check.checks.movement.D_VCLIP;

import com.xiii.libertycity.lac.check.Category;
import com.xiii.libertycity.lac.check.Check;
import com.xiii.libertycity.lac.check.CheckInfo;
import com.xiii.libertycity.lac.check.CheckState;
import com.xiii.libertycity.lac.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Movement:D-1", category = Category.PLAYER, state = CheckState.EXPERIMENTAL, addBuffer = 0, removeBuffer = 0, maxBuffer = 0, punishVL = 1, silent = false, kickable = true, bannable = false)
public class MovementD1 extends Check {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        final boolean exempt = isExempt(ExemptType.GLIDE, ExemptType.NEAR_VEHICLE, ExemptType.SLIME, ExemptType.TELEPORT, ExemptType.FLYING);

        double maxMotion = isExempt(ExemptType.VELOCITY) ? 1.5: 0.5;
        maxMotion += (double)(data.hasPotionEffect(PotionEffectType.JUMP) ? (data.getEffectByType(PotionEffectType.JUMP).get().getAmplifier() + 1) * 0.1F : 0);
        final double absMotion = Math.abs(motionY);
        final double finalMaxMotion = maxMotion + Math.abs(lastMotionY);

        if(absMotion > finalMaxMotion && !exempt) fail(packet, "Téléportation verticale invalide", "motionY §9" + absMotion + "§8/§c" + finalMaxMotion);

    }

}
