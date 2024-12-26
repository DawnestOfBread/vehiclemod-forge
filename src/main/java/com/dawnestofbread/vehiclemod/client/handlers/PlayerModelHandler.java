package com.dawnestofbread.vehiclemod.client.handlers;

import com.dawnestofbread.vehiclemod.AbstractVehicle;
import com.dawnestofbread.vehiclemod.utils.Seat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerModelHandler {
    @SubscribeEvent
    public void onPreRender(RenderPlayerEvent.Pre event)
    {
        Player player = event.getEntity();
        Entity ridden = player.getVehicle();
        if(ridden instanceof AbstractVehicle vehicle)
        {
            this.applyPassengerTransformations(vehicle, player, event.getPoseStack(), event.getPartialTick());
        }
    }

    private void applyPassengerTransformations(AbstractVehicle vehicle, Player player, PoseStack poseStack, float partialTick)
    {
        int seatIndex = vehicle.SeatManager.indexOf(player.getUUID());
        if(seatIndex == -1) return;

        Seat seat = vehicle.getSeats()[seatIndex];
        poseStack.mulPose(Axis.YP.rotationDegrees(-vehicle.getYRot()));
        poseStack.translate(0, -seat.seatOffset.y(), 0);
        poseStack.mulPose(Axis.XP.rotationDegrees((float) vehicle.passengerTransform().getRotX()));
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) vehicle.passengerTransform().getRotZ()));
        poseStack.translate(0, seat.seatOffset.y(), 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(vehicle.getYRot()));
    }
}
