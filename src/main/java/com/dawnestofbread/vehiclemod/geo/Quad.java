package com.dawnestofbread.vehiclemod.geo;

import net.minecraft.core.Direction;
import org.joml.Vector3f;

/*
 * Builds a quad
 * This is lifted directly from GeckoLib
*/
public record Quad(Vertex[] vertices, Vector3f normal, Direction direction) {
    public static Quad build(Vertex[] vertices, double[] uvCoords, double[] uvSize, float texWidth, float texHeight, boolean mirror, Direction direction) {
        return build(vertices, (float)uvCoords[0], (float)uvCoords[1], (float)uvSize[0], (float)uvSize[1], texWidth, texHeight, mirror, direction);
    }

    public static Quad build(Vertex[] vertices, float u, float v, float uSize, float vSize, float texWidth,
                                                                      float texHeight, boolean mirror, Direction direction) {
        float uWidth = (u + uSize) / texWidth;
        float vHeight = (v + vSize) / texHeight;
        u /= texWidth;
        v /= texHeight;
        Vector3f normal = direction.step();

        if (!mirror) {
            float tempWidth = uWidth;
            uWidth = u;
            u = tempWidth;
        }
        else {
            normal.mul(-1, 1, 1);
        }

        vertices[0] = vertices[0].withUVs(u, v);
        vertices[1] = vertices[1].withUVs(uWidth, v);
        vertices[2] = vertices[2].withUVs(uWidth, vHeight);
        vertices[3] = vertices[3].withUVs(u, vHeight);

        return new Quad(vertices, normal, direction);
    }
}
