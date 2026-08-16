package net.dinomine.potioneer.beyonder.events;

import net.dinomine.potioneer.beyonder.ModAttributes;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.mystery.GymnasticsEffect;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.entities.custom.CloneEntity;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific.DoubleJumpMessage;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.item.ItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber
public class ServerEventsMystery {

    @SubscribeEvent
    public static void onItemRightClick(PlayerInteractEvent.RightClickItem event){
        if(!event.getItemStack().is(Items.PAPER)) return;

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
