package net.dinomine.potioneer.event;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.beyonder.client.ClientStatsData;
import net.dinomine.potioneer.beyonder.client.KeyBindings;
import net.dinomine.potioneer.util.misc.MysticismHelper;
import net.dinomine.potioneer.beyonder.pathways.BeyonderPathway;
import net.dinomine.potioneer.beyonder.client.screen.BeyonderScreen;
import net.dinomine.potioneer.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Potioneer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeHandler {
    private static boolean leftClickFlag = false;


    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<Component> tooltip = event.getToolTip();
        Level level = event.getEntity() != null ? event.getEntity().level() : null;
        // Only run client-side
        if (level != null) {
            if(ClientStatsData.getPathwayId() > -1){
                int spirituality = (int) MysticismHelper.getSpiritualityOfItem(stack);
                if (spirituality > 0) tooltip.add(Component.literal("Spirituality: " + spirituality).withStyle(ChatFormatting.GRAY));
            }
            if(stack.hasTag() && stack.getTag().contains("beyonder_info")){
                tooltip.add(Component.literal("Sequence Level " + stack.getTag().getCompound("beyonder_info").getInt("id")%10));
            }
            if(stack.hasTag() && stack.getTag().contains("recipe_data")){
                tooltip.add(Component.translatable("potioneer.beyonder.sequence." +
                        BeyonderPathway.getSequenceNameFromId(stack.getTag().getCompound("recipe_data").getInt("id"), false)));
            }
                //tooltip.add(Component.literal("★ Special Item!").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        }
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event){
        Minecraft minecraft = Minecraft.getInstance();

        if(!KeyBindings.INSTANCE.quickAbilityKey.isDown()) ClientStatsData.keyPressed = false;

        if(ClientStatsData.getPathwayId() > -1 && KeyBindings.INSTANCE.beyonderMenuKey.consumeClick() && minecraft.player != null ){
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
    public static void onItemLeftClick(InputEvent.MouseButton event){
        if(Minecraft.getInstance().player != null && Minecraft.getInstance().player.getMainHandItem().is(ModItems.LEYMANOS_TRAVELS.get())){
            if(event.getButton() == 0 && event.getAction() == 1 && Minecraft.getInstance().screen == null){
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onLeftClick(InputEvent.MouseButton event){
        if(!ClientAbilitiesData.showHotbar) return;
        Minecraft minecraft = Minecraft.getInstance();
        boolean success = false;
        if(minecraft.player != null && event.getButton() == 0 && event.getAction() == 1){
            System.out.println("Left Click event");
            success = ClientAbilitiesData.useAbility(minecraft.player);
        }
        if(success){
            event.setCanceled(true);
        }
    }

}
