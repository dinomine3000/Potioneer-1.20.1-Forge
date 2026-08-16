package net.dinomine.potioneer.beyonder.events;

import net.dinomine.potioneer.beyonder.ModAttributes;
import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.mystery.MagicTricksAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.mystery.GymnasticsEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.entities.custom.CloneEntity;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific.DoubleJumpMessage;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.item.ItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;


@Mod.EventBusSubscriber
public class ServerEventsMystery {

    @SubscribeEvent
    public static void onItemRightClick(PlayerInteractEvent.RightClickItem event){
        if(!event.getItemStack().is(Items.PAPER)) return;
        LivingEntity ent = event.getEntity();
        Optional<BeyonderCapability> optCap = CapProvider.beyonder(ent);
        if(optCap.isEmpty()) return;
        BeyonderCapability cap = optCap.get();
        Ability abl = cap.getAbilitiesManager().getFirstAbilityOutOfCooldown(Abilities.TRICKS.getAblId());
        if(!(abl instanceof MagicTricksAbility tricksAbility)) return;
        MagicTricksAbility.doPaper(tricksAbility, cap, ent, false);
        if(!(ent instanceof Player player) || !player.isCreative()) event.getItemStack().shrink(1);
    }

    @SubscribeEvent
    public static void onEntityFall(LivingFallEvent event){
        if(!AbilityFunctionHelper.hasEffect(BeyonderEffects.MYSTERY_BOUNCY.getEffectId(), event.getEntity())) return;
        LivingEntity ent = event.getEntity();
        Vec3 motion = ent.getDeltaMovement();
        AbilityFunctionHelper.pushEntity(ent, new Vec3(0, 1, 0));
    }

    @SubscribeEvent
    public static void onEntityJump(LivingEvent.LivingJumpEvent event){
        if(!(event.getEntity() instanceof Player player)) return;
        CapProvider.beyonder(event.getEntity()).ifPresent(cap -> {
            GymnasticsEffect eff = AbilityFunctionHelper.getEffectOnTarget(BeyonderEffects.MYSTERY_GYMNASTICS.getEffectId(), event.getEntity());
            if(eff != null
                    && player.level().isClientSide() && eff.canJump()
                    && !AbilityFunctionHelper.isEntityStandingOnGround(0, player, false)){
                PacketHandler.sendMessageCTS(new DoubleJumpMessage());
            }
            if(eff != null) eff.onJump(event.getEntity(), cap);
        });
        AbilityFunctionHelper.pushEntity(player, new Vec3(0, ModAttributes.getJump(player), 0));
    }

}
