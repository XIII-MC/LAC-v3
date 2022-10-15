package com.xiii.libertycity.lac.utils;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class RayTrace {

    Vector direction;
    Vector origin;

    public RayTrace(Player player) {
        this.direction = player.getEyeLocation().getDirection().clone();
        this.origin = player.getEyeLocation().toVector().clone();
    }

    public double getOrigin(int i) {
        if(i == 0) return origin.getX();
        if(i == 1) return origin.getY();
        if(i == 2) return origin.getZ();
        return 0;
    }

    public double getDirection(int i) {
        if(i == 0) return direction.getX();
        if(i == 1) return direction.getY();
        if(i == 2) return direction.getZ();
        return 0;
    }
}
