package net.dinomine.potioneer.event;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.commands.*;
import net.dinomine.potioneer.entities.custom.ChryonEntity;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.savedata.PotionFormulaSaveData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

@Mod.EventBusSubscriber(modid = Potioneer.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void commandsRegister(RegisterCommandsEvent event){
        new ResetBeyonderCommand(event.getDispatcher());
        new ChangeSpiritualityCommand(event.getDispatcher());
        new ChangeSanityCommand(event.getDispatcher());
        new ResetBeyonderEffectsCommand(event.getDispatcher());
        new RefreshFormulasCommand(event.getDispatcher());
        new GiveCharacteristicCommand(event.getDispatcher());
        new ChangeActingCommand(event.getDispatcher());

        ConfigCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void chryonSpawnEvent(MobSpawnEvent.FinalizeSpawn event){
        if(event.getEntity() instanceof ChryonEntity chryon){
            ItemStack stack = ModItems.FROZEN_SWORD.get().getDefaultInstance();
            EquipmentSlot slot = LivingEntity.getEquipmentSlotForItem(stack);
            chryon.setItemSlot(slot, stack.copy());
        }
    }

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event){
//        System.out.println("--------------world lodead call-------------");
        if(event.getLevel() != null){
            MinecraftServer lvl = event.getLevel().getServer();
            if(lvl == null || lvl.overworld() != event.getLevel()) return;
            PotionFormulaSaveData.from((ServerLevel) event.getLevel());
        }
    }
}
