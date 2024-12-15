package com.dawnestofbread.vehiclemod.geo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/*
 * Base renderer for all the vehicles based on GeoRenderer, created because of problems caused by GeckoLib
 */
public abstract class BedrockEntityRenderer<T extends Entity> extends EntityRenderer<T> {

    private final BedrockModel model;
    protected final RenderType renderType = RenderType.solid();
    private VertexConsumer buffer;
    private Entity entity;

    public BedrockEntityRenderer(EntityRendererProvider.Context context, ResourceLocation modelLocation) {
        super(context);
        this.model = new BedrockModel(modelLocation);
    }

    public BedrockModel getModel() {
        return model;
    }

    public VertexConsumer getBuffer() {
        return buffer;
    }

    public Entity getEntity() {
        return entity;
    }

    protected void setEntity(Entity entity) {
        this.entity = entity;
    }

    public RenderType getRenderType() {
        return renderType;
    }

    @Override
    public void render(@NotNull T entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);

        if (model == null) {
            return;
        }
        setEntity(entity);
        LinearColour tint = getTint(entity, partialTick, packedLight);
        int packedOverlay = getPackedOverlay(entity, 0, partialTick);
        renderBedrockModel(poseStack, buffer, partialTick, packedLight, packedOverlay, tint);
    }

    private void renderBedrockModel(PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay, LinearColour tint) {
        for (Bone bone : model.getTopLevelBones()) {
            buffer = bufferSource.getBuffer(renderType);
            renderBone(poseStack, bone, renderType, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, tint);
        }
    }

    private void renderBone(PoseStack poseStack, Bone bone, RenderType renderType, MultiBufferSource bufferSource,
                            VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
                            int packedOverlay, LinearColour tint) {
        poseStack.pushPose();
        RenderUtils.preparePoseStackForBone(poseStack, bone);
        renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, tint);

        renderChildBones(poseStack, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, tint);
        poseStack.popPose();
    }

    private void renderChildBones(PoseStack poseStack, Bone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                                  boolean isReRender, float partialTick, int packedLight, int packedOverlay, LinearColour tint) {
        for (Bone childBone : model.getChildrenOfBone(bone.getName())) {
            renderBone(poseStack, childBone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, tint);
        }
    }

    private void renderCubesOfBone(PoseStack poseStack, Bone bone, VertexConsumer buffer, int packedLight,
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

            /* TODO Fix normals */
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
    public @NotNull ResourceLocation getTextureLocation(@NotNull Entity entity) {
        return new ResourceLocation("vehiclemod", "textures/test.png");
    }
}

