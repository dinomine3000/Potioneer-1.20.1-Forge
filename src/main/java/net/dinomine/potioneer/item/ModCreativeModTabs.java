package net.dinomine.potioneer.item;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
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
                        output.accept(ModItems.GHOST_FISHING_ROD.get());
                        output.accept(ModItems.PECAN_SPAWN_EGG.get());
                        output.accept(ModItems.CHRYON_SPAWN_EGG.get());
                        output.accept(ModItems.BEYONDER_POTION.get());
                        output.accept(ModItems.VIAL.get());
                        output.accept(ModItems.FLASK.get());
                        output.accept(ModItems.FORMULA.get());
                        //output.accept(ModItems.GOLDEN_DROP.get());
                        output.accept(ModItems.COIN_ITEM.get());
                        output.accept(ModItems.DIVINATION_ROD.get());
                    })
                    .build());

    public static final RegistryObject<CreativeModeTab> POTIONEER_INGREDIENTS_TAB = CREATIVE_MODE_TABS.register("ingredients_tab",
            () -> CreativeModeTab.builder().icon( () -> new ItemStack(ModItems.SOLSEER.get()))
                    .title(Component.translatable("creativetab.potioneer_ingredients_tab"))
                    .displayItems(( itemDisplayParameters, output) -> {
//                        output.accept(ModBlocks.MUTATED_MUSHROOM.get());
                        output.accept(ModItems.SAPPHIRE.get());
                        output.accept(ModItems.PECAN_LEAF.get());
                        output.accept(ModItems.PECAN_SHELL.get());
                        output.accept(ModItems.SOLSEER.get());
                        output.accept(ModItems.WANDERING_CACTUS_PRICK.get());

                        //cactus sap item
                        ItemStack vial = new ItemStack(ModItems.VIAL.get());
                        CompoundTag tag = new CompoundTag();
                        CompoundTag potionInfo = new CompoundTag();
                        potionInfo.putInt("amount", 1);
                        potionInfo.putString("name", "cactus_sap");
                        potionInfo.putInt("color", 65280);
                        tag.put("potion_info", potionInfo);
                        vial.setTag(tag);
                        output.accept(vial);

                    }).build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
