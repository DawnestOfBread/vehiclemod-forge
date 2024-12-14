package com.dawnestofbread.vehiclemod.geo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Bone {
    private final JsonObject jsonBone;
    private final String name;
    private final String parentName;
    private final boolean hasParent;
    private Vec3 pivot = Vec3.ZERO;
    private Vec3 scale = Vec3.ZERO;
    private Vec3 rotation = Vec3.ZERO;
    private boolean hidden = false;
    private final List<Cube> cubes = new ArrayList<>();
    public Bone(JsonElement bone) {
        JsonObject boneObject = bone.getAsJsonObject();
        jsonBone = boneObject;
        this.name = boneObject.get("name").getAsString();
        this.hasParent = boneObject.has("parent");
        this.parentName = hasParent ? boneObject.get("parent").getAsString() : null;

        if (boneObject.has("pivot")) {
            JsonArray pivot = boneObject.getAsJsonArray("pivot");
            this.pivot = new Vec3(pivot.get(0).getAsDouble() / 16, pivot.get(1).getAsDouble() / 16, pivot.get(2).getAsDouble() / 16);
        }

        this.scale = new Vec3(1,1,1);

        if (boneObject.has("rotation")) {
            JsonArray rotation = boneObject.get("rotation").getAsJsonArray();
            this.rotation = new Vec3(rotation.get(0).getAsDouble(), rotation.get(1).getAsDouble(), rotation.get(2).getAsDouble());
        }

        for (JsonElement cubeE : boneObject.getAsJsonArray("cubes")) {
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
