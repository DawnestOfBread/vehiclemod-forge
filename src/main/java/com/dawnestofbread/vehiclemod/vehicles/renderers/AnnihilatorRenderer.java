package com.dawnestofbread.vehiclemod.vehicles.renderers;

import com.dawnestofbread.vehiclemod.vehicles.entities.Annihilator;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class AnnihilatorRenderer extends AbstractWheeledRenderer<Annihilator> {

    public AnnihilatorRenderer(EntityRendererProvider.Context context) {
        super(context, new ResourceLocation("vehiclemod", "geometry/annihilator.geo.json"));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Annihilator p_114482_) {
        return new ResourceLocation("vehiclemod", "textures/annihilator.png");
    }
}