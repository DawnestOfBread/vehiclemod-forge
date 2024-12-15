package com.dawnestofbread.vehiclemod.vehicles.renderers;

import com.dawnestofbread.vehiclemod.geo.BedrockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class TwinkieBedrockRenderer<T extends Entity> extends BedrockEntityRenderer<T> {

    public TwinkieBedrockRenderer(EntityRendererProvider.Context context) {
        super(context, new ResourceLocation("vehiclemod", "geometry/twinkie.geo.json"));
    }
}
