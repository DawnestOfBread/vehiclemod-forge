package com.dawnestofbread.vehiclemod.vehicles.entities;

import com.dawnestofbread.vehiclemod.WheeledVehicle;
import com.dawnestofbread.vehiclemod.client.audio.AudioManager;
import com.dawnestofbread.vehiclemod.collision.OBB;
import com.dawnestofbread.vehiclemod.utils.Curve;
import com.dawnestofbread.vehiclemod.utils.Seat;
import com.dawnestofbread.vehiclemod.utils.Wheel;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

import static com.dawnestofbread.vehiclemod.registries.SoundEventRegistry.*;

public class Annihilator extends WheeledVehicle {

    public Annihilator(EntityType<? extends Entity> entityType, Level worldIn) {
        super(entityType, worldIn);
        // This probably could be moved to setupSeats, but meh
        SeatManager = new ArrayList<>(4);
        SeatManager.add(0,UUID.fromString("00000000-0000-0000-0000-000000000000"));
        SeatManager.add(1,UUID.fromString("00000000-0000-0000-0000-000000000000"));
        SeatManager.add(2,UUID.fromString("00000000-0000-0000-0000-000000000000"));
        SeatManager.add(3,UUID.fromString("00000000-0000-0000-0000-000000000000"));

        collisionBounds = new OBB(new Vector3f(0,0.9375f,0.03125f), new Vector3f(1,0.46875f,2.59375f), new Quaternionf());

        // Some values required for maths; when getting them from Blockbench, divide the value by 8!
        this.rearAxle = new Vec3(0, 0.838125, -3.004375);
        this.frontAxle = new Vec3(0, 0.838125, 3.745625);
        this.centreOfGeometry = new Vec3(0,0,0);
        // This is also really important
        this.width = 2;
        this.height = 2.1875;
        this.length = 5.0625;

        // Purely visual, should be set up to not clip into the tyres
        this.maxBodyPitch = 10;
        this.maxBodyRoll = 10;

        // Really important values here, you should try to get them right
        this.idleRPM = 600;
        this.maxRPM = 5500;
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
        tempTCurve.add(5, 300d);
        tempTCurve.add(6, 200d);
        this.transmissionEfficiency = .7;
        this.torqueCurve = new Curve(tempTCurve);

        // Gear setup
        this.differentialRatio = 3.42;
        this.gearRatios = new double[7];
        this.gearRatios[0] = -1.5; // Reverse
        this.gearRatios[1] = 1; // Neutral
        this.gearRatios[2] = 2.66; // 1st
        this.gearRatios[3] = 1.78; // 2nd and so on...
        this.gearRatios[4] = 1.30;
        this.gearRatios[5] = 1.0;
        this.gearRatios[6] = 0.74;

        // Used for the automatic gearbox
        this.shiftUpRPM = 4000;
        this.shiftDownRPM = 2900;
        // In seconds
        this.timeToShift = .5;

        this.steeringAngle = 32;

        // 0-1
        this.exhaustFumeAmount = .8f;
        this.exhaust = new Vec3[1];
        this.exhaust[0] = new Vec3(-1.114375, 0.920625, -5.2675);

        this.engineSounds = new HashMap<>(2);
        this.engineSounds.put(AudioManager.SoundType.ENGINE_IDLE, PICKUP01_IDLE.get());
        this.engineSounds.put(AudioManager.SoundType.ENGINE_LO, PICKUP01_LO.get());
        this.engineSounds.put(AudioManager.SoundType.ENGINE_HI, PICKUP01_HI.get());
    }

    // Create the seats and set their offsets
    @Override
    protected void setupSeats() {
        Seats = new Seat[4];

        // This is divided by 16
        Seat seat0 = new Seat();
        seat0.seatOffset = new Vec3(0.40625,0.090625,1.88125 - 1.5625);
        Seats[0] = seat0;

        Seat seat1 = new Seat();
        seat1.seatOffset = new Vec3(-0.40625,0.090625,1.88125 - 1.5625);
        Seats[1] = seat1;

        Seat seat2 = new Seat();
        seat2.seatOffset = new Vec3(0.6, 0.6, .7984375 - 1.5625);
        seat2.yawOffset = 90;
        Seats[2] = seat2;

        Seat seat3 = new Seat();
        seat3.seatOffset = new Vec3(-0.6, 0.6, .7984375 - 1.8125);
        seat3.yawOffset = -90;
        Seats[3] = seat3;
    }

    // Create the wheels and set their parameters
    @Override
    protected void setupWheels() {
        Wheels = new ArrayList<>(4);

        for (int i1 = 0; i1 < 4; i1++) {
            Wheels.add(i1, new Wheel());
        }

        // Divide this by 16, because it works better and results in more realistic proportions
        for (int i = 0; i < 4; i++) {
            Wheels.get(i).radius = 0.390625;
        }

        for (int j = 0; j < 4; j++) {
            Wheels.get(j).width = 0.3125;
        }

        for (int i = 0; i < 4; i++) {
            Wheels.get(i).springMinLength = 0.56875;
        }
        for (int i = 0; i < 4; i++) {
            Wheels.get(i).springMaxLength = 0.63125;
        }

        // And this
        Wheels.get(0).startingRelativePosition = new Vec3(1.9125, 0.803125, 3.747775);
        Wheels.get(1).startingRelativePosition = new Vec3(-1.9125, 0.803125, 3.747775);
        Wheels.get(2).startingRelativePosition = new Vec3(1.9125, 0.803125, -3.002225);
        Wheels.get(3).startingRelativePosition = new Vec3(-1.9125, 0.803125, -3.002225);

        Wheels.get(0).affectedBySteering = true;
        Wheels.get(1).affectedBySteering = true;

        for (int i = 0; i < 4; i++) {
            Wheels.get(i).mass = 50;
        }

        Wheels.get(2).affectedByEngine = true;
        Wheels.get(3).affectedByEngine = true;

        Wheels.get(2).affectedByBrake = true;
        Wheels.get(3).affectedByBrake = true;

        Wheels.get(2).affectedByHandbrake = true;
        Wheels.get(3).affectedByHandbrake = true;
    }
}
