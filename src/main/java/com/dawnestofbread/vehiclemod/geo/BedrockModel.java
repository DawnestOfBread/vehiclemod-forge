package com.dawnestofbread.vehiclemod.geo;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.dawnestofbread.vehiclemod.WheeledVehicle.LOGGER;

/*
 * A bedrock entity model format parser
 * While the raw JSON model is accessible (getJsonModel), it shouldn't be used outside of this class
 */
public class BedrockModel {
    private final List<Bone> bones = new ArrayList<>();
    private JsonObject jsonModel;
    private Vec2 textureSize;

    public BedrockModel(ResourceLocation modelLocation) {
        try {
            InputStream stream = Minecraft.getInstance().getResourceManager().open(modelLocation);
            InputStreamReader reader = new InputStreamReader(stream);
            jsonModel = JsonParser.parseReader(reader).getAsJsonObject();
            JsonObject geometryObject = jsonModel.get("minecraft:geometry").getAsJsonArray().get(0).getAsJsonObject();

            if (geometryObject.has("description")) {
                textureSize = new Vec2(geometryObject.getAsJsonObject("description").get("texture_width").getAsFloat(), geometryObject.getAsJsonObject("description").get("texture_height").getAsFloat());
            }
            for (JsonElement bone : geometryObject.getAsJsonArray("bones")) {
                bones.add(new Bone(bone, this));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Vec2 getTextureSize() {
        return textureSize;
    }

    public JsonObject getJsonModel() {
        return jsonModel;
    }

    public List<Bone> getBones() {
        return bones;
    }

    public List<Bone> getChildrenOfBone(String name) {
        return bones.stream().filter(bone -> Objects.equals(bone.getParentName(), name)).toList();
    }

    public List<Bone> getRootBones() {
        return bones.stream().filter(bone -> !bone.hasParent()).toList();
    }

    public Bone getBone(String name) {
        return bones.stream().filter(bone -> bone.getName().equals(name)).findFirst().orElseThrow();
    }
}
