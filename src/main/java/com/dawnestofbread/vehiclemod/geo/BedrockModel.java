package com.dawnestofbread.vehiclemod.geo;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BedrockModel {
    private final List<Bone> bones = new ArrayList<>();
    private JsonObject jsonModel;

    public BedrockModel(ResourceLocation resourceLocation) {
        try {
            InputStream stream = Minecraft.getInstance().getResourceManager().open(resourceLocation);
            InputStreamReader reader = new InputStreamReader(stream);
            setJsonModel(JsonParser.parseReader(reader).getAsJsonObject());

            for (JsonElement bone : jsonModel.get("minecraft:geometry").getAsJsonArray().get(0).getAsJsonObject().getAsJsonArray("bones")) {
                bones.add(new Bone(bone));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JsonObject getJsonModel() {
        return jsonModel;
    }

    protected void setJsonModel(JsonObject jsonModel) {
        this.jsonModel = jsonModel;
    }

    public List<Bone> getBones() {
        return bones;
    }

    public List<Bone> getChildBones(String name) {
        return bones.stream().filter(bone -> bone.hasParent() && Objects.equals(bone.getParentName(), name)).toList();
    }

    public List<Bone> getTopLevelBones() {
        return bones.stream().filter(bone -> !bone.hasParent()).toList();
    }

    public Bone getBone(String name) {
        return bones.stream().filter(bone -> bone.getName().equals(name)).findFirst().orElseThrow();
    }
}
