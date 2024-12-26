package com.dawnestofbread.vehiclemod.vehicles.renderers;

import com.dawnestofbread.vehiclemod.AbstractMotorcycle;
import com.dawnestofbread.vehiclemod.geo.Transform;
import com.dawnestofbread.vehiclemod.utils.MathUtils;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static com.dawnestofbread.vehiclemod.utils.MathUtils.*;

public abstract class AbstractMotorcycleRenderer<T extends AbstractMotorcycle> extends AbstractWheeledRenderer<T> {
    public AbstractMotorcycleRenderer(EntityRendererProvider.Context context, ResourceLocation modelLocation) {
        super(context, modelLocation);
    }

    @Override
    protected void handleRoot(T entity, Transform root, float partialTick) {
        double newRootXRot = MathUtils.dInterpTo(root.getRotX(), entity.getXRot(), 1.5f, partialTick * 0.05);
        double newRootZRot = MathUtils.dInterpToExp(root.getRotZ(), (entity.getForwardSpeed() * (entity.getSteeringAngle() * entity.getSteering()) * (2 - entity.getTraction())) / 30, 1.5, partialTick * 0.05);
        root.setRotX(newRootXRot);
        root.setRotZ(newRootZRot);
        entity.passengerTransform().addRotation(newRootXRot, 0, newRootZRot);
    }

    @Override
    public void onSetupBoneTransform(float partialTick, T entity, String boneName, Transform boneTransform) {
        super.onSetupBoneTransform(partialTick, entity, boneName, boneTransform);
        if (boneName.equals("additionalTurningComponent")) {
            boneTransform.setRotY(dInterpTo(boneTransform.getRotY(),-entity.getSteering() * entity.getSteeringAngle() * mapDoubleRangeClamped(entity.getTraction(), 0, 1, -1, 1), 1.5, partialTick));
        }
    }
}
