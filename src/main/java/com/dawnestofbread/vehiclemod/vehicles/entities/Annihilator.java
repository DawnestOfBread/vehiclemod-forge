package com.dawnestofbread.vehiclemod.vehicles.entities;

import com.dawnestofbread.vehiclemod.WheeledVehicle;
import com.dawnestofbread.vehiclemod.client.audio.AudioManager;
import com.dawnestofbread.vehiclemod.utils.Curve;
import com.dawnestofbread.vehiclemod.utils.SeatData;
import com.dawnestofbread.vehiclemod.utils.WheelData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;

import java.util.*;

public class Annihilator extends WheeledVehicle {

    public Annihilator(EntityType<? extends Entity> entityType, Level worldIn) {
        super(entityType, worldIn);
        // This probably could be moved to setupSeats, but meh
        SeatManager = new ArrayList<>(4);
        SeatManager.add(0,UUID.fromString("00000000-0000-0000-0000-000000000000"));
        SeatManager.add(1,UUID.fromString("00000000-0000-0000-0000-000000000000"));
        SeatManager.add(2,UUID.fromString("00000000-0000-0000-0000-000000000000"));
        SeatManager.add(3,UUID.fromString("00000000-0000-0000-0000-000000000000"));

        // WIP Set up collision boxes
        this.collision = new AABB[1];
        this.collision[0] = new AABB(-2.0625,0.75,-5.125, 2.0625,2.6875,0.5);

        // Some values required for maths; when getting them from Blockbench, divide the value by 8!
        this.rearAxle = new Vec3(0, 0.838125, -3.004375);
        this.frontAxle = new Vec3(0, 0.838125, 3.745625);
        this.centreOfGeometry = new Vec3(0,0,0);
        // This is also really important
        this.width = 2;
        this.height = 2.1875;
        this.length = 5.0625;

        // Purely visual, should be set up to not clip into the tyres
        this.maxBodyPitch = 5;
        this.maxBodyRoll = 10;

        // Really important values here, you should try to get them right
        this.idleRPM = 600;
        this.maxRPM = 6000;
        this.mass = 1900;
        this.brakingConstant = 10000;

        // Brake multiplier applied when there's no input
        this.idleBrakeAmount = .25;
        // Unused
        this.corneringStiffness = 60;

        // Torque curve
        List<Double> tempTCurve = new LinkedList<>();
        tempTCurve.add(0, 100d);
        tempTCurve.add(1, 100d);
        tempTCurve.add(2, 290d);
        tempTCurve.add(3, 310d);
        tempTCurve.add(4, 370d);
        tempTCurve.add(5, 400d);
        tempTCurve.add(6, 300d);
        this.transmissionEfficiency = .7;
        this.torqueCurve = new Curve(tempTCurve);

        // Gear setup
        this.differentialRatio = 3.42;
        this.gearRatios = new double[7];
        this.gearRatios[0] = -1.5; // Reverse
        this.gearRatios[1] = 0.01; // Neutral
        this.gearRatios[2] = 2.66; // 1st
        this.gearRatios[3] = 1.78; // 2nd and so on...
        this.gearRatios[4] = 1.30;
        this.gearRatios[5] = 1.0;
        this.gearRatios[6] = 0.74;

        // Used for the automatic gearbox
        this.shiftUpRPM = 3000;
        this.shiftDownRPM = 2500;
        // In seconds
        this.timeToShift = .15;

        // How much the wheels turn; NOT using Ackermann steering geometry
        this.steeringAngle = 35;

        // Traction/grip while turning (0-1 range; 1 meaning great, 0 meaning awful)
        this.traction = 0.73;

        // 0-1
        this.exhaustFumeAmount = .8f;
        this.exhaust = new Vec3[1];
        this.exhaust[0] = new Vec3(-1.114375, 0.920625, -5.2675);

        this.engineSounds = new HashMap<>();
    }

    // Create the seats and set their offsets
    @Override
    protected void setupSeats() {
        Seats = new SeatData[4];

        // This is divided by 16
        SeatData seat0 = new SeatData();
        seat0.seatOffset = new Vec3(0.40625,0.090625,1.88125 - 1.5625);
        Seats[0] = seat0;

        SeatData seat1 = new SeatData();
        seat1.seatOffset = new Vec3(-0.40625,0.090625,1.88125 - 1.5625);
        Seats[1] = seat1;

        SeatData seat2 = new SeatData();
        seat2.seatOffset = new Vec3(0.6, 0.6, .7984375 - 1.5625);
        seat2.yawOffset = 90;
        Seats[2] = seat2;

        SeatData seat3 = new SeatData();
        seat3.seatOffset = new Vec3(-0.6, 0.6, .7984375 - 1.8125);
        seat3.yawOffset = -90;
        Seats[3] = seat3;
    }

    // Create the wheels and set their parameters
    @Override
    protected void setupWheels() {
        Wheels = new WheelData[4];

        Wheels[0] = new WheelData();
        Wheels[1] = new WheelData();
        Wheels[2] = new WheelData();
        Wheels[3] = new WheelData();

        // Divide this by 16, because it works better and results in more realistic proportions
        Wheels[0].radius = 0.390625;
        Wheels[1].radius = 0.390625;
        Wheels[2].radius = 0.390625;
        Wheels[3].radius = 0.390625;

        Wheels[0].width = 0.3125;
        Wheels[1].width = 0.3125;
        Wheels[2].width = 0.3125;
        Wheels[3].width = 0.3125;

        Wheels[0].suspensionRaise = -0.05;
        Wheels[1].suspensionRaise = -0.05;
        Wheels[2].suspensionRaise = -0.05;
        Wheels[3].suspensionRaise = -0.05;
        Wheels[0].suspensionDrop = 0.12;
        Wheels[1].suspensionDrop = 0.12;
        Wheels[2].suspensionDrop = 0.12;
        Wheels[3].suspensionDrop = 0.12;

        // And this
        Wheels[0].startingRelativePosition = new Vec3(1.9125, 0.803125, 3.747775);
        Wheels[1].startingRelativePosition = new Vec3(-1.9125, 0.803125, 3.747775);
        Wheels[2].startingRelativePosition = new Vec3(1.9125, 0.803125, -3.002225);
        Wheels[3].startingRelativePosition = new Vec3(-1.9125, 0.803125, -3.002225);

        Wheels[0].affectedBySteering = true;
        Wheels[1].affectedBySteering = true;

        Wheels[0].mass = 50;
        Wheels[1].mass = 50;
        Wheels[2].mass = 50;
        Wheels[3].mass = 50;

        Wheels[2].affectedByEngine = true;
        Wheels[3].affectedByEngine = true;

        Wheels[2].affectedByBrake = true;
        Wheels[3].affectedByBrake = true;

        Wheels[2].affectedByHandbrake = true;
        Wheels[3].affectedByHandbrake = true;
    }
}
