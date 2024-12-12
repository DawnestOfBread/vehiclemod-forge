package com.dawnestofbread.vehiclemod.geo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec2;

/*
 * Parses a quad's UV map
*/
public class UV {
    private final Direction face;
    private final Vec2 uvPos;
    private final Vec2 uvSize;

    public UV(JsonObject uv, Direction dir) {
        this.face = dir;
        JsonArray uvPos = uv.getAsJsonArray("uv");
        this.uvPos = new Vec2(uvPos.get(0).getAsFloat(), uvPos.get(1).getAsFloat());
        JsonArray uvSize = uv.getAsJsonArray("uv_size");
        this.uvSize = new Vec2(uvSize.get(0).getAsFloat(), uvSize.get(1).getAsFloat());
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
