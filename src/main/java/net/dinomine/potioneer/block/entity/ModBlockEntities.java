package net.dinomine.potioneer.block.entity;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Potioneer.MOD_ID);

    public static RegistryObject<BlockEntityType<PotionCauldronBlockEntity>> POTION_CAULDRON_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("potion_cauldron_block_entity",
                    () -> BlockEntityType.Builder.of(PotionCauldronBlockEntity::new , ModBlocks.POTION_CAULDRON.get())
                            .build(null));

    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}