package net.dinomine.potioneer.event;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.commands.*;
import net.dinomine.potioneer.entities.custom.ChryonEntity;
import net.dinomine.potioneer.savedata.PotionFormulaSaveData;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.List;

@Mod.EventBusSubscriber(modid = Potioneer.MOD_ID)
public class ModEvents {


    @SubscribeEvent
    public static void commandsRegister(RegisterCommandsEvent event){
        new ResetBeyonderCommand(event.getDispatcher());
        new ChangeSpiritualityCommand(event.getDispatcher());
        new ChangeSanityCommand(event.getDispatcher());
        new ResetBeyonderEffectsCommand(event.getDispatcher());
        new RefreshFormulasCommand(event.getDispatcher());

        ConfigCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void chryonSpawnEvent(MobSpawnEvent.FinalizeSpawn event){
        if(event.getEntity() instanceof ChryonEntity chryon){
            ItemStack stack = Items.DIAMOND_SWORD.getDefaultInstance();
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
