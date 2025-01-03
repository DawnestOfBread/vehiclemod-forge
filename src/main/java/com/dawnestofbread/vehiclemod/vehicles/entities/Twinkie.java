package com.dawnestofbread.vehiclemod.vehicles.entities;

import com.dawnestofbread.vehiclemod.AbstractMotorcycle;
import com.dawnestofbread.vehiclemod.animation.twinkie.TwinkieDriverPoseSet;
import com.dawnestofbread.vehiclemod.animation.twinkie.TwinkiePassenger0PoseSet;
import com.dawnestofbread.vehiclemod.client.audio.AudioManager;
import com.dawnestofbread.vehiclemod.collision.OBB;
import com.dawnestofbread.vehiclemod.utils.Curve;
import com.dawnestofbread.vehiclemod.utils.Seat;
import com.dawnestofbread.vehiclemod.utils.Wheel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

import static com.dawnestofbread.vehiclemod.registries.SoundEventRegistry.*;

public class Twinkie extends AbstractMotorcycle {

    public Twinkie(EntityType<? extends Entity> entityType, Level worldIn) {
        super(entityType, worldIn);
        // This probably could be moved to setupSeats, but meh
        SeatManager = new ArrayList<>(2);
        SeatManager.add(0,UUID.fromString("00000000-0000-0000-0000-000000000000"));
        SeatManager.add(1,UUID.fromString("00000000-0000-0000-0000-000000000000"));

        collisionBounds = new OBB(new Vector3f(0,0.78125f,0), new Vector3f(0.375f,0.59375f,1.0625f), new Quaternionf());

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
        this.shiftDownRPM = 2800;
        // In seconds
        this.timeToShift = .25;

        this.steeringAngle = 25;

        // 0-1
        this.exhaustFumeAmount = .1f;
        this.exhaust = new Vec3[2];
        this.exhaust[0] = new Vec3(0.4375, 0.3715625, -1.1594375);
        this.exhaust[1] = new Vec3(-0.4375, 0.3715625, -1.1594375);

        this.engineSounds = new HashMap<>(2);
        this.engineSounds.put(AudioManager.SoundType.ENGINE_IDLE, SCOOTER_IDLE.get());
        this.engineSounds.put(AudioManager.SoundType.ENGINE_MOVING, SCOOTER_MOVING.get());
    }

    // Create the seats and set their offsets
    @Override
    protected void setupSeats() {
        Seats = new Seat[2];

        // This is divided by 16
        Seat seat0 = new Seat();
        seat0.seatOffset = new Vec3(0,0.125,-0.225);
        seat0.animationSet = new TwinkieDriverPoseSet();
        Seats[0] = seat0;

        Seat seat1 = new Seat();
        seat1.seatOffset = new Vec3(0,0.125,-0.8558625);
        seat1.animationSet = new TwinkiePassenger0PoseSet();
        Seats[1] = seat1;
    }

    // Create the wheels and set their parameters
    @Override
    protected void setupWheels() {
        Wheels = new ArrayList<>(2);

        Wheels.add(0, new Wheel());
        Wheels.add(1, new Wheel());

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
