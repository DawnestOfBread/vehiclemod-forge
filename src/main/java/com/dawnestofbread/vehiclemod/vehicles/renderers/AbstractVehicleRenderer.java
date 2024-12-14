package com.dawnestofbread.vehiclemod.vehicles.renderers;

import com.dawnestofbread.vehiclemod.AbstractVehicle;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import static com.dawnestofbread.vehiclemod.utils.Rendering.*;

@SuppressWarnings("SameParameterValue")
public abstract class AbstractVehicleRenderer<T extends AbstractVehicle> extends GeoEntityRenderer<T> {
    public AbstractVehicleRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
        super(renderManager, model);
        this.shadowRadius = 0;
    }
    protected GeoModel<T> model;

    @Override
    public void render(@NotNull T entity, float entityYaw, float partialTick, PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-entityYaw));
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();

        //poseStack.translate(animatable.translateOffset.x, animatable.translateOffset.y, animatable.translateOffset.z);
    }
}
