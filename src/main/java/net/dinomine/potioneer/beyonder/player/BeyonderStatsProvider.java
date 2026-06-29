package net.dinomine.potioneer.beyonder.player;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BeyonderStatsProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    private final LivingEntity ent;

    public BeyonderStatsProvider(LivingEntity ent){
        this.ent = ent;
    }

    public static Capability<LivingEntityBeyonderCapability> BEYONDER_STATS = CapabilityManager.get(new CapabilityToken<>() {});
    public static Capability<EffectEntityCapability> EFFECT_ENTITIES = CapabilityManager.get(new CapabilityToken<>() {});

    private LivingEntityBeyonderCapability beyonderStats = null;
    private EffectEntityCapability effectEntity = null;
    private final LazyOptional<LivingEntityBeyonderCapability> beyonderOptional = LazyOptional.of(this::createBeyonderStats);
    private final LazyOptional<EffectEntityCapability> effectEntityOptional = LazyOptional.of(this::createEffectEntity);

    private LivingEntityBeyonderCapability createBeyonderStats() {
        if(this.beyonderStats == null){
            this.beyonderStats = new LivingEntityBeyonderCapability(ent);
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
        createBeyonderStats().saveNBTData(nbt);
        createEffectEntity().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        createBeyonderStats().loadNBTData(compoundTag);
        createEffectEntity().loadNBTData(compoundTag);
    }
}
