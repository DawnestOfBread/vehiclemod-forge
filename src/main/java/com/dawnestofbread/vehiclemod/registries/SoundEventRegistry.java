package com.dawnestofbread.vehiclemod.registries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundEventRegistry {
    public static final String MODID = "vehiclemod";
    public static void RegisterAllSoundEvents() {
        SOUND_EVENTS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);
    public static final RegistryObject<SoundEvent> SCOOTER_IDLE = SOUND_EVENTS.register("scooter.idle",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "scooter.idle"))
    );
    public static final RegistryObject<SoundEvent> SCOOTER_MOVING = SOUND_EVENTS.register("scooter.moving",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "scooter.moving"))
    );

    public static final RegistryObject<SoundEvent> PICKUP01_IDLE = SOUND_EVENTS.register("pickup01.idle",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "pickup01.idle"))
    );
    public static final RegistryObject<SoundEvent> PICKUP01_LO = SOUND_EVENTS.register("pickup01.lo",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "pickup01.lo"))
    );
    public static final RegistryObject<SoundEvent> PICKUP01_HI = SOUND_EVENTS.register("pickup01.hi",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "pickup01.hi"))
    );
}
