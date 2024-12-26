package com.dawnestofbread.vehiclemod.animation;

import com.dawnestofbread.vehiclemod.AbstractVehicle;
import net.minecraft.world.entity.player.Player;

public abstract class PoseDefinition<T extends AbstractVehicle> {
    public abstract PlayerPose getPose(Player player, T vehicle);
}
