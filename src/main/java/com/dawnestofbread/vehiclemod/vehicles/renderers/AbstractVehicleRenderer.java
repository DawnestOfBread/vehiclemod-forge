package com.dawnestofbread.vehiclemod.vehicles.renderers;

import com.dawnestofbread.vehiclemod.AbstractVehicle;
import com.dawnestofbread.vehiclemod.geo.BedrockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractVehicleRenderer<T extends AbstractVehicle> extends BedrockEntityRenderer<T> {
    public AbstractVehicleRenderer(EntityRendererProvider.Context context, ResourceLocation modelLocation) {
        super(context, modelLocation);
    }
}
