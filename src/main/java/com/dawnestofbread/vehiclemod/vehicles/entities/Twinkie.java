package com.dawnestofbread.vehiclemod.vehicles.entities;

import com.dawnestofbread.vehiclemod.AbstractMotorcycle;
import com.dawnestofbread.vehiclemod.client.audio.AudioManager;
import com.dawnestofbread.vehiclemod.utils.Curve;
import com.dawnestofbread.vehiclemod.utils.SeatData;
import com.dawnestofbread.vehiclemod.utils.WheelProperties;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

import static com.dawnestofbread.vehiclemod.registries.SoundEventRegistry.*;

public class Twinkie extends AbstractMotorcycle {

    public Twinkie(EntityType<? extends Entity> entityType, Level worldIn) {
        super(entityType, worldIn);
        // This probably could be moved to setupSeats, but meh
        SeatManager = new ArrayList<>(2);
        SeatManager.add(0,UUID.fromString("00000000-0000-0000-0000-000000000000"));
        SeatManager.add(1,UUID.fromString("00000000-0000-0000-0000-000000000000"));

        // WIP Set up collision boxes
        this.collision = new AABB[1];
        this.collision[0] = new AABB(-2.0625,0.75,-5.125, 2.0625,2.6875,0.5);

        // Some values required for maths; when getting them from Blockbench, divide the value by 8!
        this.frontAxle = new Vec3(0, 0.522025, 1.637475);
        this.rearAxle = new Vec3(0, 0.522025, -1.362525);
        this.centreOfGeometry = new Vec3(0,0,0);
        // This is also really important
        this.width = 0.75;
        this.height = 1.1;
        this.length = 2.0625;

        // Purely visual, should be set up to not clip into the tyres
        this.maxBodyPitch = 10;
        this.maxBodyRoll = 22.5;

        // Really important values here, you should try to get them right
        this.idleRPM = 1000;
        this.maxRPM = 5200;
        this.mass = 200;
        this.brakingConstant = 3000;

        // Brake multiplier applied when there's no input
        this.idleBrakeAmount = .175;
        // Unused
        this.corneringStiffness = 60;

        // Torque curve
        List<Double> tempTCurve = new LinkedList<>();
        tempTCurve.add(0, 10d);
        tempTCurve.add(1, 10d);
        tempTCurve.add(2, 40d);
        tempTCurve.add(3, 45d);
        tempTCurve.add(4, 30d);
        tempTCurve.add(5, 15d);
        tempTCurve.add(6, 5d);
        this.transmissionEfficiency = .72;
        this.torqueCurve = new Curve(tempTCurve);

        // Gear setup
        this.differentialRatio = 2.86;
        this.gearRatios = new double[5];
        this.gearRatios[0] = -.5; // Reverse
        this.gearRatios[1] = 1; // Neutral
        this.gearRatios[2] = 2.0; // 1st
        this.gearRatios[3] = 1.12; // 2nd and so on...
        this.gearRatios[4] = 0.71;

        // Used for the automatic gearbox
        this.shiftUpRPM = 4200;
        this.shiftDownRPM = 2700;
        // In seconds
        this.timeToShift = .25;

        // How much the wheels turn; NOT using Ackermann steering geometry
        this.steeringAngle = 27;

        // Traction/grip while turning (0-1 range; 1 meaning great, 0 meaning awful)
        this.traction = 0.6;

        // 0-1
        this.exhaustFumeAmount = .1f;
        this.exhaust = new Vec3[2];
        this.exhaust[0] = new Vec3(0.4375, 0.3715625, -1.1594375);
        this.exhaust[1] = new Vec3(-0.4375, 0.3715625, -1.1594375);

        this.engineSounds = new HashMap<>();
        this.engineSounds.put(AudioManager.SoundType.ENGINE_IDLE, SCOOTER_IDLE.get());
        this.engineSounds.put(AudioManager.SoundType.ENGINE_MOVING, SCOOTER_MOVING.get());
    }

    // Create the seats and set their offsets
    @Override
    protected void setupSeats() {
        Seats = new SeatData[2];

        // This is divided by 16
        SeatData seat0 = new SeatData();
        seat0.seatOffset = new Vec3(0,0.125,-0.2933625);
        Seats[0] = seat0;

        SeatData seat1 = new SeatData();
        seat1.seatOffset = new Vec3(0,0.125,-0.7933625);
        Seats[1] = seat1;
    }

    // Create the wheels and set their parameters
    @Override
    protected void setupWheels() {
        Wheels = new ArrayList<>(2);

        Wheels.add(0, new WheelProperties());
        Wheels.add(1, new WheelProperties());

        // Divide this by 16, because it works better and results in more realistic proportions
        Wheels.get(0).radius = 0.253125;
        Wheels.get(1).radius = 0.253125;

        Wheels.get(0).width = 0.203125;
        Wheels.get(1).width = 0.203125;

        Wheels.get(0).springMinLength = 0.56875;
        Wheels.get(1).springMinLength = 0.56875;
        Wheels.get(0).springMaxLength = 0.63125;
        Wheels.get(1).springMaxLength = 0.63125;

        // And this
        Wheels.get(0).startingRelativePosition = new Vec3(0.002675, 0.522025, 1.637475);
        Wheels.get(1).startingRelativePosition = new Vec3(0.002675, 0.522025, -1.362525);

        Wheels.get(0).affectedBySteering = true;

        Wheels.get(0).mass = 10;
        Wheels.get(1).mass = 10;

        Wheels.get(1).affectedByEngine = true;

        Wheels.get(0).affectedByBrake = true;
        Wheels.get(1).affectedByBrake = true;

        Wheels.get(1).affectedByHandbrake = true;
    }
}
