package com.dawnestofbread.vehiclemod.vehicles.renderers;

import com.dawnestofbread.vehiclemod.vehicles.entities.Twinkie;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class TwinkieRenderer extends AbstractMotorcycleRenderer<Twinkie> {

    public TwinkieRenderer(EntityRendererProvider.Context context) {
        super(context, new ResourceLocation("vehiclemod", "geometry/twinkie.geo.json"));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Twinkie p_114482_) {
        return new ResourceLocation("vehiclemod", "textures/twinkie.png");
    }
}
