package com.dawnestofbread.vehiclemod.geo;


import net.minecraft.world.phys.Vec3;

public class Transform {
    private Vec3 position;
    private Vec3 rotation;
    private Vec3 scale;

    public Transform() {
        position = Vec3.ZERO;
        rotation = Vec3.ZERO;
        scale = new Vec3(1, 1, 1);
    }

    public Vec3 getPosition() {
        return position;
    }

    public Transform setPosition(Vec3 position) {
        this.position = position;
        return this;
    }

    public double getXPos() {
        return position.x();
    }

    public double getYPos() {
        return position.y();
    }

    public double getZPos() {
        return position.z();
    }

    public void setPosX(double d0) {
        position = new Vec3(d0, position.y, position.z);
    }

    public void setPosY(double d0) {
        position = new Vec3(position.x, d0, position.z);
    }

    public void setPosZ(double d0) {
        position = new Vec3(position.x, position.y, d0);
    }

    public Transform setPosition(double x, double y, double z) {
        this.position = new Vec3(x, y, z);
        return this;
    }

    public Transform addPosition(double x, double y, double z) {
        this.position = this.position.add(x, y, z);
        return this;
    }

    public Transform addPosition(Vec3 position) {
        this.position = this.position.add(position);
        return this;
    }

    public Vec3 getRotation() {
        return rotation;
    }

    public Transform setRotation(Vec3 rotation) {
        this.rotation = rotation;
        return this;
    }

    public Transform setRotation(double x, double y, double z) {
        this.rotation = new Vec3(x, y, z);
        return this;
    }

    public Transform addRotation(double x, double y, double z) {
        this.rotation = this.rotation.add(x, y, z);
        return this;
    }

    public Transform addRotation(Vec3 rotation) {
        this.rotation = this.rotation.add(rotation);
        return this;
    }

    public double getRotX() {
        return rotation.x();
    }

    public void setRotX(double d0) {
        rotation = new Vec3(d0, rotation.y, rotation.z);
    }

    public double getRotY() {
        return rotation.y();
    }

    public void setRotY(double d0) {
        rotation = new Vec3(rotation.x, d0, rotation.z);
    }

    public double getRotZ() {
        return rotation.z();
    }

    public void setRotZ(double d0) {
        rotation = new Vec3(rotation.x, rotation.y, d0);
    }

    public Vec3 getScale() {
        return scale;
    }

    public Transform setScale(Vec3 scale) {
        this.scale = scale;
        return this;
    }

    public Transform setScale(double x, double y, double z) {
        this.scale = new Vec3(x, y, z);
        return this;
    }

    public Transform addScale(double x, double y, double z) {
        this.scale = this.scale.add(x, y, z);
        return this;
    }

    public Transform addScale(Vec3 scale) {
        this.scale = this.scale.add(scale);
        return this;
    }
}
