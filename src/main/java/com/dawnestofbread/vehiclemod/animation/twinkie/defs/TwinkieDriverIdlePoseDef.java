package com.dawnestofbread.vehiclemod.animation.twinkie.defs;

import com.dawnestofbread.vehiclemod.AbstractVehicle;
import com.dawnestofbread.vehiclemod.animation.PlayerPose;
import com.dawnestofbread.vehiclemod.animation.PoseDefinition;
import com.dawnestofbread.vehiclemod.geo.Transform;
import com.dawnestofbread.vehiclemod.vehicles.entities.Twinkie;
import net.minecraft.world.entity.player.Player;

public class TwinkieDriverIdlePoseDef extends PoseDefinition<Twinkie> {
    @Override
    public PlayerPose getPose(Player player, Twinkie vehicle) {
        Transform headT = new Transform().setPosition(0,0,0.75);
        Transform bodyT = new Transform().setPosition(0,0,0.35).setRotation(-5, 0, 0);
        Transform lArmT = new Transform().setPosition(-0.0618,-0.614,-0.2517).setRotation(72.4373, -4.768, -9.007);
        Transform rArmT = new Transform().setPosition(0.03,-0.614,-0.2517).setRotation(72.4373, 4.768, 9.007);
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
