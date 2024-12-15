package com.dawnestofbread.vehiclemod.client.camera;

import net.minecraft.client.Camera;
import net.minecraft.client.player.LocalPlayer;

public class LaggedBoomArmCameraEntity extends Camera {
    final LocalPlayer target;

    public LaggedBoomArmCameraEntity(LocalPlayer target) {
        super();
        this.target = target;
    }
}
