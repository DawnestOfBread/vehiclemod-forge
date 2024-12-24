package com.dawnestofbread.vehiclemod.geo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static com.dawnestofbread.vehiclemod.WheeledVehicle.LOGGER;

/*
 * Parses the json data and builds a cube out of quads
 * This class doesn't provide a raw json
 */
public class Cube {
    private final List<Quad> quads = new ArrayList<>();
    private final List<UV> uv = new ArrayList<>();
    private final BedrockModel model;
    private Vec3 origin = Vec3.ZERO;
    private Vec3 pivot = Vec3.ZERO;
    private Vec3 rotation = Vec3.ZERO;
    private Vec3 size = Vec3.ZERO;

    public Cube(JsonObject cubeO, BedrockModel model, Bone bone) {
        this.model = model;
        if (cubeO.has("origin")) {
            JsonArray origin = cubeO.get("origin").getAsJsonArray();
            this.origin = new Vec3(origin.get(0).getAsDouble() / 16, origin.get(1).getAsDouble() / 16, origin.get(2).getAsDouble() / 16);
        }

        if (cubeO.has("pivot")) {
            JsonArray pivot = cubeO.get("pivot").getAsJsonArray();
            this.pivot = new Vec3(-pivot.get(0).getAsDouble() / 16, pivot.get(1).getAsDouble() / 16, pivot.get(2).getAsDouble() / 16);
        }

        if (cubeO.has("rotation")) {
            JsonArray rotation = cubeO.get("rotation").getAsJsonArray();
            this.rotation = new Vec3(Math.toRadians(-rotation.get(0).getAsDouble()), Math.toRadians(-rotation.get(1).getAsDouble()), Math.toRadians(rotation.get(2).getAsDouble()));
        }

        if (cubeO.has("size")) {
            JsonArray size = cubeO.get("size").getAsJsonArray();
            this.size = new Vec3(size.get(0).getAsDouble() / 16, size.get(1).getAsDouble() / 16, size.get(2).getAsDouble() / 16);
            this.origin = new Vec3(-(this.origin.x + this.size.x), this.origin.y, this.origin.z);
        }

        for (Direction dir : Direction.values()) {
            if (cubeO.has("uv")) {
                if (cubeO.get("uv").getAsJsonObject().has(dir.getName().toLowerCase())) {
                    JsonObject uv = cubeO.get("uv").getAsJsonObject().get(dir.getName().toLowerCase()).getAsJsonObject();
                    this.uv.add(new UV(uv, dir, bone));
                } else {
                    this.uv.add(new UV(dir, bone));
                    LOGGER.warn("No " + dir + " UV face found for cube");
                }
            }

            float x1 = (float) this.origin.x();
            float y1 = (float) this.origin.y();
            float z1 = (float) this.origin.z();

            float x2 = (float) (x1 + this.size.x());
            float y2 = (float) (y1 + this.size.y());
            float z2 = (float) (z1 + this.size.z());

            quads.add(buildQuad(x1, y1, z1, x2, y2, z2, dir));
        }
    }

    public BedrockModel getModel() {
        return model;
    }

    private Quad buildQuad(float x1, float y1, float z1, float x2, float y2, float z2,
                           Direction face) {
        Vertex[] vertices = new Vertex[4];
        switch (face) {
            case NORTH -> {
                vertices[0] = new Vertex(new Vector3f(x1, y2, z1), 0, 0);
                vertices[1] = new Vertex(new Vector3f(x2, y2, z1), 0, 0);
                vertices[2] = new Vertex(new Vector3f(x2, y1, z1), 0, 0);
                vertices[3] = new Vertex(new Vector3f(x1, y1, z1), 0, 0);
            }
            case SOUTH -> {
                vertices[0] = new Vertex(new Vector3f(x2, y2, z2), 0, 0);
                vertices[1] = new Vertex(new Vector3f(x1, y2, z2), 0, 0);
                vertices[2] = new Vertex(new Vector3f(x1, y1, z2), 0, 0);
                vertices[3] = new Vertex(new Vector3f(x2, y1, z2), 0, 0);
            }
            case WEST -> {
                vertices[0] = new Vertex(new Vector3f(x1, y2, z2), 0, 0);
                vertices[1] = new Vertex(new Vector3f(x1, y2, z1), 0, 0);
                vertices[2] = new Vertex(new Vector3f(x1, y1, z1), 0, 0);
                vertices[3] = new Vertex(new Vector3f(x1, y1, z2), 0, 0);
            }
            case EAST -> {
                vertices[0] = new Vertex(new Vector3f(x2, y2, z1), 0, 0);
                vertices[1] = new Vertex(new Vector3f(x2, y2, z2), 0, 0);
                vertices[2] = new Vertex(new Vector3f(x2, y1, z2), 0, 0);
                vertices[3] = new Vertex(new Vector3f(x2, y1, z1), 0, 0);
            }
            case UP -> {
                vertices[0] = new Vertex(new Vector3f(x1, y2, z2), 0, 0);
                vertices[1] = new Vertex(new Vector3f(x2, y2, z2), 0, 0);
                vertices[2] = new Vertex(new Vector3f(x2, y2, z1), 0, 0);
                vertices[3] = new Vertex(new Vector3f(x1, y2, z1), 0, 0);
            }
            case DOWN -> {
                vertices[0] = new Vertex(new Vector3f(x1, y1, z1), 0, 0);
                vertices[1] = new Vertex(new Vector3f(x2, y1, z1), 0, 0);
                vertices[2] = new Vertex(new Vector3f(x2, y1, z2), 0, 0);
                vertices[3] = new Vertex(new Vector3f(x1, y1, z2), 0, 0);
            }
            default -> throw new IllegalArgumentException("Invalid face");
        }
        UV uvMap = getUVMapForFace(face);
        return Quad.build(vertices, uvMap.getUvPos().x, uvMap.getUvPos().y, uvMap.getUvSize().x, uvMap.getUvSize().y, getModel().getTextureSize().x, getModel().getTextureSize().y, false, face);
    }

    private UV getUVMapForFace(Direction face) {
        return this.uv.stream().filter(uv1 -> uv1.getFace().equals(face)).findFirst().orElseThrow();
    }

    public List<Quad> getQuads() {
        return quads;
    }

    public Vec3 getRotation() {
        return rotation;
    }

    public Vec3 getPivot() {
        return pivot;
    }

    public Vec3 getOrigin() {
        return origin;
    }

    public Vec3 getSize() {
        return size;
    }

    public List<UV> getUv() {
        return uv;
    }
}
