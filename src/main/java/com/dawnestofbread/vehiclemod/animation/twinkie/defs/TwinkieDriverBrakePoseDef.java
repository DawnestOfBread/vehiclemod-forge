package com.dawnestofbread.vehiclemod.animation.twinkie.defs;

import com.dawnestofbread.vehiclemod.AbstractVehicle;
import com.dawnestofbread.vehiclemod.animation.PlayerPose;
import com.dawnestofbread.vehiclemod.animation.PoseDefinition;
import com.dawnestofbread.vehiclemod.geo.Transform;
import com.dawnestofbread.vehiclemod.vehicles.entities.Twinkie;
import net.minecraft.world.entity.player.Player;

public class TwinkieDriverBrakePoseDef extends PoseDefinition<Twinkie> {
    @Override
    public PlayerPose getPose(Player player, Twinkie vehicle) {
        double a = 1;
        Transform headT = new Transform().setPosition(0,0,4.75 * a).setRotation(-42.5 * a,0,0);
        Transform bodyT = new Transform().setPosition(0,0,4.35 * a).setRotation(-25 * a, 0, 0);
        Transform lArmT = new Transform().setPosition(-0.4081,1.2654,0.3514).setRotation(64.9373, -4.768, -9.007);
        Transform rArmT = new Transform().setPosition(0.279,1.2811,0.3514).setRotation(64.9373, 4.768, 9.007);
        Transform lLegT = new Transform().setPosition(0.0327,1.9571,3.0649).setRotation(10.0132, -5.7915, 4.056);
        Transform rLegT = new Transform().setPosition(-0.0327,1.9571,3.0649).setRotation(10.0132, 5.7915, -4.056);
        return new PlayerPose()
                .withHead(headT)
                .withBody(bodyT)
                .withLeftArm(lArmT)
                .withRightArm(rArmT)
                .withLeftLeg(lLegT)
                .withRightLeg(rLegT);
    }
}
