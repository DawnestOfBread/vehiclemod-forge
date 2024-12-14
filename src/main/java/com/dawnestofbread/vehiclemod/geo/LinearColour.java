package com.dawnestofbread.vehiclemod.geo;

import net.minecraft.util.Mth;

public class LinearColour {
    public static final LinearColour BLACK = new LinearColour(0, 0, 0);
    public static final LinearColour WHITE = new LinearColour(1, 1, 1);
    public static final LinearColour RED = new LinearColour(1, 0, 0);
    public static final LinearColour GREEN = new LinearColour(0, 1, 0);
    public static final LinearColour BLUE = new LinearColour(0, 0, 1);
    private final float r, g, b, a;

    public LinearColour(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public LinearColour(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = 1;
    }

    public float getR() {
        return Mth.clamp(r * 255, 0, 255);
    }

    public float getG() {
        return Mth.clamp(g * 255, 0, 255);
    }

    public float getB() {
        return Mth.clamp(b * 255, 0, 255);
    }

    public float getA() {
        return Mth.clamp(a * 255, 0, 255);
    }

    public LinearColour multiplyRGB(LinearColour colour) {
        return new LinearColour(this.r * colour.r, this.g * colour.g, this.b * colour.b);
    }

    public LinearColour multiplyRGBA(LinearColour colour) {
        return new LinearColour(this.r * colour.r, this.g * colour.g, this.b * colour.b, this.a * colour.a);
    }

    public LinearColour multiplyRGB(float f0) {
        return new LinearColour(this.r * f0, this.g * f0, this.b * f0);
    }

    public LinearColour multiplyRGBA(float f0) {
        return new LinearColour(this.r * f0, this.g * f0, this.b * f0, this.a * f0);
    }

    public LinearColour addRGB(LinearColour colour) {
        return new LinearColour(this.r + colour.r, this.g + colour.g, this.b + colour.b);
    }

    public LinearColour addRGBA(LinearColour colour) {
        return new LinearColour(this.r + colour.r, this.g + colour.g, this.b + colour.b, this.a + colour.a);
    }

    public LinearColour addRGB(float f0) {
        return new LinearColour(this.r + f0, this.g + f0, this.b + f0);
    }

    public LinearColour addRGBA(float f0) {
        return new LinearColour(this.r + f0, this.g + f0, this.b + f0, this.a + f0);
    }
}
