package net.dinomine.potioneer.event;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.beyonder.client.ClientStatsData;
import net.dinomine.potioneer.beyonder.client.KeyBindings;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.screen.BeyonderScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.swing.text.JTextComponent;

@Mod.EventBusSubscriber(modid = Potioneer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeHandler {
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event){
        Minecraft minecraft = Minecraft.getInstance();

        if(!KeyBindings.INSTANCE.quickAbilityKey.isDown()) ClientStatsData.keyPressed = false;

        if(KeyBindings.INSTANCE.beyonderMenuKey.consumeClick() && minecraft.player != null){
            Minecraft.getInstance().setScreen(new BeyonderScreen());
            //DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().setScreen(new BeyonderScreen()));
        } else if(ClientStatsData.getPathwayId() > -1 && KeyBindings.INSTANCE.quickAbilityKey.consumeClick() && minecraft.player != null){
            if (!ClientStatsData.keyPressed){
                ClientAbilitiesData.useAbility(minecraft.player);
                ClientStatsData.keyPressed = true;
            }
        }

        ClientAbilitiesData.showHotbar = KeyBindings.INSTANCE.showHotbarKey.isDown();
        ClientAbilitiesData.tick(minecraft.getPartialTick());

    }

    @SubscribeEvent
    public static void onScrollWheel(InputEvent.MouseScrollingEvent event){
        if(!ClientAbilitiesData.showHotbar) return;
        ClientAbilitiesData.changeCaret((int)event.getScrollDelta());
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onLeftClick(InputEvent.MouseButton event){
        if(!ClientAbilitiesData.showHotbar) return;
        Minecraft minecraft = Minecraft.getInstance();
        boolean success = false;
        if(minecraft.player != null && event.getButton() == 0 && event.getAction() == 1){
            success = ClientAbilitiesData.useAbility(minecraft.player);
        }
        if(success){
            event.setCanceled(true);
        }
    }

}
