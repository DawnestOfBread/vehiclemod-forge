package com.dawnestofbread.vehiclemod.animation.twinkie;

import com.dawnestofbread.vehiclemod.animation.PlayerPose;
import com.dawnestofbread.vehiclemod.animation.PoseSet;
import com.dawnestofbread.vehiclemod.animation.twinkie.defs.TwinkieDriverBrakePoseDef;
import com.dawnestofbread.vehiclemod.animation.twinkie.defs.TwinkieDriverBrakeRPoseDef;
import com.dawnestofbread.vehiclemod.animation.twinkie.defs.TwinkieDriverIdlePoseDef;
import com.dawnestofbread.vehiclemod.utils.MathUtils;
import com.dawnestofbread.vehiclemod.vehicles.entities.Twinkie;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class TwinkieDriverPoseSet extends PoseSet<Twinkie> {
    public TwinkieDriverPoseSet() {
        super();
        this.poses.put("idle", new TwinkieDriverIdlePoseDef());
        this.poses.put("brake", new TwinkieDriverBrakePoseDef());
        this.poses.put("brakeR", new TwinkieDriverBrakeRPoseDef());
    }
    private double lastAlpha;

    @Override
    public PlayerPose evaluate(Player player, Twinkie vehicle) {
        double alpha = MathUtils.suspensionEasing(lastAlpha, (-vehicle.getWeightTransferX() * vehicle.getMaxBodyPitch()) * (vehicle.getForwardSpeed() / 20), 1, Minecraft.getInstance().getPartialTick());
        lastAlpha = alpha;
        PlayerPose p = this.poses.get("idle").getPose(player, vehicle).blendTwo(this.poses.get("brakeR").getPose(player,vehicle), this.poses.get("brake").getPose(player, vehicle), alpha / vehicle.getMaxBodyPitch() * .5);
        last = p;
        return p;
    }
}
