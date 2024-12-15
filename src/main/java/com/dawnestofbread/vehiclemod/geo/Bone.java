package com.dawnestofbread.vehiclemod.geo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/*
 * Parses the json bone data and packs it into this nice class
 * Again, the raw JSON shouldn't be used outside of here
*/
public class Bone {
    private final JsonObject jsonBone;
    private final String name;
    private final String parentName;
    private final boolean hasParent;
    private Vec3 pivot = Vec3.ZERO;
    private final Vec3 scale;
    private Vec3 rotation = Vec3.ZERO;
    private boolean hidden = false;
    private final List<Cube> cubes = new ArrayList<>();
    public Bone(JsonElement bone) {
        jsonBone = bone.getAsJsonObject();
        this.name = jsonBone.get("name").getAsString();
        this.hasParent = jsonBone.has("parent");
        this.parentName = hasParent ? jsonBone.get("parent").getAsString() : null;

        if (jsonBone.has("pivot")) {
            JsonArray pivot = jsonBone.getAsJsonArray("pivot");
            this.pivot = new Vec3(-pivot.get(0).getAsDouble() / 16, pivot.get(1).getAsDouble() / 16, pivot.get(2).getAsDouble() / 16);
        }

        this.scale = new Vec3(1,1,1);

        if (jsonBone.has("rotation")) {
            JsonArray rotation = jsonBone.get("rotation").getAsJsonArray();
            this.rotation = new Vec3(Math.toRadians(-rotation.get(0).getAsDouble()), Math.toRadians(-rotation.get(1).getAsDouble()), Math.toRadians(rotation.get(2).getAsDouble()));
        }

        if (jsonBone.has("cubes")) for (JsonElement cubeE : jsonBone.getAsJsonArray("cubes")) {
            JsonObject cube = cubeE.getAsJsonObject();
            this.cubes.add(new Cube(cube));
        }
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public Vec3 getScale() {
        return scale;
    }

    public Vector3f getScaleF() {
        return scale.toVector3f();
    }

    public boolean hasParent() {
        return hasParent;
    }

    public JsonObject getJsonBone() {
        return jsonBone;
    }

    public String getName() {
        return name;
    }

    public String getParentName() {
        return parentName;
    }

    public Vec3 getPivot() {
        return pivot;
    }

    public Vector3f getPivotF() {
        return pivot.toVector3f();
    }

    public Vec3 getRotation() {
        return rotation;
    }

    public Vector3f getRotationF() {
        return rotation.toVector3f();
    }

    public List<Cube> getCubes() {
        return cubes;
    }
}
