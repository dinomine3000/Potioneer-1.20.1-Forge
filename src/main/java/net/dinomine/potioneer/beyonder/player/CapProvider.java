package net.dinomine.potioneer.beyonder.player;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CapProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    private final LivingEntity ent;

    public CapProvider(LivingEntity ent){
        this.ent = ent;
    }

    public static Capability<BeyonderCapability> BEYONDER_STATS = CapabilityManager.get(new CapabilityToken<>() {});
    public static Capability<EffectEntityCapability> EFFECT_ENTITIES = CapabilityManager.get(new CapabilityToken<>() {});

    private BeyonderCapability beyonderStats = null;
    private EffectEntityCapability effectEntity = null;
    private final LazyOptional<BeyonderCapability> beyonderOptional = LazyOptional.of(this::createBeyonderStats);
    private final LazyOptional<EffectEntityCapability> effectEntityOptional = LazyOptional.of(this::createEffectEntity);

    public static Optional<BeyonderCapability> beyonder(Entity target){return target.getCapability(BEYONDER_STATS).resolve();}

    private BeyonderCapability createBeyonderStats() {
        if(this.beyonderStats == null){
            this.beyonderStats = new BeyonderCapability(ent);
        }
        return this.beyonderStats;
    }

    private EffectEntityCapability createEffectEntity() {
        if(this.effectEntity == null){
            this.effectEntity = new EffectEntityCapability(ent);
        }
        return this.effectEntity;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        if(capability == BEYONDER_STATS){
            return beyonderOptional.cast();
        }
        if(capability == EFFECT_ENTITIES){
            return effectEntityOptional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();

        // Use distinct nested tags to prevent key collisions between capabilities
        CompoundTag beyonderTag = new CompoundTag();
        createBeyonderStats().saveNBTData(beyonderTag);
        nbt.put("BeyonderStats", beyonderTag);

        CompoundTag effectEntityTag = new CompoundTag();
        createEffectEntity().saveNBTData(effectEntityTag);
        nbt.put("EffectEntities", effectEntityTag);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        if (compoundTag.contains("BeyonderStats", CompoundTag.TAG_COMPOUND)) {
            createBeyonderStats().loadNBTData(compoundTag.getCompound("BeyonderStats"));
        } else {
            createBeyonderStats().loadNBTData(compoundTag);
        }

        if (compoundTag.contains("EffectEntities", CompoundTag.TAG_COMPOUND)) {
            createEffectEntity().loadNBTData(compoundTag.getCompound("EffectEntities"));
        } else {
            createEffectEntity().loadNBTData(compoundTag);
        }
    }
}
