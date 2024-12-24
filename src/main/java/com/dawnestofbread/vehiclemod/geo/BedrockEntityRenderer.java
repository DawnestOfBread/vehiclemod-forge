package com.dawnestofbread.vehiclemod.geo;

import com.dawnestofbread.vehiclemod.AbstractVehicle;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.HashMap;

/*
 * Base renderer for all the vehicles based on GeoRenderer, created because of problems caused by GeckoLib
 */
public abstract class BedrockEntityRenderer<T extends AbstractVehicle> extends EntityRenderer<T> {
    protected final RenderType renderType;
    private final BedrockModel model;
    protected Vector3f poseStackRotation = new Vector3f().zero();
    private VertexConsumer buffer;
    private T entity;
    public BedrockEntityRenderer(EntityRendererProvider.Context context, ResourceLocation modelLocation) {
        super(context);
        this.model = new BedrockModel(modelLocation);
        renderType = RenderType.entityCutoutNoCull(getTextureLocation(entity));
    }

    public BedrockModel getModel() {
        return model;
    }

    public VertexConsumer getBuffer() {
        return buffer;
    }

    public RenderType getRenderType() {
        return renderType;
    }

    @Override
    public void render(@NotNull T entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        preRender(entity, entityYaw, partialTick, poseStack, buffer, packedLight);

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);

        if (model == null) return;
        this.entity = entity;

        poseStack.mulPose(Axis.YP.rotationDegrees(-entityYaw));

        LinearColour tint = getTint(entity, partialTick, packedLight);
        int packedOverlay = getPackedOverlay(entity, 0, partialTick);
        onRender(entity, entityYaw, poseStack, buffer, partialTick, packedLight, packedOverlay, tint, entity.getBoneTransforms());
        renderBedrockModel(entity, poseStack, buffer, partialTick, packedLight, packedOverlay, tint, entity.getBoneTransforms());

        postRender(entity, entityYaw, poseStack, buffer, partialTick, packedLight, packedOverlay, tint, entity.getBoneTransforms());
        poseStack.popPose();
    }
    public void preRender(@NotNull T entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {};
    public void onRender(@NotNull T entity, float entityYaw, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay, LinearColour tint, HashMap<Bone, Transform> boneTransformHashMap) {};
    public void postRender(@NotNull T entity, float entityYaw, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay, LinearColour tint, HashMap<Bone, Transform> boneTransformHashMap) {};

    private void renderBedrockModel(@NotNull T entity, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay, LinearColour tint, HashMap<Bone, Transform> boneTransformHashMap) {
        for (Bone bone : model.getRootBones()) {
            buffer = bufferSource.getBuffer(renderType);
            renderBone(entity, poseStack, bone, renderType, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, tint, boneTransformHashMap);
        }
    }

    public abstract void onSetupBoneTransform(float partialTick, T entity, String boneName, Transform boneTransform);

    private void renderBone(@NotNull T entity, PoseStack poseStack, Bone bone, RenderType renderType, MultiBufferSource bufferSource,
                            VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
                            int packedOverlay, LinearColour tint, HashMap<Bone, Transform> boneTransformHashMap) {
        poseStack.pushPose();
            boneTransformHashMap.computeIfAbsent(bone, b -> new Transform());
            onSetupBoneTransform(partialTick, entity, bone.getName(), boneTransformHashMap.get(bone));
            RenderUtils.preparePoseStackForBone(poseStack, bone, boneTransformHashMap.get(bone));
            RenderUtils.prepareBoneOffsets(poseStack, bone, boneTransformHashMap.get(bone));
            renderCubesOfBone(entity, poseStack, bone, buffer, packedLight, packedOverlay, tint);
            renderChildBones(entity, poseStack, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, tint, boneTransformHashMap);
        poseStack.popPose();
    }

    private void renderChildBones(@NotNull T entity, PoseStack poseStack, Bone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                                  boolean isReRender, float partialTick, int packedLight, int packedOverlay, LinearColour tint, HashMap<Bone, Transform> boneTransformHashMap) {
        for (Bone childBone : model.getChildrenOfBone(bone.getName())) {
            renderBone(entity, poseStack, childBone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, tint, boneTransformHashMap);
        }
    }

    private void renderCubesOfBone(@NotNull T entity, PoseStack poseStack, Bone bone, VertexConsumer buffer, int packedLight,
                                   int packedOverlay, LinearColour tint) {
        if (bone.isHidden())
            return;

        for (Cube cube : bone.getCubes()) {
            poseStack.pushPose();
            renderCube(poseStack, cube, buffer, packedLight, packedOverlay, tint);
            poseStack.popPose();
        }
    }

    private void renderCube(PoseStack poseStack, Cube cube, VertexConsumer buffer, int packedLight,
                            int packedOverlay, LinearColour tint) {
        RenderUtils.translatePoseToPivot(poseStack, cube);
        RenderUtils.rotatePose(poseStack, cube);
        RenderUtils.returnPoseFromPivot(poseStack, cube);

        Matrix3f normalisedPose = poseStack.last().normal();
        Matrix4f pose = poseStack.last().pose();

        for (Quad quad : cube.getQuads()) {
            if (quad == null)
                continue;

            Vector3f normal = normalisedPose.transform(new Vector3f(quad.normal()));

            RenderUtils.fixZeroWidthCube(cube, normal);
            createVertices(quad, pose, normal, buffer, packedLight, packedOverlay, tint);
        }
    }

    private void createVertices(Quad quad, Matrix4f poseState, Vector3f normal, VertexConsumer buffer,
                                int packedLight, int packedOverlay, LinearColour tint) {
        for (Vertex vertex : quad.vertices()) {
            Vector3f position = vertex.position();
            Vector4f vector4f = poseState.transform(new Vector4f(position.x(), position.y(), position.z(), 1.0f));

            buffer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), tint.getR(), tint.getG(), tint.getB(), tint.getA(), vertex.texU(),
                    vertex.texV(), packedOverlay, packedLight, normal.x(), normal.y(), normal.z());
        }
    }

    public LinearColour getTint(Entity entity, float partialTick, int packedLight) {
        return LinearColour.WHITE;
    }

    public int getPackedOverlay(Entity entity, float u, float partialTick) {
        return OverlayTexture.NO_OVERLAY;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T entity) {
        return new ResourceLocation("vehiclemod", "textures/test.png");
    }
}

