package com.dawnestofbread.vehiclemod.animation.twinkie.defs;

import com.dawnestofbread.vehiclemod.AbstractVehicle;
import com.dawnestofbread.vehiclemod.animation.PlayerPose;
import com.dawnestofbread.vehiclemod.animation.PoseDefinition;
import com.dawnestofbread.vehiclemod.geo.Transform;
import net.minecraft.world.entity.player.Player;

public class TwinkiePassenger0IdlePoseDef extends PoseDefinition {
    @Override
    public PlayerPose getPose(Player player, AbstractVehicle vehicle) {
        Transform headT = new Transform().setPosition(0,0,0.75);
        Transform bodyT = new Transform().setPosition(0,0,0.85).setRotation(-5, 0, 0);
        Transform lArmT = new Transform().setPosition(-0.25,-0.9063,0.4226).setRotation(-7.5, 0, -7.5);
        Transform rArmT = new Transform().setPosition(0.25,-0.9063,0.4226).setRotation(-7.5, 0, 7.5);
        Transform lLegT = new Transform().setPosition(-3,6,3).setRotation(35, -17.5, 0);
        Transform rLegT = new Transform().setPosition(3,6,3).setRotation(35, 17.5, 0);
        return new PlayerPose()
                .withHead(headT)
                .withBody(bodyT)
                .withLeftArm(lArmT)
                .withRightArm(rArmT)
                .withLeftLeg(lLegT)
                .withRightLeg(rLegT);
    }
}
