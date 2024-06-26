package com.xiii.libertycity.lac.utils;

import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@Getter
public class BoundingBox {

    private double maxX, maxY, maxZ, minX, minY, minZ;

    public BoundingBox(Block block) {
        this.maxX = io.github.retrooper.packetevents.utils.boundingbox.BoundingBox.of(block).getMaxX();
        this.maxY = io.github.retrooper.packetevents.utils.boundingbox.BoundingBox.of(block).getMaxY();
        this.maxZ = io.github.retrooper.packetevents.utils.boundingbox.BoundingBox.of(block).getMaxZ();
        this.minX = io.github.retrooper.packetevents.utils.boundingbox.BoundingBox.of(block).getMinX();
        this.minY = io.github.retrooper.packetevents.utils.boundingbox.BoundingBox.of(block).getMinY();
        this.minZ = io.github.retrooper.packetevents.utils.boundingbox.BoundingBox.of(block).getMinZ();
    }

    public BoundingBox(Player player) {
        this.maxX = player.getLocation().getX() + 0.3D;
        this.maxY = player.getLocation().getY() + 1.8D;
        this.maxZ = player.getLocation().getZ() + 0.3D;
        this.minX = player.getLocation().getX() - 0.3D;
        this.minY = player.getLocation().getY();
        this.minZ = player.getLocation().getZ() - 0.3D;
    }

    public BoundingBox(double maxX, double maxY, double maxZ, double minX, double minY, double minZ) {
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
    }

    public BoundingBox expand(final double x, final double y, final double z) {
        this.maxX += x;
        this.maxY += y;
        this.maxZ += z;
        this.minX -= x;
        this.minY -= y;
        this.minZ -= z;
        return this;
    }

    public BoundingBox expandOther(final double minX, final double maxX, final double minY, final double maxY, final double minZ, final double maxZ) {
        this.maxX += maxX;
        this.maxY += maxY;
        this.maxZ += maxZ;
        this.minX -= minX;
        this.minY -= minY;
        this.minZ -= minZ;
        return this;
    }

    public double isCollided(RayTrace rayTrace, double maxValue, double minValue) {
        for(int i = 0; i < 3; i++) {
            double direction = 1/ rayTrace.getDirection(i);
            double maxBoundingBox = direction * (getMax(i) - rayTrace.getOrigin(i));
            double minBoundingBox = direction * (getMin(i) - rayTrace.getOrigin(i));
            if(direction < 0) {
                double lastMinBoundingBox = minBoundingBox;
                minBoundingBox = maxBoundingBox;
                maxBoundingBox = lastMinBoundingBox;
            }
            maxValue = Math.max(maxValue, maxBoundingBox);
            minValue = Math.max(minValue, minBoundingBox);
            if(maxValue <= minValue) return 10;
        }
        return minValue;
    }

    public double getMin(int i) {
        if(i == 0) {
            return getMinX();
        }
        if(i == 1) {
            return getMinY();
        }
        if(i == 2) {
            return getMinZ();
        }
        return 0;
    }

    public double getMax(int i) {
        if(i == 0) {
            return getMaxX();
        }
        if(i == 1) {
            return getMaxY();
        }
        if(i == 2) {
            return getMaxZ();
        }
        return 0;
    }

    public boolean isCollided(BoundingBox otherBB) {
        return this.maxX >= otherBB.minX && this.minX <= otherBB.maxX && this.maxY >= otherBB.minY && this.minY <= otherBB.maxY && this.maxZ >= otherBB.minZ && this.minZ <= otherBB.maxZ;
    }
}
