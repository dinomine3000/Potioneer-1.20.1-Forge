package net.dinomine.potioneer.beyonder.player;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BeyonderStatsProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<PlayerBeyonderStats> BEYONDER_STATS = CapabilityManager.get(new CapabilityToken<PlayerBeyonderStats>() {});

    private PlayerBeyonderStats beyonderStats = null;
    private final LazyOptional<PlayerBeyonderStats> optional = LazyOptional.of(this::createBeyonderStats);

    private PlayerBeyonderStats createBeyonderStats() {
        if(this.beyonderStats == null){
            this.beyonderStats = new PlayerBeyonderStats();
        }
        return this.beyonderStats;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        if(capability == BEYONDER_STATS){
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createBeyonderStats().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        createBeyonderStats().loadNBTData(compoundTag);

    }
}
