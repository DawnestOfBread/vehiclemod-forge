package com.dawnestofbread.vehiclemod.geo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.GeoCube;

public class RenderUtils {
    public static void preparePoseStackForBone(PoseStack poseStack, Bone bone) {
        //translatePoseToBone(poseStack, bone);
        translatePoseToPivot(poseStack, bone);
        rotatePose(poseStack, bone);
        scalePoseToBone(poseStack, bone);
        returnPoseFromPivot(poseStack, bone);
    }

    public static void translatePoseToPivot(PoseStack poseStack, Cube cube) {
        Vec3 pivot = cube.getPivot();
        poseStack.translate(pivot.x(), pivot.y(), pivot.z());
    }

    public static void translatePoseToBone(PoseStack poseStack, Bone bone) {
        poseStack.translate(-bone.getPivot().x(), bone.getPivot().y(), bone.getPivot().z());
    }

    public static void translatePoseToPivot(PoseStack poseStack, Bone bone) {
        poseStack.translate(bone.getPivot().x(), bone.getPivot().y(), bone.getPivot().z());
    }

    public static void rotatePose(PoseStack poseStack, Bone bone) {
        if (bone.getRotation().x() != 0)
            poseStack.mulPose(Axis.XP.rotation(bone.getRotationF().x()));

        if (bone.getRotation().y() != 0)
            poseStack.mulPose(Axis.YP.rotation(bone.getRotationF().y()));

        if (bone.getRotation().z() != 0)
            poseStack.mulPose(Axis.ZP.rotation(bone.getRotationF().z()));
    }

    public static void rotatePose(PoseStack poseStack, Cube cube) {
        Vec3 rotation = cube.getRotation();

        poseStack.mulPose(new Quaternionf().rotationXYZ(0, 0, (float) rotation.z()));
        poseStack.mulPose(new Quaternionf().rotationXYZ(0, (float) rotation.y(), 0));
        poseStack.mulPose(new Quaternionf().rotationXYZ((float) rotation.x(), 0, 0));
    }

    public static void scalePoseToBone(PoseStack poseStack, Bone bone) {
        poseStack.scale(bone.getScaleF().x(), bone.getScaleF().y(), bone.getScaleF().z());
    }

    public static void returnPoseFromPivot(PoseStack poseStack, Bone bone) {
        poseStack.translate(-bone.getPivot().x(), -bone.getPivot().y(), -bone.getPivot().z());
    }

    public static void returnPoseFromPivot(PoseStack poseStack, Cube cube) {
        Vec3 pivot = cube.getPivot();

        poseStack.translate(-pivot.x(), -pivot.y(), -pivot.z());
    }

    public static void fixZeroWidthCube(Cube cube, Vector3f normal) {
        if (normal.x() < 0 && (cube.getSize().y() == 0 || cube.getSize().z() == 0))
            normal.mul(-1, 1, 1);

        if (normal.y() < 0 && (cube.getSize().x() == 0 || cube.getSize().z() == 0))
            normal.mul(1, -1, 1);

        if (normal.z() < 0 && (cube.getSize().x() == 0 || cube.getSize().y() == 0))
            normal.mul(1, 1, -1);
    }
}
