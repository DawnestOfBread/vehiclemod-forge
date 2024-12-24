package com.dawnestofbread.vehiclemod.geo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec2;

import static com.dawnestofbread.vehiclemod.WheeledVehicle.LOGGER;

/*
 * Parses a quad's UV map
*/
public class UV {
    private final Direction face;
    private final Vec2 uvPos;
    private final Vec2 uvSize;

    public UV(JsonObject uv, Direction dir, Bone bone) {
        this.face = dir;
        JsonArray uvPos = uv.getAsJsonArray("uv");
        this.uvPos = new Vec2(uvPos.get(0).getAsFloat(), uvPos.get(1).getAsFloat());
        JsonArray uvSize = uv.getAsJsonArray("uv_size");
        this.uvSize = new Vec2(uvSize.get(0).getAsFloat(), uvSize.get(1).getAsFloat());
        LOGGER.info("New UV for face: " + dir + ", for cube of bone: " + bone.toString());
    }
    public UV(Direction dir, Bone bone) {
        this.face = dir;
        this.uvPos = new Vec2(0,0);
        this.uvSize = new Vec2(0,0);
        LOGGER.info("New empty UV for face: " + dir + ", for cube of bone: " + bone.toString());
    }

    public Direction getFace() {
        return face;
    }

    public Vec2 getUvPos() {
        return uvPos;
    }

    public Vec2 getUvSize() {
        return uvSize;
    }
}
