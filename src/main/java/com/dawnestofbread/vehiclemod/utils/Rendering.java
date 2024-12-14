package com.dawnestofbread.vehiclemod.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Rendering {
    public static void drawLine(VertexConsumer vertexConsumer, PoseStack.Pose pose, double x1, double y1, double z1, double x2, double y2, double z2, float red, float green, float blue) {
        vertexConsumer.vertex(pose.pose(), (float) x1, (float) y1, (float) z1).color(red, green, blue, 1.0f).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(pose.pose(), (float) x2, (float) y2, (float) z2).color(red, green, blue, 1.0f).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
    }
    public static void drawLine(VertexConsumer vertexConsumer, PoseStack.Pose pose, Vec3 vec1, Vec3 vec2, float red, float green, float blue) {
        vertexConsumer.vertex(pose.pose(), (float) vec1.x, (float) vec1.y, (float) vec1.z).color(red, green, blue, 1.0f).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(pose.pose(), (float) vec2.x, (float) vec2.y, (float) vec2.z).color(red, green, blue, 1.0f).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
    }
    public static void renderBox(PoseStack poseStack, VertexConsumer vertexConsumer, AABB aabb, float red, float green, float blue) {
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
}
