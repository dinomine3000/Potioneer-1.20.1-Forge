package net.dinomine.potioneer.event;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.client.KeyBindings;
import net.dinomine.potioneer.beyonder.screen.BeyonderScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Potioneer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeHandler {
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event){
        Minecraft minecraft = Minecraft.getInstance();
        if(KeyBindings.INSTANCE.beyonderMenuKey.consumeClick() && minecraft.player != null){
            Minecraft.getInstance().setScreen(new BeyonderScreen());
            //DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().setScreen(new BeyonderScreen()));
        } else if(KeyBindings.INSTANCE.quickAbilityKey.consumeClick() && minecraft.player != null){

        }
    }
}
