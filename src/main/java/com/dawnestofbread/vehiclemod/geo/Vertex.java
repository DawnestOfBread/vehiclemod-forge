package com.dawnestofbread.vehiclemod.geo;

import org.joml.Vector3f;

public record Vertex(Vector3f position, float texU, float texV) {
    public Vertex(double x, double y, double z) {
        this(new Vector3f((float)x, (float)y, (float)z), 0, 0);
    }

    public Vertex withUVs(float texU, float texV) {
        return new Vertex(this.position, texU, texV);
    }
}