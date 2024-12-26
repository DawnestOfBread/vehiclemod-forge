package com.dawnestofbread.vehiclemod;

import com.dawnestofbread.vehiclemod.client.audio.AudioManager;
import com.dawnestofbread.vehiclemod.client.audio.SimpleEngineSound;
import com.dawnestofbread.vehiclemod.client.effects.SurfaceHelper;
import com.dawnestofbread.vehiclemod.utils.Curve;
import com.dawnestofbread.vehiclemod.utils.HitResult;
import com.dawnestofbread.vehiclemod.utils.VectorUtils;
import com.dawnestofbread.vehiclemod.utils.Wheel;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.dawnestofbread.vehiclemod.client.audio.AudioManager.calculateVolume;
import static com.dawnestofbread.vehiclemod.utils.LineTrace.lineTraceByType;
import static com.dawnestofbread.vehiclemod.utils.MathUtils.*;
import static com.dawnestofbread.vehiclemod.utils.VectorUtils.rotateVectorToEntitySpace;
import static com.dawnestofbread.vehiclemod.utils.VectorUtils.rotateVectorToEntitySpaceYOnly;

public abstract class WheeledVehicle extends AbstractVehicle {
    public static final Logger LOGGER = VehicleMod.LOGGER;
    private static final EntityDataAccessor<Float> VELOCITY = SynchedEntityData.defineId(WheeledVehicle.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> WEIGHT_TRANSFER_X = SynchedEntityData.defineId(WheeledVehicle.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> WEIGHT_TRANSFER_Z = SynchedEntityData.defineId(WheeledVehicle.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> RPM_SYNC = SynchedEntityData.defineId(WheeledVehicle.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TRACTION = SynchedEntityData.defineId(WheeledVehicle.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> BRAKING = SynchedEntityData.defineId(WheeledVehicle.class, EntityDataSerializers.BOOLEAN);
    public final double climbAmount = .5;
    protected final double dragConstant = 0.4257;
    protected final Vec3 gravitationalAcceleration = new Vec3(0, -9, 0);
    public double wheelBase;
    public Vec3 centreOfGeometry, frontAxle, rearAxle;
    protected double steeringAngle;
    protected List<Wheel> Wheels;
    protected double maxBodyPitch, maxBodyRoll;
    protected Vec3 acceleration;
    protected double angularVelocity;
    protected double baseHeight;
    protected boolean braking;
    protected Vec3 brakingForce, dragForce, lateralForce = Vec3.ZERO;
    protected int currentGear;
    protected double differentialRatio;
    protected double brakingConstant;
    protected double corneringStiffness;
    protected double driveWheelAngularVelocity;
    protected int driveWheelReferenceIndex;
    protected double engineForceMultiplier;
    protected double engineTorque, driveTorque;
    protected Vec3[] exhaust;
    protected double exhaustFumeAmount;
    protected double forwardSpeed;
    protected Vec3 forwardVelocity = new Vec3(0, 0, 0);
    protected double[] gearRatios;
    protected double idleRPM, shiftUpRPM, shiftDownRPM, maxRPM, engineForce;
    protected double movementDirection;
    protected double slipRatio;
    protected double steeringDelta, circleRadius;
    protected int targetGear;
    protected double timeToShift;
    protected Curve torqueCurve;
    protected Curve slipAngleCurve;
    protected double traction = 1;
    protected double transmissionEfficiency; // .7 - .9
    protected double weight, idleBrakeAmount = .1;
    private double weightTransferX, weightTransferZ; // 1 = full weight on front | -1 = full weight on rear
    private double shiftTimeLeft;

    protected WheeledVehicle(EntityType<? extends Entity> entityType, Level worldIn) {
        super(entityType, worldIn);
        this.setupWheels();
    }

    public double getTraction() {
        return traction;
    }

    public List<Wheel> getWheels() {
        return Wheels;
    }

    public double getMaxBodyPitch() {
        return maxBodyPitch;
    }

    public double getMaxBodyRoll() {
        return maxBodyRoll;
    }

    public double getWeightTransferX() {
        return weightTransferX;
    }

    public double getWeightTransferZ() {
        return weightTransferZ;
    }

    protected abstract void setupWheels();

    public double getMovementDirection() {
        return movementDirection;
    }

    public double getForwardSpeed() {
        return forwardSpeed;
    }

    @Override
    public float getStepHeight() {
        return .5f;
    }

    public double getSteeringDelta() {
        return steeringDelta;
    }

    public double getSteeringAngle() {
        return steeringAngle;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VELOCITY, 0f);
        this.entityData.define(WEIGHT_TRANSFER_X, 0f);
        this.entityData.define(WEIGHT_TRANSFER_Z, 0f);
        this.entityData.define(RPM_SYNC, 0f);
        this.entityData.define(TRACTION, 0f);
        this.entityData.define(BRAKING, false);
    }

    protected void updatePassengerPosition(Entity passenger) {
        super.updatePassengerPosition(passenger);
        if (!this.level().isClientSide) {
            ServerPlayer castedEntity = (ServerPlayer) passenger;
            // Temporary UI
            //                                                                                                 This should be multiplied by 3.6, but it's faked for gameplay’s sake
            // Display current speed and gear                                                                  Why lie to the player? Because I can!
            castedEntity.connection.send(new ClientboundSetActionBarTextPacket(Component.literal(Math.round(forwardSpeed * 4.3) + "km/h \n" + "Gear: " + (currentGear == 0 ? "R" : currentGear == 1 ? "N" : String.valueOf(currentGear - 1)) + "\nRPM: " + Math.round(RPM) + "\nTorque: " + Math.round(engineTorque) + "\nSpring length: " + Wheels.get(0).springLength * 100 + "cm"))); // Long boi
        }
    }

    /**
     * Helper method for checking which wheels are on the ground
     *
     * @param listIn list of wheels to check
     */
    public List<Wheel> getWheelsOnGround(List<Wheel> listIn) {
        return listIn.stream().filter(wheel -> wheel.onGround).toList();
    }

    /**
     * Helper method for getting wheels with the affectedByEngine flag
     */
    public List<Wheel> getPoweredWheels() {
        return Wheels.stream().filter(wheel -> wheel.affectedByEngine).toList();
    }

    /**
     * Helper method for getting wheels with the affectedBySteering flag
     */
    public List<Wheel> getTurningWheels() {
        return Wheels.stream().filter(wheel -> wheel.affectedBySteering).toList();
    }

    /**
     * Helper method for getting wheels with the affectedByBrake flag
     */
    public List<Wheel> getBrakingWheels() {
        return Wheels.stream().filter(wheel -> wheel.affectedByBrake).toList();
    }

    /**
     * Helper method for getting wheels with the affectedByHandbrake flag
     */
    public List<Wheel> getHandBrakingWheels() {
        return Wheels.stream().filter(wheel -> wheel.affectedByHandbrake).toList();
    }

    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        // Completely made-up and probably subject to change
        this.lerpYRot = yaw + (1 - traction) * ((steering * 2 * steeringAngle) / (this.mass * Mth.square(this.length) / 850));
        this.lerpXRot = pitch;
        this.lerpSteps = posRotationIncrements;
    }

    @SubscribeEvent
    public void tick() {
        accumulatedTime += 0.05d;
        double deltaTime = timeStep;
        super.tick(deltaTime);
        while (accumulatedTime >= timeStep) {
            steering = steeringEasing(steering, steeringInput, (float) deltaTime);
            if (!this.level().isClientSide) {
                simulateVehicleServer(deltaTime);

                this.move(MoverType.SELF, velocity.add(this.onGround() ? Vec3.ZERO : gravitationalAcceleration).scale(deltaTime));

                Vec3 rotationPivot = calculateMidpointStart(Wheels.stream().filter(wheel -> !getTurningWheels().contains(wheel)).toList()).multiply(.5, .5, -.5);
                this.setPos(this.position().subtract(rotateVectorToEntitySpace(rotationPivot, this)));

                double yawVelocity = (float) ((angularVelocity * deltaTime) * -movementDirection);
                if (!Double.isNaN(yawVelocity)) this.turn(yawVelocity, 0);
                this.setPos(this.position().add(rotateVectorToEntitySpace(rotationPivot, this)));

            } else {
                simulateVehicleClient(deltaTime);
                updateVehicleRotation(deltaTime);
            }
            accumulatedTime -= timeStep;
        }
    }

    protected void simulateVehicleServer(double deltaTime) {
        Wheel driveWheel = Wheels.get(driveWheelReferenceIndex);
        engineForce = throttle * engineForceMultiplier;

        forward = new Vec3(0, 0, 1);
        forwardSpeed = forwardVelocity.length();
        this.writeFloatTag(VELOCITY, (float) (forwardSpeed * movementDirection));

        wheelBase = frontAxle.distanceTo(rearAxle);

        // Coming up next! A bit of complicated maths from 'Marco Monster', some random paper on vehicle physics, and my own stupidity!
        dragForce = new Vec3(-dragConstant * forwardVelocity.x * forwardSpeed, 0, -dragConstant * forwardVelocity.z * forwardSpeed);

        braking = (movementDirection > 0 && throttle < 0) || (movementDirection < 0 && throttle > 0) || throttle == 0 || handbrake > 0;
        this.writeBoolTag(BRAKING, braking);

        // Calculate and scale brake force based on how many braking wheels are on the ground
        brakingForce = forward.reverse().scale(throttle != 0 ? brakingConstant : brakingConstant * idleBrakeAmount).scale(handbrake == 1 ? 2f : 1f).scale(movementDirection);
        if (handbrake > 0)
            brakingForce = brakingForce.scale((double) getWheelsOnGround(getHandBrakingWheels()).size() / getHandBrakingWheels().size());
        else
            brakingForce = brakingForce.scale((double) getWheelsOnGround(getBrakingWheels()).size() / getBrakingWheels().size());

        // Calculate and scale drive force based on how many drive wheels are on the ground
        double driveForce = (driveTorque / driveWheel.radius) * (double) getWheelsOnGround(getPoweredWheels()).size() / getPoweredWheels().size();

        if (braking) {
            acceleration = VectorUtils.divideVectorByScalar(brakingForce.add(dragForce), mass);
            if (acceleration.scale(deltaTime).length() > forwardSpeed) {
                // Fixes jitter at very low speeds
                acceleration = Vec3.ZERO;
                forwardVelocity = Vec3.ZERO;
            }
        } else
            acceleration = VectorUtils.divideVectorByScalar(new Vec3(0, 0, 1).scale(driveForce).scale(traction).add(dragForce), mass);

        forwardVelocity = forwardVelocity.add(acceleration.scale(deltaTime));
        forwardSpeed = forwardVelocity.length();
        movementDirection = forward.dot(forwardVelocity.normalize());

        weight = mass * gravity;

        driveWheelAngularVelocity = forwardSpeed / driveWheel.radius;

        RPM = Mth.clamp(Math.abs(driveWheelAngularVelocity * gearRatios[currentGear] * differentialRatio * 15) + idleRPM, idleRPM, maxRPM);
        this.writeFloatTag(RPM_SYNC, (float) RPM);
        engineTorque = Math.abs(throttle) * torqueCurve.lookup(RPM, 1000);

        driveTorque = engineTorque * gearRatios[currentGear] * differentialRatio * transmissionEfficiency;

        // Forward motion ends right about here
        // Onto steering now

        steeringDelta = steeringAngle * steering;
        circleRadius = wheelBase / Math.tan(Math.toRadians(-steeringDelta));
        angularVelocity = forwardSpeed / circleRadius / ((Mth.PI / 180) / 10);
        angularVelocity *= traction;
        angularVelocity *= Math.max(handbrake + .5, 1);

        // Drifting mechanics
        double driftIntensity = Math.min(forwardSpeed / 40, 1.0);
        if (Math.abs(steering) > 0.5 && (forwardSpeed > 13.5 || (forwardSpeed > 10 && handbrake > 0) || (forwardSpeed > 11.5 && braking && handbrake == 0))) {
            traction = Math.max(traction - 0.33 * deltaTime, 0.33); // Traction reduction
        } else {
            driftIntensity *= 1 - traction;
            traction = Math.min(1.0, traction + deltaTime * 0.1); // Gradual traction recovery
        }
        this.writeFloatTag(TRACTION, (float) traction);
        lateralForce = forward.yRot((float) Math.toRadians(steeringDelta + (90 - Math.abs(steeringDelta)) * (1 - traction) * Math.signum(steeringDelta))).normalize().scale(Math.abs(steeringDelta) * driftIntensity * .65);
        Vec3 counterForce = forward.reverse().scale(driftIntensity * .025); // Counteracting force to balance drift
        forwardVelocity = forwardVelocity.add(counterForce);

        velocity = rotateVectorToEntitySpace(forwardVelocity, this).add(rotateVectorToEntitySpace(lateralForce, this));

        weightTransferX = (height / wheelBase) * ((acceleration.length() * (forward.dot(acceleration.normalize()))) / gravity) * 2;
        // This equation is baloney, but the result sure does look nice
        weightTransferZ = (height / wheelBase) * ((this.lateralForce.length() * driftIntensity * (forward.yRot((float) Math.toRadians(90)).dot(this.lateralForce.normalize()))) / gravity) * 4;

        this.writeFloatTag(WEIGHT_TRANSFER_X, (float) weightTransferX);
        this.writeFloatTag(WEIGHT_TRANSFER_Z, (float) weightTransferZ);

        gearShift(deltaTime);
        updateWheelsServer(deltaTime);
    }

    protected void gearShift(double deltaTime) {
        if (shiftTimeLeft > 0) {
            currentGear = 1;
            shiftTimeLeft -= deltaTime;
        }
        if (shiftTimeLeft <= 0) {
            currentGear = targetGear;
            if (throttle > 0 && currentGear == 1) {
                currentGear = 2;
                targetGear = 2;
            } else if (throttle < 0 && currentGear == 1) {
                currentGear = 0;
                targetGear = 0;
            } else if (throttle > 0 && currentGear == 0) {
                currentGear = 1;
                targetGear = 1;
            } else if (RPM > shiftUpRPM && currentGear > 1 && throttle > 0 && targetGear + 1 < gearRatios.length)
                targetGear++;
            else if (RPM < shiftDownRPM && currentGear > 1 && throttle <= 0) targetGear--;
            if (currentGear != targetGear) shiftTimeLeft = timeToShift;
        }
    }

    protected final void updateWheelsServer(double deltaTime) {
        for (int i = 0; i < Wheels.size(); i++) {
            updateWheelServer(i, deltaTime);
        }
    }

    protected void updateWheelServer(int wheelIndex, double deltaTime) {
        if (!(Wheels.size() > 0)) return;
        if (Wheels.get(wheelIndex) == null) return;
        Wheel wheel = Wheels.get(wheelIndex);

        HitResult wheelTrace = checkWheelOnGroundRaycast(wheel);
        wheel.onGround = wheelTrace.hit();
        wheel.springLength = wheelTrace.hit() ? wheelTrace.getDistance() - wheel.springMinLength - wheel.radius : wheel.springMaxLength;
        wheel.currentRelativePosition = rotateVectorToEntitySpace(wheel.startingRelativePosition.scale(.5).add(0, -wheel.springLength, 0), this);
    }

    private HitResult checkWheelOnGroundRaycast(Wheel wheel) {
        Vec3 offsetStart = this.position().add(wheel.currentRelativePosition.scale(0.5).add(new Vec3(-wheel.width / 2, -wheel.radius, -wheel.radius).yRot(-this.getYRot() * ((float) Math.PI / 180F)).xRot(-this.getXRot() * ((float) Math.PI / 180F))));
        Vec3 offsetEnd = this.position().add(wheel.currentRelativePosition.scale(0.5).add(new Vec3(wheel.width / 2, wheel.radius, wheel.radius).yRot(-this.getYRot() * ((float) Math.PI / 180F)).xRot(-this.getXRot() * ((float) Math.PI / 180F))));

        Vec3 lineTraceStart = this.position().add(rotateVectorToEntitySpace(wheel.startingRelativePosition.scale(.5).add(0, wheel.springMinLength, 0), this));
        Vec3 lineTraceEnd = this.position().add(rotateVectorToEntitySpace(wheel.startingRelativePosition.scale(.5).add(0, -wheel.springMaxLength, 0), this));

        AABB aabb = new AABB(offsetStart.x, offsetStart.y, offsetStart.z, offsetEnd.x, offsetEnd.y, offsetEnd.z);

        return lineTraceByType(lineTraceStart, lineTraceEnd, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this);
    }

    protected void simulateVehicleClient(double deltaTime) {
        wheelBase = frontAxle.distanceTo(rearAxle);
        forward = getForward();
        angularVelocity = Math.abs(this.getYRot() - this.yRotO);
        forwardSpeed = this.readFloatTag(VELOCITY);
        movementDirection = (forwardSpeed > 0 ? 1f : forwardSpeed < 0 ? -1f : 0f);

        // TODO Calculate these on client-side
        weightTransferX = this.readFloatTag(WEIGHT_TRANSFER_X);
        weightTransferZ = this.readFloatTag(WEIGHT_TRANSFER_Z);

        RPM = dInterpTo(RPM, this.readFloatTag(RPM_SYNC), 3500f, deltaTime);
        traction = this.readFloatTag(TRACTION);
        braking = this.readBoolTag(BRAKING);
        updateWheelsClient(deltaTime);

        Map<AudioManager.SoundType, SimpleEngineSound> soundMap = SOUND_MANAGER.computeIfAbsent(this, v -> new EnumMap<>(AudioManager.SoundType.class));
        SimpleEngineSound idleSound = soundMap.get(AudioManager.SoundType.ENGINE_IDLE);
        SimpleEngineSound movingSound = soundMap.get(AudioManager.SoundType.ENGINE_MOVING);
        if (idleSound != null)
            idleSound.setVolume(calculateVolume(RPM, 0, idleRPM + 1000)).setPitch(mapDoubleRangeClamped(RPM, idleRPM, idleRPM + 1500, 1, 1.4));
        if (movingSound != null)
            movingSound.setVolume(mapDoubleRangeClamped(RPM, idleRPM, idleRPM + 1000, 0, 1)).setPitch(mapDoubleRangeClamped(RPM, idleRPM, maxRPM, .9, 1.4));

        if (this.isEngineOn()) {
            for (Vec3 pos : exhaust) {
                for (int i = 0; i < Math.ceil(RPM / 250); i++) {
                    Vec3 p = position().add(pos.xRot(this.getXRot()).yRot((float) Math.toRadians(-this.getYRot())).scale(.5));
                    Vec3 vel = forward.scale(-movementDirection / 8).add(0, -.05, 0);

                    double probability = exhaustFumeAmount;
                    double result = Math.random();

                    if (result < probability)
                        Objects.requireNonNull(Minecraft.getInstance().level).addParticle(ParticleTypes.SMOKE, p.x, p.y, p.z, vel.x, vel.y, vel.z);
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected final void updateWheelsClient(double deltaTime) {
        for (int i = 0; i < Wheels.size(); i++) {
            updateWheelClient(i, deltaTime);
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected void updateWheelClient(int wheelIndex, double deltaTime) {
        if (!(Wheels.size() > 0)) return;
        if (Wheels.get(wheelIndex) == null) return;
        Wheel wheel = Wheels.get(wheelIndex);

        HitResult wheelTrace = checkWheelOnGroundRaycast(wheel);
        wheel.onGround = wheelTrace.hit();
        wheel.springLength = wheelTrace.hit() ? wheelTrace.getDistance() - wheel.springMinLength - wheel.radius : wheel.springMaxLength;
        wheel.currentRelativePosition = wheel.startingRelativePosition.scale(.5).add(0, -wheel.springLength, 0).yRot(-this.getYRot() * ((float) Math.PI / 180F));

        if (wheel.affectedByEngine) {
            wheel.angularVelocity = forwardSpeed / wheel.radius;
        } else if (wheel.onGround) {
            wheel.angularVelocity = forwardSpeed / wheel.radius;
            if (wheel.affectedBySteering) angularVelocity /= steering * steeringAngle;
        } else wheel.angularVelocity *= 0.99;
        if (wheel.affectedByHandbrake & handbrake > 0f)
            wheel.angularVelocity = 0;

        double probability = Math.abs(wheel.angularVelocity) / 20;
        double result = Math.random();

        HitResult climbTrace = checkWheelShouldClimbRaycast(wheel);
        if (climbTrace.hit() && !climbTrace.isInside())
            wheel.targetWorldPosition = climbTrace.getHitLocation().add(0, wheel.radius, 0).add(rotateVectorToEntitySpaceYOnly(new Vec3(0, 0, Math.sqrt(Math.pow(climbTrace.getStart().x - climbTrace.getHitLocation().x, 2) + Math.pow(climbTrace.getStart().z - climbTrace.getHitLocation().z, 2)) * -movementDirection), this));
        else wheel.targetWorldPosition = wheel.currentRelativePosition.add(this.position());

        if (wheel.onGround) {
            if (result < probability && wheel.affectedByEngine)
                SurfaceHelper.spawnFrictionEffect(SurfaceHelper.getSurfaceFromPosition(this.getBlockPosBelowThatAffectsMyMovement()), this.position().add(wheel.currentRelativePosition.x, wheel.currentRelativePosition.y / 2, wheel.currentRelativePosition.z), forward.scale(-movementDirection).add(0, .01, 0));
            if ((((wheel.affectedByBrake && braking) || (wheel.affectedByHandbrake & handbrake > 0f) || (!wheel.affectedBySteering && angularVelocity > 0.5 && forwardSpeed > 7)) && Math.abs(forwardSpeed) > 1))
                SurfaceHelper.spawnSkidEffect(SurfaceHelper.getSurfaceFromPosition(this.getBlockPosBelowThatAffectsMyMovement()), this.position().add(wheel.currentRelativePosition.x, wheel.currentRelativePosition.y / 2, wheel.currentRelativePosition.z), forward.scale(-movementDirection / 16).add(0, .01, 0));
        }
    }

    private HitResult checkWheelShouldClimbRaycast(Wheel wheel) {
        Vec3 lineTraceStart = this.position().add(rotateVectorToEntitySpaceYOnly(wheel.startingRelativePosition.scale(.5).add(0, climbAmount * 1.01, 0), this));
        Vec3 lineTraceEnd = lineTraceStart.add(0, -wheelBase - wheel.radius, 0).add(rotateVectorToEntitySpaceYOnly(new Vec3(0, 0, wheel.radius * 1.5 * movementDirection), this));

        return lineTraceByType(lineTraceStart, lineTraceEnd, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this);
    }

    private void updateVehicleRotation(double deltaTime) {
        int wheelCount = Wheels.size();
        List<Wheel> frontWheels = Wheels.subList(0, wheelCount / 2); // Front wheels (first half)
        List<Wheel> rearWheels = Wheels.subList(wheelCount / 2, wheelCount); // Rear wheels (second half)

        Vec3 frontMidpoint = calculateMidpointWorld(frontWheels);
        Vec3 rearMidpoint = calculateMidpointWorld(rearWheels);

        Vec3 frontLeftMostWheel = getLeftMostWheelPosition(frontWheels);
        Vec3 frontRightMostWheel = getRightMostWheelPosition(rearWheels);

        Vec3 rearLeftMostWheel = getLeftMostWheelPosition(frontWheels);
        Vec3 rearRightMostWheel = getRightMostWheelPosition(rearWheels);

        double horizontalDistance = Math.sqrt(Math.pow(frontMidpoint.x - rearMidpoint.x, 2) + Math.pow(frontMidpoint.z - rearMidpoint.z, 2));
        double inclineAngle = Math.atan2(frontMidpoint.y - rearMidpoint.y, horizontalDistance);

        // Create alignment plane
        Vec3 v1 = frontRightMostWheel.subtract(rearLeftMostWheel);
        Vec3 v2 = frontLeftMostWheel.subtract(rearRightMostWheel);
        Vec3 normal = v1.cross(v2).normalize();

        double pitch = Math.asin(normal.y());
        double roll = Math.atan2(normal.x(), normal.z());

        double minY = Math.min(Math.min(frontLeftMostWheel.y, frontRightMostWheel.y), Math.min(rearLeftMostWheel.y, rearRightMostWheel.y));
        double vehicleY = minY + baseHeight;

        setXRot((float) Math.toDegrees(pitch));
    }

    public Vec3 calculateMidpointWorld(List<Wheel> wheels) {
        Vec3 midpoint = Vec3.ZERO;
        for (Wheel wheel : wheels) {
            midpoint = midpoint.add(wheel.targetWorldPosition);
        }
        return midpoint.scale(1.0 / wheels.size());
    }

    public Vec3 calculateMidpointStart(List<Wheel> wheels) {
        Vec3 midpoint = Vec3.ZERO;
        for (Wheel wheel : wheels) {
            midpoint = midpoint.add(wheel.startingRelativePosition);
        }
        return midpoint.scale(1.0 / wheels.size());
    }

    private Vec3 getLeftMostWheelPosition(List<Wheel> wheels) {
        Wheel leftmostWheel = wheels.get(0);
        for (Wheel wheel : wheels) {
            if (wheel.startingRelativePosition.x() < leftmostWheel.startingRelativePosition.x()) {
                leftmostWheel = wheel;
            }
        }
        return leftmostWheel.targetWorldPosition;
    }

    private Vec3 getRightMostWheelPosition(List<Wheel> wheels) {
        Wheel rightmostWheel = wheels.get(0);
        for (Wheel wheel : wheels) {
            if (wheel.startingRelativePosition.x() > rightmostWheel.startingRelativePosition.x()) {
                rightmostWheel = wheel;
            }
        }
        return rightmostWheel.targetWorldPosition;
    }

    @Override
    public boolean onGround() {
        // Requires only one wheel to be on the ground to consider the vehicle grounded
        return getWheelsOnGround(Wheels).size() > 0;
    }
}
