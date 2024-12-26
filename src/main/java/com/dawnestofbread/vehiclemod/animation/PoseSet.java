package com.dawnestofbread.vehiclemod.animation;

import com.dawnestofbread.vehiclemod.AbstractVehicle;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class PoseSet<T extends AbstractVehicle> {
    protected Map<String, PoseDefinition> poses;
    protected PlayerPose last = new PlayerPose();
    public abstract PlayerPose evaluate(Player player, T vehicle);

    public PoseSet() {
        this.poses = new HashMap<>();
    }
}
