package com.dawnestofbread.vehiclemod;

import com.dawnestofbread.vehiclemod.client.audio.AudioManager;
import com.dawnestofbread.vehiclemod.client.audio.SimpleEngineSound;
import com.dawnestofbread.vehiclemod.collision.OBB;
import com.dawnestofbread.vehiclemod.geo.Bone;
import com.dawnestofbread.vehiclemod.geo.Transform;
import com.dawnestofbread.vehiclemod.network.*;
import com.dawnestofbread.vehiclemod.utils.MathUtils;
import com.dawnestofbread.vehiclemod.utils.Seat;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.joml.Intersectionf;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;

import java.util.*;

import static com.dawnestofbread.vehiclemod.client.audio.AudioManager.playEngineSound;
import static com.dawnestofbread.vehiclemod.utils.VectorUtils.rotateVectorToEntitySpace;

public abstract class AbstractVehicle extends Entity {
    public static final Logger LOGGER = VehicleMod.LOGGER;
    protected static final EntityDataAccessor<Float> THROTTLE = SynchedEntityData.defineId(AbstractVehicle.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> STEERING = SynchedEntityData.defineId(AbstractVehicle.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<CompoundTag> SEAT_MANAGER = SynchedEntityData.defineId(AbstractVehicle.class, EntityDataSerializers.COMPOUND_TAG);
    protected final WeakHashMap<AbstractVehicle, EnumMap<AudioManager.SoundType, SimpleEngineSound>> SOUND_MANAGER = new WeakHashMap<>();
    protected final double gravity = 9.81;
    protected final double timeStep = 1d / 120d;
    private final HashMap<Bone, Transform> boneTransforms = new HashMap<>();
    public List<UUID> SeatManager;
    public double width;
    public double length;
    public double height;
    public Map<AudioManager.SoundType, SoundEvent> engineSounds;
    public float throttle = 0;
    public float handbrake = 0;
    public float sprint = 0;
    protected Seat[] Seats;
    protected Transform passengerTransform = new Transform();
    protected float steeringInput = 0;
    protected float steering = 0;
    protected float RPM = 0;
    // TODO Implement collision system
    protected Vec3 forward;
    protected Vec3 velocity = Vec3.ZERO;
    protected int lerpSteps;
    protected double lerpX;
    protected double lerpXRot;
    protected double lerpY;
    protected double lerpYRot;
    protected double lerpZ;
    protected double mass = 1000;
    protected double accumulatedTime;
    protected Vec3 translationOffset = Vec3.ZERO;
    protected OBB collisionBounds;
    boolean inputForward = false;
    boolean inputBackward = false;
    boolean inputRight = false;
    boolean inputLeft = false;
    boolean inputJump = false;
    boolean inputSprint = false;
    private float zRot;
    private OBB actualCollision;

    protected AbstractVehicle(EntityType<? extends Entity> entityType, Level worldIn) {
        super(entityType, worldIn);
        this.noPhysics = false;
        this.ejectPassengers();
        this.setupSeats();
    }

    public OBB getCollisionBounds() {
        return collisionBounds;
    }

    public OBB getActualCollision() {
        return actualCollision;
    }
    public OBB getCollisionRelative() {
        return new OBB(new Vector3f(this.actualCollision.getCentre()).sub(this.position().toVector3f()), this.collisionBounds.getHalfSize(), this.actualCollision.getOrientation());
    }

    public Transform passengerTransform() {
        return passengerTransform;
    }

    public Seat[] getSeats() {
        return Seats;
    }


    public float getSteeringInput() {
        return steeringInput;
    }

    public float getSteering() {
        return steering;
    }

    public void setSteering(float input) {
        this.steeringInput = input;
    }

    public HashMap<Bone, Transform> getBoneTransforms() {
        return boneTransforms;
    }

    public boolean isEngineOn() {
        return !SeatManager.get(0).equals(UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }

    protected abstract void setupSeats();

    public Vec3 getTranslationOffset() {
        return translationOffset;
    }

    protected final void writeFloatTag(EntityDataAccessor<Float> dataAccessor, float in) {
        this.entityData.set(dataAccessor, in);
    }

    protected final void writeBoolTag(EntityDataAccessor<Boolean> dataAccessor, boolean in) {
        this.entityData.set(dataAccessor, in);
    }

    protected float readFloatTag(EntityDataAccessor<Float> dataAccessor) {
        return this.entityData.get(dataAccessor);
    }

    protected boolean readBoolTag(EntityDataAccessor<Boolean> dataAccessor) {
        return this.entityData.get(dataAccessor);
    }

    public float getZRot() {
        return zRot;
    }

    public void setZRot(float zRot) {
        this.zRot = zRot;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SEAT_MANAGER, new CompoundTag());
        this.entityData.define(THROTTLE, 0F);
        this.entityData.define(STEERING, 0F);
    }

    @Override
    protected final void playStepSound(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
    }

    // Come on you can't just push a vehicle, you're not *that* strong
    @Override
    public void push(double x, double y, double z) {
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("SeatManager", Tag.TAG_COMPOUND)) readSeatManager(compound.getCompound("SeatManager"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.put("SeatManager", writeSeatManager());
    }

    protected CompoundTag writeSeatManager() {
        CompoundTag SeatManagerTag = new CompoundTag();
        ListTag SeatManagerList = new ListTag();
        for (int i = 0; i < SeatManager.size(); i++) {
            CompoundTag seatTag = new CompoundTag();
            seatTag.putUUID("UUID", SeatManager.get(i));
            seatTag.putInt("Index", i);
            SeatManagerList.add(seatTag);
        }
        SeatManagerTag.put("seatList", SeatManagerList);
        return SeatManagerTag;
    }

    @Override
    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
        if (!player.level().isClientSide && !player.isCrouching()) {
            // Unused for now
            ItemStack heldItem = player.getItemInHand(hand);

            if (this.canRide(player)) {
                if (SeatManager.contains(player.getUUID())) return InteractionResult.SUCCESS;
                int closestSeatIndex = -1;
                double closestDistance = 0;
                for (int i = 0; i < Seats.length; i++) {
                    Seat seat = Seats[i];
                    if (!SeatManager.get(i).equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) continue;

                    Vec3 seatVec = seat.seatOffset.yRot(-this.getYRot() * ((float) Math.PI / 180F)).add(this.position());
                    double distance = player.distanceToSqr(seatVec.x, seatVec.y - player.getBbHeight() / 2F, seatVec.z);
                    if (closestSeatIndex == -1 || distance < closestDistance) {
                        closestSeatIndex = i;
                        closestDistance = distance;
                    }
                }
                if (closestSeatIndex != -1) {
                    SeatManager.set(closestSeatIndex, player.getUUID());
                    this.entityData.set(SEAT_MANAGER, writeSeatManager(), true);
                    player.startRiding(this);
                }
                return InteractionResult.SUCCESS;
            }
        }
        //LOGGER.info(SeatManager.toString() + " - " + (player.level().isClientSide ? "Client" : "Server"));
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    protected void positionRider(@NotNull Entity rider_, @NotNull MoveFunction moveFunc) {
        super.positionRider(rider_, moveFunc);
        updatePassengerPosition(rider_);
    }

    @Override
    protected boolean canRide(@NotNull Entity entity) {
        return SeatManager.contains(UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }

    @Override
    public void addPassenger(@NotNull Entity passenger) {
        super.addPassenger(passenger);
        if (this.isControlledByLocalInstance() && this.lerpSteps > 0) {
            this.lerpSteps = 0;
            this.setPos(this.lerpX, this.lerpY, this.lerpZ);
            this.setYRot((float) this.lerpYRot);
            this.setXRot((float) this.lerpXRot);
        }
    }

    @Override
    protected void removePassenger(@NotNull Entity entity) {
        super.removePassenger(entity);
        if (!entity.level().isClientSide && SeatManager.contains(entity.getUUID())) {
            if (SeatManager.indexOf(entity.getUUID()) == 0) throttle = 0;
            SeatManager.set(SeatManager.indexOf(entity.getUUID()), UUID.fromString("00000000-0000-0000-0000-000000000000"));
            this.entityData.set(SEAT_MANAGER, writeSeatManager());
        }
        LOGGER.info(SeatManager.toString() + " - " + (this.level().isClientSide ? "Client" : "Server"));
    }

    @Override
    protected boolean canAddPassenger(@NotNull Entity passenger) {
        return this.getPassengers().size() < Seats.length;
    }

    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYRot = yaw;
        this.lerpXRot = pitch;
        this.lerpSteps = posRotationIncrements;
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> dataAccessor) {
        super.onSyncedDataUpdated(dataAccessor);
        if (dataAccessor.equals(SEAT_MANAGER)) readSeatManager(this.entityData.get(SEAT_MANAGER));
    }

    protected void updatePassengerPosition(Entity passenger) {
        if (passenger.level().isClientSide() && this.hasPassenger(passenger)) {
            if (!SeatManager.contains(passenger.getUUID())) return;
            Seat seat = Seats[SeatManager.indexOf(passenger.getUUID())];

            if (seat == null) return;
            passenger.setYBodyRot(this.getYRot() + seat.yawOffset);
            Vec3 position = new Vec3(this.position().x, this.position().y, this.position().z).add(rotateVectorToEntitySpace(seat.seatOffset, this));
            //position = position.yRot((this.getYRot()));
            passenger.setPos(position);
        }
    }

    protected void readSeatManager(CompoundTag tag) {
        if (!tag.contains("seatList")) return;
        ListTag SeatManagerList = tag.getList("seatList", Tag.TAG_COMPOUND);
        for (Tag value : SeatManagerList) {
            CompoundTag seatTag = (CompoundTag) value;
            SeatManager.set(seatTag.getInt("Index"), seatTag.getUUID("UUID"));
        }
    }

    @SubscribeEvent
    public void tick(double deltaTime) {
//        if (!this.level().isClientSide) {
            this.actualCollision = new OBB(
                    this.position().toVector3f().add(this.collisionBounds.getCentre()),
                    this.collisionBounds.getHalfSize(),
                    new Quaternionf().rotationXYZ((float) Math.toRadians(-this.getXRot()), (float) Math.toRadians(-this.getYRot()), (float) Math.toRadians(-this.getZRot()))
            );
//        }

        super.tick();
        this.lerpTick();

        if (this.level().isClientSide) {
            if (this.SeatManager.get(0).equals(Objects.requireNonNull(Minecraft.getInstance().player).getUUID())) {
                inputForward = Minecraft.getInstance().options.keyUp.isDown();
                inputBackward = Minecraft.getInstance().options.keyDown.isDown();
                inputRight = Minecraft.getInstance().options.keyRight.isDown();
                inputLeft = Minecraft.getInstance().options.keyLeft.isDown();

                inputJump = Minecraft.getInstance().options.keyJump.isDown();
                inputSprint = Minecraft.getInstance().options.keySprint.isDown();

                if ((inputForward && inputBackward ? 2f : inputForward ? 1f : inputBackward ? -1f : 0f) != throttle)
                    PacketHandler.INSTANCE.sendToServer(new MessageThrottle(inputForward && inputBackward ? 2f : inputForward ? 1f : inputBackward ? -1f : 0f));
                if ((inputLeft ? -1f : inputRight ? 1f : 0f) != steeringInput)
                    PacketHandler.INSTANCE.sendToServer(new MessageSteering(inputLeft ? -1f : inputRight ? 1f : 0f));
                if ((inputJump ? 1f : 0f) != handbrake)
                    PacketHandler.INSTANCE.sendToServer(new MessageHandbrake(inputJump ? 1f : 0f));
                if ((inputSprint ? 1f : 0f) != sprint)
                    PacketHandler.INSTANCE.sendToServer(new MessageSprint(inputSprint ? 1f : 0f));
                this.setThrottle(inputForward && inputBackward ? 2f : inputForward ? 1f : inputBackward ? -1f : 0f);
                this.setSteering(inputLeft ? -1f : inputRight ? 1f : 0f);
                this.setHandbrake(inputJump ? 1f : 0f);
                this.setSprint(inputSprint ? 1f : 0f);

//                UpdateCamera(deltaTime);

                this.readSeatManager(this.entityData.get(SEAT_MANAGER));
                if (this.isEngineOn()) {
                    if (engineSounds.containsKey(AudioManager.SoundType.ENGINE_IDLE))
                        playEngineSound(SOUND_MANAGER, this, AudioManager.SoundType.ENGINE_IDLE, this.engineSounds.get(AudioManager.SoundType.ENGINE_IDLE)).setVolume(0f);
                    if (engineSounds.containsKey(AudioManager.SoundType.ENGINE_MOVING))
                        playEngineSound(SOUND_MANAGER, this, AudioManager.SoundType.ENGINE_MOVING, this.engineSounds.get(AudioManager.SoundType.ENGINE_MOVING)).setVolume(0f);
                    if (engineSounds.containsKey(AudioManager.SoundType.ENGINE_LO))
                        playEngineSound(SOUND_MANAGER, this, AudioManager.SoundType.ENGINE_LO, this.engineSounds.get(AudioManager.SoundType.ENGINE_LO)).setVolume(0f);
                    if (engineSounds.containsKey(AudioManager.SoundType.ENGINE_HI))
                        playEngineSound(SOUND_MANAGER, this, AudioManager.SoundType.ENGINE_HI, this.engineSounds.get(AudioManager.SoundType.ENGINE_HI)).setVolume(0f);
                }
            }
        }
    }

    @Override
    public boolean isColliding(BlockPos blockPos, BlockState blockState) {
        // Get the block's VoxelShape
        VoxelShape blockShape = blockState.getCollisionShape(this.level(), blockPos);

        // Iterate over the block's AABBs
        for (AABB box : blockShape.toAabbs()) {
            // Translate the block's AABB to world position
            AABB worldBox = box.move(blockPos.getX(), blockPos.getY(), blockPos.getZ());

            // Compute the center of the worldBox
            Vec3 blockCenter = new Vec3(
                    (worldBox.minX + worldBox.maxX) / 2.0,
                    (worldBox.minY + worldBox.maxY) / 2.0,
                    (worldBox.minZ + worldBox.maxZ) / 2.0
            );

            // Create an OBB for the block, assuming axis-aligned (identity rotation)
            OBB blockOBB = new OBB(
                    blockCenter.toVector3f(),
                    new Vector3f(
                            (float) (box.getXsize() / 2.0),
                            (float) (box.getYsize() / 2.0),
                            (float) (box.getZsize() / 2.0)
                    ),
                    new Quaternionf() // Blocks are axis-aligned, no rotation
            );

            // Check collision between entity's OBB and block's OBB
            if (actualCollision.intersects(blockOBB)) return true;
        }

        // No collision detected
        return false;
    }


    public void setThrottle(float power) {
        this.throttle = power;
    }

    public void setSprint(float input) {
        this.sprint = input;
    }

    public void setHandbrake(float input) {
        this.handbrake = input;
    }

    private void lerpTick() {
        if (this.lerpSteps > 0) {
            double d0 = this.getX() + (this.lerpX - this.getX()) / (double) this.lerpSteps;
            double d2 = this.getY() + (this.lerpY - this.getY()) / (double) this.lerpSteps;
            double d4 = this.getZ() + (this.lerpZ - this.getZ()) / (double) this.lerpSteps;
            double d6 = Mth.wrapDegrees(this.lerpYRot - (double) this.getYRot());
            double d8 = Mth.wrapDegrees(this.lerpXRot - (double) this.getXRot());
            float yRot = this.getYRot() + (float) d6 / (float) this.lerpSteps;
            float xRot = (float) this.lerpXRot;
            --this.lerpSteps;
            this.setPos(d0, d2, d4);
            if (!Float.isNaN(xRot)) this.setXRot(xRot);
            if (!Float.isNaN(yRot)) this.setYRot(yRot);
        }
    }

//    @Override
//    protected @NotNull AABB makeBoundingBox() {
//        if (collision == null) return new AABB(-.1,-.1,-.1,.1,.1,.1).move(this.position());
//        Vec3 start = rotateVectorToEntitySpace(new Vec3(collision.minX, collision.minY, collision.minZ), this);
//        Vec3 end = rotateVectorToEntitySpace(new Vec3(collision.maxX, collision.maxY, collision.maxZ), this);
//        AABB positionedAABB = new AABB(start.add(this.position()), end.add(this.position()));
//        return positionedAABB;
//    }

    protected void updateCamera(double deltaTime) {
        Entity camera = Minecraft.getInstance().cameraEntity;

        float xPos = MathUtils.fInterpToExp((float) Objects.requireNonNull(camera).getX(), (float) this.getX(), 3f, (float) deltaTime);
        float yPos = MathUtils.fInterpToExp((float) camera.getY(), (float) this.getY(), 3f, (float) deltaTime);
        float zPos = MathUtils.fInterpToExp((float) camera.getZ(), (float) this.getZ(), 3f, (float) deltaTime);

        camera.setPos(xPos, yPos, zPos);
        camera.setYRot(MathUtils.fInterpToExp(camera.getYRot(), this.getYRot(), 3f, (float) deltaTime));
    }

    @Override
    public void move(MoverType type, Vec3 movement) {
        if (!this.level().isClientSide) {
            // Start with the desired movement vector
            Vec3 desiredMovement = movement;

            // Calculate the new position based on movement
            Vec3 newPosition = this.position().add(desiredMovement);

            // Update the OBB with the new position
            OBB futureOBB = new OBB(
                    newPosition.toVector3f().add(this.collisionBounds.getCentre()),
                    this.collisionBounds.getHalfSize(),
                    new Quaternionf().rotationXYZ((float) Math.toRadians(-this.getXRot()), (float) Math.toRadians(-this.getYRot()), (float) Math.toRadians(-this.getZRot()))
            );

            // Check for collisions with blocks and entities
            List<OBB> collidingOBBs = getCollidingOBBs(desiredMovement, futureOBB);

            if (!collidingOBBs.isEmpty()) {
                onHit(collidingOBBs, desiredMovement);
                desiredMovement = Vec3.ZERO;
                this.velocity = Vec3.ZERO;
            }

            // Apply the resolved movement to the entity
            this.setPos(this.position().add(desiredMovement));

            // Update the OBB to reflect the new position
            this.actualCollision = new OBB(
                    this.position().toVector3f().add(this.collisionBounds.getCentre()),
                    this.collisionBounds.getHalfSize(),
                    new Quaternionf().rotationXYZ((float) Math.toRadians(-this.getXRot()), (float) Math.toRadians(-this.getYRot()), (float) Math.toRadians(-this.getZRot()))
            );
        }
    }

    protected abstract void onHit(List<OBB> collidingOBBs, Vec3 desiredMovement);

    private List<OBB> getCollidingOBBs(Vec3 movement, OBB futureOBB) {
        List<OBB> collidingOBBs = new ArrayList<>();

//        for (Entity entity : this.level().getEntities(this, futureOBB.toAABB())) {
//            if (entity instanceof AbstractVehicle other && other.getActualCollision() != null) {
//                if (futureOBB.intersects(other.getActualCollision())) {
//                    collidingOBBs.add(other.getActualCollision());
//                    other.move(MoverType.PLAYER, movement);
//                }
//            }
//        }

        BlockPos startPoint = this.blockPosition().offset((int) (-4 * collisionBounds.getHalfSize().x), (int) (-4 * collisionBounds.getHalfSize().y), (int) (-4 * collisionBounds.getHalfSize().z));
        BlockPos endPoint = this.blockPosition().offset((int) (4 * collisionBounds.getHalfSize().x), (int) (4 * collisionBounds.getHalfSize().y), (int) (4 * collisionBounds.getHalfSize().z));

        for (BlockPos pos : BlockPos.betweenClosed(startPoint, endPoint)) {
            BlockState state = level().getBlockState(pos);
            // You can check for specific block types or handle all blocks
            if (state.getCollisionShape(level(), pos).isEmpty()) {
                continue;  // No collision if the block shape is empty
            }

            for (AABB aabb : state.getCollisionShape(level(), pos).toAabbs()) {
                OBB blockOBB = OBB.fromAABB(aabb.move(pos));
                if (futureOBB.intersects(blockOBB)) {
                    collidingOBBs.add(blockOBB);
                }
            }
        }

        return collidingOBBs;
    }
}
