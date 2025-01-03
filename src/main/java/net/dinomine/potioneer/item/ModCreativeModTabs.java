package net.dinomine.potioneer.item;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Potioneer.MOD_ID);

    public static final RegistryObject<CreativeModeTab> POTIONEER_TAB = CREATIVE_MODE_TABS.register("potioneer_tab",
            () -> CreativeModeTab.builder().icon( () -> new ItemStack(ModItems.SAPPHIRE.get()))
                    .title(Component.translatable("creativetab.potioneer_tab"))
                    .displayItems((itemDisplayParameters, output) -> {

                        output.accept(ModItems.SAPPHIRE.get());

                        output.accept(ModBlocks.SAPPHIRE_BLOCK.get());
                        output.accept(ModBlocks.RAW_SAPPHIRE_BLOCK.get());

                        output.accept(ModBlocks.SAPPHIRE_ORE.get());
                        output.accept(ModBlocks.DEEPSLATE_SAPPHIRE_ORE.get());
                        output.accept(ModBlocks.POTION_CAULDRON.get());
                        output.accept(ModItems.PECAN_SPAWN_EGG.get());
                        output.accept(ModItems.CHRYON_SPAWN_EGG.get());
                        output.accept(ModItems.BEYONDER_POTION.get());
                        output.accept(ModItems.PECAN_LEAF.get());
                        output.accept(ModItems.PECAN_SHELL.get());
                        output.accept(ModItems.SOLSEER.get());
                        output.accept(ModItems.WANDERING_CACTUS_PRICK.get());
                    })
                    .build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
