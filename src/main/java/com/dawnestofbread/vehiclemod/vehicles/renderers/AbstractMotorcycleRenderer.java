package com.dawnestofbread.vehiclemod.vehicles.renderers;

import com.dawnestofbread.vehiclemod.AbstractMotorcycle;
import com.dawnestofbread.vehiclemod.geo.Transform;
import com.dawnestofbread.vehiclemod.utils.MathUtils;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static com.dawnestofbread.vehiclemod.VehicleMod.LOGGER;

public abstract class AbstractMotorcycleRenderer<T extends AbstractMotorcycle> extends AbstractWheeledRenderer<T> {
    public AbstractMotorcycleRenderer(EntityRendererProvider.Context context, ResourceLocation modelLocation) {
        super(context, modelLocation);
    }

    @Override
    protected void handleRoot(T entity, Transform root, float partialTick) {
        double newRootXRot = MathUtils.dInterpTo(root.getRotX(), -entity.getXRot(), 15f, partialTick * 0.05);
        double newRootZRot = MathUtils.dInterpToExp(root.getRotZ(), (entity.getForwardSpeed() * (entity.getSteeringAngle() * entity.getSteering()) * (2 - entity.getTraction())) / 30, 1.5, partialTick * 0.05);
        root.setRotX(newRootXRot);
        root.setRotZ(newRootZRot);
        entity.passengerTransform().addRotation(newRootXRot, 0, newRootZRot);
    }
}
