package com.dawnestofbread.vehiclemod.utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class LineTrace {
    public static HitResult lineTraceByType(Vec3 start, Vec3 end, ClipContext.Block blockResponse, ClipContext.Fluid fluidResponse, Entity caller) {
        ClipContext lineTrace = new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, caller);
        BlockHitResult r = caller.level().clip(lineTrace);
        return new HitResult()
                .withType(r.getType())
                .withDistance(r.getLocation().distanceTo(start))
                .didHit(r.getType() != net.minecraft.world.phys.HitResult.Type.MISS)
                .isInside(r.isInside())
                .from(start)
                .to(end)
                .hitAt(r.getLocation());
    }
}