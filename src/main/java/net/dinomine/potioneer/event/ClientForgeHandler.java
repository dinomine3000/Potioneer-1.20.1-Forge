package net.dinomine.potioneer.event;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.beyonder.client.ClientStatsData;
import net.dinomine.potioneer.beyonder.client.KeyBindings;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.misc.MysticismHelper;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.screen.BeyonderAbilitiesScreen;
import net.dinomine.potioneer.beyonder.screen.BeyonderScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.swing.text.JTextComponent;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Potioneer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeHandler {


    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<Component> tooltip = event.getToolTip();
        Level level = event.getEntity() != null ? event.getEntity().level() : null;
        // Only run client-side
        if (level != null && ClientStatsData.getPathwayId() > -1) {
            int spirituality = (int) MysticismHelper.getSpiritualityOfItem(stack);
            if (spirituality > 0) tooltip.add(Component.literal("Spirituality: " + spirituality).withStyle(ChatFormatting.GRAY));
                //tooltip.add(Component.literal("★ Special Item!").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        }
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event){
        Minecraft minecraft = Minecraft.getInstance();

        if(!KeyBindings.INSTANCE.quickAbilityKey.isDown()) ClientStatsData.keyPressed = false;

        if(KeyBindings.INSTANCE.beyonderMenuKey.consumeClick() && minecraft.player != null && ClientStatsData.getPathwayId() > -1){
            Minecraft.getInstance().setScreen(new BeyonderScreen());
            //DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().setScreen(new BeyonderScreen()));
        } else if(ClientStatsData.getPathwayId() > -1 && KeyBindings.INSTANCE.quickAbilityKey.consumeClick() && minecraft.player != null){
            if (!ClientStatsData.keyPressed){
                ClientAbilitiesData.useQuickAbility(minecraft.player);
                ClientStatsData.keyPressed = true;
            }
        }

        ClientAbilitiesData.setShowHotbar(KeyBindings.INSTANCE.showHotbarKey.isDown());
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
