package com.dawnestofbread.vehiclemod;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class AbstractMotorcycle extends WheeledVehicle {
    protected double wheelieAngularVelocity;
    protected double wheelieAngularAcceleration;
    protected AbstractMotorcycle(EntityType<? extends Entity> entityType, Level worldIn) {
        super(entityType, worldIn);
    }

}
