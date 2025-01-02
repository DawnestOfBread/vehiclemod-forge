package com.dawnestofbread.vehiclemod.vehicles.renderers;

import com.dawnestofbread.vehiclemod.WheeledVehicle;
import com.dawnestofbread.vehiclemod.collision.OBB;
import com.dawnestofbread.vehiclemod.geo.Bone;
import com.dawnestofbread.vehiclemod.geo.LinearColour;
import com.dawnestofbread.vehiclemod.geo.Transform;
import com.dawnestofbread.vehiclemod.utils.MathUtils;
import com.dawnestofbread.vehiclemod.utils.Wheel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Intersectionf;

import java.util.HashMap;
import java.util.List;

import static com.dawnestofbread.vehiclemod.WheeledVehicle.LOGGER;
import static com.dawnestofbread.vehiclemod.utils.MathUtils.dInterpTo;
import static com.dawnestofbread.vehiclemod.utils.MathUtils.mapDoubleRangeClamped;
import static com.dawnestofbread.vehiclemod.utils.Rendering.*;
import static com.dawnestofbread.vehiclemod.utils.VectorUtils.*;

public abstract class AbstractWheeledRenderer<T extends WheeledVehicle> extends AbstractVehicleRenderer<T> {
    public AbstractWheeledRenderer(EntityRendererProvider.Context context, ResourceLocation modelLocation) {
        super(context, modelLocation);
    }

    protected double calculateSteering(Transform boneTransform, T entity, float partialTick) {
        return dInterpTo(boneTransform.getRotY(),-entity.getSteering() * entity.getSteeringAngle() * mapDoubleRangeClamped(entity.getTraction(), 0, .5, -1, 1), 1.5, partialTick);
    }

    @Override
    public void preRender(@NotNull T entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        super.preRender(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
        drawDebug(entity, poseStack, buffer);
    }

    @Override
    public void onRender(@NotNull T entity, float entityYaw, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay, LinearColour tint, HashMap<Bone, Transform> boneTransformHashMap) {

    }

    @Override
    public void postRender(@NotNull T entity, float entityYaw, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay, LinearColour tint, HashMap<Bone, Transform> boneTransforms) {
        final double deltaTime = partialTick * 0.05f;
        for (Wheel wheel : entity.getWheels()) {
            Transform t = boneTransforms.get(getModel().getBone("wheel" + entity.getWheels().indexOf(wheel)));
            t.setRotX(t.getRotX() + Math.toDegrees(wheel.angularVelocity / 2.5) * deltaTime);
            t.setPosY(-wheel.springLength);
            if (wheel.affectedBySteering) t.setRotY(calculateSteering(t, entity, partialTick));
        }
    }

    @Override
    public void onSetupBoneTransform(float partialTick, T entity, String boneName, Transform boneTransform) {
        // Do not change this to a switch statement, it'll absolutely bork it up
        if (boneName.equals("body")) {
            handleBody(entity, boneTransform, partialTick);
        }
        if (boneName.equals("root")) {
            handleRoot(entity, boneTransform, partialTick);
        }
        if (boneName.contains("exhaust")) {
            handleExhaust(entity, boneTransform, partialTick);
        }
        if (boneName.equals("steeringComponent")) {
            boneTransform.setRotY(calculateSteering(boneTransform, entity, partialTick));
        }
    }

    protected void handleRoot(T entity, Transform root, float partialTick) {
    }
    protected void handleExhaust(T entity, Transform exhaust, float partialTick) {
        if (entity.isEngineOn()) exhaust.setRotation(Math.random() * 2.5, Math.random() * 2.5, Math.random() * 2.5);
        else exhaust.setRotation(0,0,0);
    }
    protected void handleBody(T entity, Transform body, double partialTick) {
        double newXBodyRot = MathUtils.suspensionEasing(body.getRotX(), -entity.getWeightTransferX() * entity.getMaxBodyPitch(), 1, partialTick);
        double newZBodyRot = MathUtils.suspensionEasing(body.getRotZ(), -entity.getWeightTransferZ() * entity.getMaxBodyRoll(), 1, partialTick);
        body.setRotX(newXBodyRot);
        body.setRotZ(newZBodyRot);
        entity.passengerTransform().addRotation(newXBodyRot, 0, newZBodyRot);
    }

    protected void drawDebug(T entity, PoseStack poseStack, MultiBufferSource bufferSource) {
        // Create a debug render buffer
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());
        poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot()));
            for (Wheel wheel : entity.getWheels()) {
                Vec3 offsetStart = wheel.currentRelativePosition.scale(0.5).add(new Vec3(-wheel.width / 2, -wheel.radius, -wheel.radius).xRot(-entity.getXRot() * ((float) Math.PI / 180F)));
                Vec3 offsetEnd = wheel.currentRelativePosition.scale(0.5).add(new Vec3(wheel.width / 2, wheel.radius, wheel.radius).xRot(-entity.getXRot() * ((float) Math.PI / 180F)));

                Vec3 lineTraceStart = rotateVectorToEntitySpaceXOnly(wheel.startingRelativePosition.scale(.5).add(0, -wheel.springMinLength, 0), entity);
                Vec3 lineTraceEnd = rotateVectorToEntitySpaceXOnly(wheel.startingRelativePosition.scale(.5).add(0, wheel.springMaxLength, 0), entity);
                drawLine(vertexConsumer, poseStack.last(), lineTraceStart, lineTraceEnd, wheel.onGround ? 0 : 1, wheel.onGround ? 1 : 0, 0);

                Vec3 lineTraceStart1 = wheel.startingRelativePosition.scale(.5).add(0, entity.climbAmount * 1.01, 0);
                Vec3 lineTraceEnd1 = lineTraceStart1.add(0, -entity.wheelBase - wheel.radius, 0).add(new Vec3(0, 0, wheel.radius * 1.5 * (entity.getForwardSpeed() > 0 ? 1f : entity.getForwardSpeed() < 0 ? -1f : 0f)));
                drawLine(vertexConsumer, poseStack.last(), lineTraceStart1, lineTraceEnd1, 0, 0, 1);

                poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot()));
                drawBox(poseStack, vertexConsumer, new AABB(wheel.currentRelativePosition.x - .25, wheel.currentRelativePosition.y - .25, wheel.currentRelativePosition.z - .25, wheel.currentRelativePosition.x + .25, wheel.currentRelativePosition.y + .25, wheel.currentRelativePosition.z + .25), 1, 0, 1);
                poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot()));
            }
            int wheelCount = entity.getWheels().size();
            List<Wheel> frontWheels = entity.getWheels().subList(0, wheelCount / 2); // Front wheels (first half)
            List<Wheel> rearWheels = entity.getWheels().subList(wheelCount / 2, wheelCount); // Rear wheels (second half)

            Vec3 frontMidpoint = entity.calculateMidpointWorld(frontWheels);
            Vec3 rearMidpoint = entity.calculateMidpointWorld(rearWheels);
            drawLine(vertexConsumer, poseStack.last(), rearMidpoint, new Vec3(frontMidpoint.x, rearMidpoint.y, frontMidpoint.z), 1, 1, 0);
            drawLine(vertexConsumer, poseStack.last(), frontMidpoint, new Vec3(frontMidpoint.x, rearMidpoint.y, frontMidpoint.z), 1, 1, 0);
        poseStack.popPose();
        poseStack.pushPose();
            if (entity.getActualCollision() != null) {
                drawOBB(poseStack, vertexConsumer, entity.getCollisionRelative(), 1, 1, 1);
            }
        poseStack.popPose();
    }
}
