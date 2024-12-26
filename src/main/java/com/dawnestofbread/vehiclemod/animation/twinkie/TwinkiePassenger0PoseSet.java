package com.dawnestofbread.vehiclemod.animation.twinkie;

import com.dawnestofbread.vehiclemod.animation.PlayerPose;
import com.dawnestofbread.vehiclemod.animation.PoseSet;
import com.dawnestofbread.vehiclemod.animation.twinkie.defs.TwinkiePassenger0IdlePoseDef;
import com.dawnestofbread.vehiclemod.vehicles.entities.Twinkie;
import net.minecraft.world.entity.player.Player;

public class TwinkiePassenger0PoseSet extends PoseSet<Twinkie> {
    public TwinkiePassenger0PoseSet() {
        super();
        this.poses.put("idle", new TwinkiePassenger0IdlePoseDef());
    }

    @Override
    public PlayerPose evaluate(Player player, Twinkie vehicle) {
        return poses.get("idle").getPose(player, vehicle);
    }
}
