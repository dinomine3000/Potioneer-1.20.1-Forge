package net.dinomine.potioneer.event;

import com.mojang.blaze3d.platform.InputConstants;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.beyonder.client.ClientStatsData;
import net.dinomine.potioneer.beyonder.client.KeyBindings;
import net.dinomine.potioneer.beyonder.client.screen.AbilityOptionsScreen;
import net.dinomine.potioneer.beyonder.client.screen.BeyonderScreen;
import net.dinomine.potioneer.beyonder.pathways.BeyonderPathway;
import net.dinomine.potioneer.beyonder.pathways.Pathways;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.recipe.PotionRecipeData;
import net.dinomine.potioneer.util.ParticleMaker;
import net.dinomine.potioneer.util.misc.ModNbtUtils;
import net.dinomine.potioneer.util.PotioneerMathHelper;
import net.dinomine.potioneer.util.misc.MysticalItemHelper;
import net.dinomine.potioneer.util.misc.MysticismHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Potioneer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeHandler {

    @SubscribeEvent
    public static void playerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event){
        ParticleMaker.clearCache();
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<Component> tooltip = event.getToolTip();
        Level level = event.getEntity() != null ? event.getEntity().level() : null;
        boolean appraiser = ClientAbilitiesData.hasAbility(Abilities.APPRAISAL.getAblId());
        // Only run client-side
        if (level != null) {
            if(ClientStatsData.getPathwaySequenceId() > -1){
                int spirituality = (int) MysticismHelper.getSpiritualityOfItem(stack);
                if (spirituality > 0){
                    tooltip.add(Component.translatable("tooltip.potioneer.spirituality", spirituality).withStyle(ChatFormatting.GRAY));
                    if(appraiser){
                        String name = MysticismHelper.getPlayerNameOfItem(stack);
                        tooltip.add(Component.translatable("tooltip.potioneer.spirituality_player", name));
                    }
                }
            }
            if(ModNbtUtils.hasTag(ModNbtUtils.TAGS.BEYONDER, stack)){
                int pathSeq = ModNbtUtils.BeyonderInfoTag.getAssociatedPathSeqLevel(ModNbtUtils.getTagFromItem(ModNbtUtils.TAGS.BEYONDER, stack));
                if(appraiser){
                    BeyonderPathway pathway = Pathways.getPathwayById(Math.floorDiv(pathSeq, 10));
                    tooltip.add(Component.empty()
                            .append(pathway.getPathwayName()).append(" ")
                                .append(pathway.getSequenceComponentFromId(pathSeq%10)));
                }
                else
                    tooltip.add(Component.translatable("potioneer.generic_beyonder.sequence", pathSeq%10));
            }
            if(stack.hasTag() && stack.getTag().contains("recipe_data")){
                tooltip.add(Component.literal(PotionRecipeData.getName(stack.getTag().getCompound("recipe_data"))));
            }
            if(MysticalItemHelper.isArtifact(stack)){
                if(appraiser)
                    for(AbilityInfo info: MysticalItemHelper.getArtifactFromItem(stack).getAbilitiesInfo(true)){
                        tooltip.add(info.getMutableNameComponent().withStyle(ChatFormatting.ITALIC));
                    }
                if(ModNbtUtils.ArtifactInfoTag.doesArtifactNeedCharge(stack)){
                    if(ModNbtUtils.ArtifactInfoTag.isArtifactCharged(stack)){
                        tooltip.add(Component.translatable("tooltip.potioneer.artifact_charge", (int) ModNbtUtils.ArtifactInfoTag.getArtifactCharge(stack)));
                    } else {
                        tooltip.add(Component.translatable("tooltip.potioneer.artifact_out_of_charge"));
                    }
                }
            }
            if(appraiser && ModNbtUtils.hasTag(ModNbtUtils.TAGS.POTION, stack)){
                CompoundTag potionTag = ModNbtUtils.getTagFromItem(ModNbtUtils.TAGS.POTION, stack);
                String name = ModNbtUtils.PotionInfoTag.getPotionName(potionTag);
                boolean conflict = ModNbtUtils.PotionInfoTag.isConflict(name);
                if(conflict){
                    tooltip.add(Component.translatable("tooltip.potioneer.conflicting_potion").withStyle(ChatFormatting.RED));
                } else if(PotioneerMathHelper.isInteger(name)){
                    boolean isComplete = ModNbtUtils.PotionInfoTag.isPotionComplete(potionTag);
                    int pathwaySequenceId = Integer.parseInt(name);
                    tooltip.add(Component.translatable("tooltip.potioneer." + (isComplete ? "valid_potion" : "incomplete_potion")).withStyle(ChatFormatting.AQUA));

                    BeyonderPathway pathway = Pathways.getPathwayBySequenceId(pathwaySequenceId);
                    tooltip.add(Component.empty()
                            .append(pathway.getPathwayName()).append(" - ")
                            .append(pathway.getSequenceComponentFromId(pathwaySequenceId%10)));
                }
            }
            if(stack.is(ModItems.CHARM.get())){
                if(ModNbtUtils.hasTag(ModNbtUtils.TAGS.CHARM, stack)){
                    tooltip.add(Component.translatable("beyondereffect.potioneer." + ModNbtUtils.CharmInfoTag.getEffectId(ModNbtUtils.getTagFromItem(ModNbtUtils.TAGS.CHARM, stack))));
                } else {
                    tooltip.add(Component.translatable("charm.potioneer.no_effect"));
                }
            }
                //tooltip.add(Component.literal("★ Special Item!").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        }
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event){
        Minecraft minecraft = Minecraft.getInstance();
        if(Minecraft.getInstance().player == null) return;

        if(!KeyBindings.INSTANCE.quickAbilityKey.isDown()) ClientStatsData.keyPressed = false;

        if(ClientStatsData.getPathwaySequenceId() > -1 && KeyBindings.INSTANCE.beyonderMenuKey.consumeClick() && minecraft.player != null ){
            Minecraft.getInstance().setScreen(new BeyonderScreen());
            //DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().setScreen(new BeyonderScreen()));
        } else if(ClientStatsData.getPathwaySequenceId() > -1 && KeyBindings.INSTANCE.quickAbilityKey.consumeClick() && minecraft.player != null){
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
        if(!ClientAbilitiesData.isHotbarVisible()) return;
        ClientAbilitiesData.changeCaret((int)event.getScrollDelta());
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onLeftClick(InputEvent.MouseButton.Pre event){
//        if(Minecraft.getInstance().player != null && Minecraft.getInstance().player.getMainHandItem().is(ModItems.LEYMANOS_TRAVELS.get())){
//            if(!Minecraft.getInstance().isPaused() && event.getButton() == 0 && event.getAction() == 1 && Minecraft.getInstance().screen == null){
//                event.setCanceled(true);
//            }
//        }
        Minecraft minecraft = Minecraft.getInstance();
        boolean success = false;
        if(minecraft.player == null) return;
        if(event.getAction() == InputConstants.PRESS && minecraft.screen == null){
            if(!ClientAbilitiesData.configScreenOpenAnimation && ClientAbilitiesData.isHotbarVisible() && ClientAbilitiesData.isHotbarValid()){
                if(event.getButton() == InputConstants.MOUSE_BUTTON_LEFT)
                    success = ClientAbilitiesData.useAbility(minecraft.player, true);
                else if(event.getButton() == InputConstants.MOUSE_BUTTON_RIGHT)
                    success = ClientAbilitiesData.useAbility(minecraft.player, false);
            }
        }

        if(success){
            event.setCanceled(true);
        }

        if(event.getAction() == InputConstants.RELEASE)
            AbilityOptionsScreen.mouseRelease();
    }

}
