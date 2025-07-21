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

    public static RegistryObject<BlockEntityType<MinerLightBlockEntity>> MINER_LIGHT_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("miner_light_block_entity",
                    () -> BlockEntityType.Builder.of(MinerLightBlockEntity::new, ModBlocks.MINER_LIGHT.get())
                            .build(null));

    public static RegistryObject<BlockEntityType<WaterTrapBlockEntity>> WATER_TRAP_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("water_trap_block_entity",
                    () -> BlockEntityType.Builder.of(WaterTrapBlockEntity::new, ModBlocks.WATER_TRAP_BLOCK.get())
                            .build(null));

    public static RegistryObject<BlockEntityType<PriestLightBlockEntity>> PRIEST_LIGHT_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("priest_light_block_entity",
                    () -> BlockEntityType.Builder.of(PriestLightBlockEntity::new, ModBlocks.PRIEST_LIGHT.get())
                            .build(null));

    public static RegistryObject<BlockEntityType<RitualAltarBlockEntity>> RITUAL_ALTAR_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("ritual_altar_block_entity",
                    () -> BlockEntityType.Builder.of(RitualAltarBlockEntity::new , ModBlocks.RITUAL_ALTAR.get())
                            .build(null));

    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}