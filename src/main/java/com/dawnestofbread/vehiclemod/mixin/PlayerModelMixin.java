package com.dawnestofbread.vehiclemod.mixin;

import com.dawnestofbread.vehiclemod.AbstractVehicle;
import com.dawnestofbread.vehiclemod.animation.PlayerPose;
import com.dawnestofbread.vehiclemod.geo.Transform;
import com.dawnestofbread.vehiclemod.utils.Seat;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public class PlayerModelMixin {
    @Unique
    protected PlayerPose vehiclemod$basePose = new PlayerPose()
            .withLeftArm(new Transform().setPosition(-5,-2,0))
            .withRightArm(new Transform().setPosition(5,-2,0))
            .withLeftLeg(new Transform().setPosition(-2,-12,0))
            .withRightLeg(new Transform().setPosition(2,-12,0));
    @Unique
    /* Used to limit how many times we reapply the base pose when not in a vehicle */
    protected boolean vehiclemod$doOnceApplyBasePose = false;
    @Shadow
    public ModelPart head;
    @Shadow
    public ModelPart body;
    @Shadow
    public ModelPart leftArm;
    @Shadow
    public ModelPart rightArm;
    @Shadow
    public ModelPart leftLeg;
    @Shadow
    public ModelPart rightLeg;

    @Inject(at = @At("TAIL"), method = "setupAnim", cancellable = true)
    public void setupAnim(LivingEntity entity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (entity instanceof Player player && entity.getVehicle() instanceof AbstractVehicle vehicle) {
            vehiclemod$doOnceApplyBasePose = true;
            int seatIndex = vehicle.SeatManager.indexOf(player.getUUID());
            if (seatIndex == -1) return;
            Seat seat = vehicle.getSeats()[seatIndex];
            if (seat.animationSet == null) return;
            PlayerPose pose = seat.animationSet.evaluate(player, vehicle);

            vehiclemod$applyPartPose(head, pose.head(), vehiclemod$basePose.head());
            vehiclemod$applyPartPose(body, pose.body(), vehiclemod$basePose.body());
            vehiclemod$applyPartPose(leftArm, pose.leftArm(), vehiclemod$basePose.leftArm());
            vehiclemod$applyPartPose(rightArm, pose.rightArm(), vehiclemod$basePose.rightArm());
            vehiclemod$applyPartPose(leftLeg, pose.leftLeg(), vehiclemod$basePose.leftLeg());
            vehiclemod$applyPartPose(rightLeg, pose.rightLeg(), vehiclemod$basePose.rightLeg());
        } else if (vehiclemod$doOnceApplyBasePose) {
            /*
             When not in a vehicle and pose wasn't reset since leaving it, reset pose and prevent resetting it again
             Sounds convoluted, but it really isn't; I'm leaving this comment here in case somebody wants to create a better solution
            */
            vehiclemod$doOnceApplyBasePose = false;
            head.resetPose();
            body.resetPose();
            leftArm.resetPose();
            rightArm.resetPose();
            leftLeg.resetPose();
            rightLeg.resetPose();
        }
    }

    @Unique
    private void vehiclemod$applyPartPose(ModelPart part, Transform transform, Transform baseTransform) {
        // The humanoid model has really weird pivot points/origins,
        // we need to transform to the correct ones first and then apply our pose
        Transform t = transform.setPosition(transform.getPosition().add(baseTransform.getPosition())).setRotation(transform.getRotation().add(baseTransform.getRotation()));

        part.setPos((float) -t.getXPos(), (float) -t.getYPos(), (float) -t.getZPos());

        part.xRot = (float) Math.toRadians(-t.getRotX());
        part.yRot = (float) Math.toRadians(t.getRotY());
        part.zRot = (float) Math.toRadians(-t.getRotZ());
    }
}
