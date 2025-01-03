package net.dinomine.potioneer.event;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.commands.ChangeSanityCommand;
import net.dinomine.potioneer.commands.ChangeSpiritualityCommand;
import net.dinomine.potioneer.commands.ResetBeyonderCommand;
import net.dinomine.potioneer.entities.custom.ChryonEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
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

}
