package com.dawnestofbread.vehiclemod.vehicles.renderers;

import com.dawnestofbread.vehiclemod.geo.BedrockEntityRenderer;
import com.dawnestofbread.vehiclemod.vehicles.entities.Twinkie;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class TwinkieBedrockRenderer<T extends Entity> extends BedrockEntityRenderer<T> {

    public TwinkieBedrockRenderer(EntityRendererProvider.Context context) {
        super(context, new ResourceLocation("vehiclemod", "geometry/twinkie.geo.json"));
    }
}
