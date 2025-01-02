package com.dawnestofbread.vehiclemod.utils;

import com.dawnestofbread.vehiclemod.collision.OBB;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import static com.dawnestofbread.vehiclemod.utils.VectorUtils.fromVector3f;

public class Rendering {
    public static void drawLine(VertexConsumer vertexConsumer, PoseStack.Pose pose, double x1, double y1, double z1, double x2, double y2, double z2, float red, float green, float blue) {
        vertexConsumer.vertex(pose.pose(), (float) x1, (float) y1, (float) z1).color(red, green, blue, 1.0f).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(pose.pose(), (float) x2, (float) y2, (float) z2).color(red, green, blue, 1.0f).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
    }
    public static void drawLine(VertexConsumer vertexConsumer, PoseStack.Pose pose, Vec3 vec1, Vec3 vec2, float red, float green, float blue) {
        vertexConsumer.vertex(pose.pose(), (float) vec1.x, (float) vec1.y, (float) vec1.z).color(red, green, blue, 1.0f).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(pose.pose(), (float) vec2.x, (float) vec2.y, (float) vec2.z).color(red, green, blue, 1.0f).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
    }
    public static void drawBox(PoseStack poseStack, VertexConsumer vertexConsumer, AABB aabb, float red, float green, float blue) {
        PoseStack.Pose pose = poseStack.last();

        // Bottom edges
        drawLine(vertexConsumer, pose, aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.minY, aabb.minZ, red, green, blue);
        drawLine(vertexConsumer, pose, aabb.maxX, aabb.minY, aabb.minZ, aabb.maxX, aabb.minY, aabb.maxZ, red, green, blue);
        drawLine(vertexConsumer, pose, aabb.maxX, aabb.minY, aabb.maxZ, aabb.minX, aabb.minY, aabb.maxZ, red, green, blue);
        drawLine(vertexConsumer, pose, aabb.minX, aabb.minY, aabb.maxZ, aabb.minX, aabb.minY, aabb.minZ, red, green, blue);

        // Top edges
        drawLine(vertexConsumer, pose, aabb.minX, aabb.maxY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.minZ, red, green, blue);
        drawLine(vertexConsumer, pose, aabb.maxX, aabb.maxY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ, red, green, blue);
        drawLine(vertexConsumer, pose, aabb.maxX, aabb.maxY, aabb.maxZ, aabb.minX, aabb.maxY, aabb.maxZ, red, green, blue);
        drawLine(vertexConsumer, pose, aabb.minX, aabb.maxY, aabb.maxZ, aabb.minX, aabb.maxY, aabb.minZ, red, green, blue);

        // Vertical edges
        drawLine(vertexConsumer, pose, aabb.minX, aabb.minY, aabb.minZ, aabb.minX, aabb.maxY, aabb.minZ, red, green, blue);
        drawLine(vertexConsumer, pose, aabb.maxX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.minZ, red, green, blue);
        drawLine(vertexConsumer, pose, aabb.maxX, aabb.minY, aabb.maxZ, aabb.maxX, aabb.maxY, aabb.maxZ, red, green, blue);
        drawLine(vertexConsumer, pose, aabb.minX, aabb.minY, aabb.maxZ, aabb.minX, aabb.maxY, aabb.maxZ, red, green, blue);
    }
    public static void drawBox(PoseStack poseStack, VertexConsumer vertexConsumer, OBB obb, float red, float green, float blue) {
        PoseStack.Pose pose = poseStack.last();
        Vec3 min = fromVector3f(obb.getCentre().sub(obb.getHalfSize()));
        Vec3 max = fromVector3f(obb.getCentre().add(obb.getHalfSize()));
        poseStack.mulPose(obb.getOrientation());
        poseStack.translate(-obb.getCentre().x, -obb.getCentre().y, -obb.getCentre().z);
        double minX = min.x();
        double minY = min.y();
        double minZ = min.z();

        double maxX = max.x();
        double maxY = max.y();
        double maxZ = max.z();

        // Bottom edges
        drawLine(vertexConsumer, pose, minX, minY, minZ, maxX, minY, minZ, red, green, blue);
        drawLine(vertexConsumer, pose, maxX, minY, minZ, maxX, minY, maxZ, red, green, blue);
        drawLine(vertexConsumer, pose, maxX, minY, maxZ, minX, minY, maxZ, red, green, blue);
        drawLine(vertexConsumer, pose, minX, minY, maxZ, minX, minY, minZ, red, green, blue);

        // Top edges
        drawLine(vertexConsumer, pose, minX, maxY, minZ, maxX, maxY, minZ, red, green, blue);
        drawLine(vertexConsumer, pose, maxX, maxY, minZ, maxX, maxY, maxZ, red, green, blue);
        drawLine(vertexConsumer, pose, maxX, maxY, maxZ, minX, maxY, maxZ, red, green, blue);
        drawLine(vertexConsumer, pose, minX, maxY, maxZ, minX, maxY, minZ, red, green, blue);

        // Vertical edges
        drawLine(vertexConsumer, pose, minX, minY, minZ, minX, maxY, minZ, red, green, blue);
        drawLine(vertexConsumer, pose, maxX, minY, minZ, maxX, maxY, minZ, red, green, blue);
        drawLine(vertexConsumer, pose, maxX, minY, maxZ, maxX, maxY, maxZ, red, green, blue);
        drawLine(vertexConsumer, pose, minX, minY, maxZ, minX, maxY, maxZ, red, green, blue);
    }

    public static void drawOBB(PoseStack poseStack, VertexConsumer buffer, OBB obb, float red, float green, float blue) {
        poseStack.pushPose();
        poseStack.translate(obb.getCentre().x, obb.getCentre().y, obb.getCentre().z);
        poseStack.mulPose(obb.getOrientation());
        Vector3f half = obb.getHalfSize();
        LevelRenderer.renderLineBox(poseStack, buffer, -half.x, -half.y, -half.z, half.x, half.y, half.z, red, green, blue, 1);
        poseStack.popPose();
    }

    private static boolean connected(Vector3f a, Vector3f b, OBB obb) {
        int differingAxes = 0;
        Vector3f halfExtents = obb.getHalfSize();
        if (Math.abs(a.x - b.x) > halfExtents.x * 1.01) differingAxes++;
        if (Math.abs(a.y - b.y) > halfExtents.y * 1.01) differingAxes++;
        if (Math.abs(a.z - b.z) > halfExtents.z * 1.01) differingAxes++;
        return differingAxes == 1; // Connected if only one axis differs
    }
}
