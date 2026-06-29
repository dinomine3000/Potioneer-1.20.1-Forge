package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.entities.custom.effects.AbstractEffectEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AutoRegisterCapability
public class EffectEntityCapability {
    private final LivingEntity entity;
    private final List<AbstractEffectEntity> effects = new ArrayList<>();
    //private static HashMap<String, BiFunction<CompoundTag, Level, Optional<Entity>>> constructors = new HashMap<>();
    /*static {
        constructors.put(ModEntities.WATER_BLOCK_EFFECT_ENTITY.get().getDescriptionId(), (tag, level) -> ModEntities.WATER_BLOCK_EFFECT_ENTITY.get().create(tag, level));

    }

    private static void registerEntity(EntityType entityType){
        constructors.put(entityType.getDescriptionId(), EntityType::create);

    }*/

    public EffectEntityCapability(LivingEntity ent) {
        entity = ent;
    }

    public void addEffect(AbstractEffectEntity effect){
        if(entity.level().isClientSide()) return;
        if(effects.stream().anyMatch(eff -> eff.getId() == effect.getId())) return;
        effects.add(effect);
    }

    public void stopEffect(AbstractEffectEntity effect){
        if(entity.level().isClientSide()) return;
        effects.removeIf(eff -> eff.getId() == effect.getId());
    }

    public void saveNBTData(CompoundTag nbt) {
        if(entity.level().isClientSide) return;
        ListTag listTag = new ListTag();
        for(AbstractEffectEntity effect: effects){
            CompoundTag tag = new CompoundTag();
            if(!effect.save(tag))
                continue;
            listTag.add(tag);
        }
        nbt.put("effectEntities", listTag);
    }

    public void loadNBTData(CompoundTag compoundTag) {
        if(entity.level().isClientSide) return;
        ListTag effectsList = compoundTag.getList("effectEntities", Tag.TAG_COMPOUND);
        for(int i = 0; i < effectsList.size(); i++){
            Optional<Entity> effect = EntityType.create(effectsList.getCompound(i), entity.level());
            if(effect.isPresent() && effect.get() instanceof AbstractEffectEntity effectEntity)
                effects.add(effectEntity);
        }

    }
}
