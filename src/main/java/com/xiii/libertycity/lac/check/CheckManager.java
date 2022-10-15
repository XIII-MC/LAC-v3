package com.xiii.libertycity.lac.check;

import com.xiii.libertycity.lac.LAC;
import com.xiii.libertycity.lac.check.checks.combat.A_KILLAURA.*;
import com.xiii.libertycity.lac.check.checks.combat.B_AUTOBLOCK.KillAuraB1;
import com.xiii.libertycity.lac.check.checks.combat.C_AUTOCLICKER.KillAuraC1;
import com.xiii.libertycity.lac.check.checks.movement.A_FLY.MovementA1;
import com.xiii.libertycity.lac.check.checks.movement.B_SPEED.*;
import com.xiii.libertycity.lac.check.checks.movement.C_GROUND.MovementC1;
import com.xiii.libertycity.lac.check.checks.movement.C_GROUND.MovementC2;
import com.xiii.libertycity.lac.check.checks.movement.D_VCLIP.MovementD1;
import com.xiii.libertycity.lac.check.checks.movement.E_STEP.MovementE1;
import com.xiii.libertycity.lac.check.checks.movement.E_STEP.MovementE2;
import com.xiii.libertycity.lac.check.checks.movement.E_STEP.MovementE3;
import com.xiii.libertycity.lac.check.checks.movement.F_MOTION.MovementF1;
import com.xiii.libertycity.lac.check.checks.movement.F_MOTION.MovementF2;
import com.xiii.libertycity.lac.check.checks.movement.F_MOTION.MovementF3;
import com.xiii.libertycity.lac.check.checks.movement.F_MOTION.MovementF4;
import com.xiii.libertycity.lac.check.checks.packet.A_POST.*;
import com.xiii.libertycity.lac.check.checks.packet.B_BADPACKET.*;
import com.xiii.libertycity.lac.check.checks.packet.C_TIMER.PacketC1;

import java.util.ArrayList;
import java.util.List;

public class CheckManager {

    public List<Check> checks = new ArrayList<>();

    public CheckManager() {

        registerCheck(new MovementA1());

        registerCheck(new MovementB1());
        registerCheck(new MovementB2());
        registerCheck(new MovementB3());
        registerCheck(new MovementB4());
        registerCheck(new MovementB5());

        registerCheck(new MovementC1());
        registerCheck(new MovementC2());

        registerCheck(new MovementD1());

        registerCheck(new MovementE1());
        registerCheck(new MovementE2());
        registerCheck(new MovementE3());

        registerCheck(new MovementF1());
        registerCheck(new MovementF2());
        registerCheck(new MovementF3());
        registerCheck(new MovementF4());

        registerCheck(new PacketA1());
        registerCheck(new PacketA2());
        registerCheck(new PacketA3());
        registerCheck(new PacketA4());
        registerCheck(new PacketA5());
        registerCheck(new PacketA6());

        registerCheck(new PacketB1());
        registerCheck(new PacketB2());
        registerCheck(new PacketB3());
        registerCheck(new PacketB4());
        registerCheck(new PacketB5());
        registerCheck(new PacketB6());
        registerCheck(new PacketB7());
        registerCheck(new PacketB8());
        registerCheck(new PacketB9());
        registerCheck(new PacketB10());
        registerCheck(new PacketB11());
        registerCheck(new PacketB12());
        registerCheck(new PacketB13());
        registerCheck(new PacketB14());
        registerCheck(new PacketB15());
        registerCheck(new PacketB16());
        registerCheck(new PacketB17());
        registerCheck(new PacketB18());
        registerCheck(new PacketB19());

        registerCheck(new PacketC1());

        registerCheck(new KillAuraA1());
        registerCheck(new KillAuraA2());
        registerCheck(new KillAuraA3());
        registerCheck(new KillAuraA4());
        registerCheck(new KillAuraA5());
        registerCheck(new KillAuraA6());

        registerCheck(new KillAuraB1());

        registerCheck(new KillAuraC1());

    }

    public void registerCheck(Check check) {
        CheckInfo info = check.getClass().getAnnotation(CheckInfo.class);
        check.name = info.name();
        check.category = info.category();
        check.state = info.state();
        check.enabled = LAC.configUtils.getBooleanFromConfig("checks", info.name() + ".enabled", info.enabled());// config
        check.kickable = LAC.configUtils.getBooleanFromConfig("checks", info.name() + ".Punishments.kick",info.kickable()); // config
        check.bannable = LAC.configUtils.getBooleanFromConfig("checks", info.name() + ".Punishments.ban", info.bannable());// config
        if(LAC.configUtils.getBooleanFromConfig("config", "silentChecks", info.silent())) {// config decides
            check.silent = LAC.configUtils.getBooleanFromConfig("checks", info.name() + ".silent", info.silent());
        } else {
            check.silent = false;
        }
        check.maxBuffer = LAC.configUtils.getDoubleFromConfig("checks", info.name() + ".Buffer.maxBuffer", info.maxBuffer());// config
        check.addBuffer = LAC.configUtils.getDoubleFromConfig("checks", info.name() + ".Buffer.addBuffer", info.addBuffer());; // config
        check.removeBuffer = LAC.configUtils.getDoubleFromConfig("checks", info.name() + ".Buffer.removeBuffer", info.removeBuffer());; // config
        if(!checks.contains(check)) checks.add(check);
    }

}
