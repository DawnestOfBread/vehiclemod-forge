package com.dawnestofbread.vehiclemod.mixin;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerModel.class)
public class PlayerModelMixin {
    @Shadow
    public ModelPart body;
    @Shadow
    public ModelPart leftSleeve;

    @Inject(at = @At("TAIL"), method = "setupAnim", cancellable = true)
    public void setupAnim(LivingEntity livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (livingEntity instanceof Player) {
            body.copyFrom(leftSleeve);
        }
    }
}
