package com.dawnestofbread.vehiclemod.registries;

import com.dawnestofbread.vehiclemod.vehicles.entities.Twinkie;
import com.dawnestofbread.vehiclemod.vehicles.models.AbstractMotorcycleModel;
import com.dawnestofbread.vehiclemod.vehicles.models.TwinkieModel;
import com.dawnestofbread.vehiclemod.vehicles.renderers.AnnihilatorRenderer;
import com.dawnestofbread.vehiclemod.vehicles.renderers.TwinkieBedrockRenderer;
import com.dawnestofbread.vehiclemod.vehicles.renderers.TwinkieRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;

public class EntityRendererRegistry {
    public static void RegisterAllRenderers() {
        EntityRenderers.register(VehicleRegistry.ANNIHILATOR.get(), AnnihilatorRenderer::new);
        EntityRenderers.register(VehicleRegistry.TWINKIE.get(), TwinkieBedrockRenderer::new);
    }
}
