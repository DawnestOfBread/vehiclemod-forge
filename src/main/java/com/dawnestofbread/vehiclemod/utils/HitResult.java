package com.dawnestofbread.vehiclemod.utils;

import net.minecraft.world.phys.Vec3;

public class HitResult {
    private double distance;
    private net.minecraft.world.phys.HitResult.Type type;
    private boolean hit;
    private boolean inside;
    private Vec3 start;
    private Vec3 hitLocation;
    private Vec3 end;

    public HitResult() {
    }

    public double getDistance() {
        return distance;
    }

    public net.minecraft.world.phys.HitResult.Type getType() {
        return type;
    }

    public boolean hit() {
        return hit;
    }

    public boolean isInside() {
        return inside;
    }

    public Vec3 getStart() {
        return start;
    }

    public Vec3 getHitLocation() {
        return hitLocation;
    }

    public Vec3 getEnd() {
        return end;
    }

    public HitResult withDistance(double distance) {
        this.distance = distance;
        return this;
    }

    public HitResult withType(net.minecraft.world.phys.HitResult.Type type) {
        this.type = type;
        return this;
    }

    public HitResult didHit(boolean hit) {
        this.hit = hit;
        return this;
    }

    public HitResult isInside(boolean inside) {
        this.inside = inside;
        return this;
    }

    public HitResult from(Vec3 start) {
        this.start = start;
        return this;
    }

    public HitResult to(Vec3 end) {
        this.end = end;
        return this;
    }

    public HitResult hitAt(Vec3 hitLocation) {
        this.hitLocation = hitLocation;
        return this;
    }
}