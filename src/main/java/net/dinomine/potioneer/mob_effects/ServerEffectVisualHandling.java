package net.dinomine.potioneer.mob_effects;

import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.effects.EntityEffectVisualMessage;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@OnlyIn(Dist.DEDICATED_SERVER)
@Mod.EventBusSubscriber
public class ServerEffectVisualHandling {
    private static final Set<Integer> mistEntities = new HashSet<>();

    public static void addMistEntity(LivingEntity target){
        if(target.level().isClientSide()) return;
        mistEntities.add(target.getId());
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new EntityEffectVisualMessage(target.getId(), EntityEffectVisualMessage.Operation.ADD, "mist"));
    }

    public static void removeMistEntity(LivingEntity target){
        if(target.level().isClientSide()) return;
        if(mistEntities.remove(target.getId()))
            PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new EntityEffectVisualMessage(target.getId(), EntityEffectVisualMessage.Operation.REMOVE, "mist"));
    }

    @SubscribeEvent
    public static void onLivingDie(LivingDeathEvent event){
        if(event.getEntity().level().isClientSide()) return;
        removeMistEntity(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
        if(event.getEntity().level().isClientSide()) return;
        PacketHandler.sendMessageSTC(new EntityEffectVisualMessage(mistEntities, EntityEffectVisualMessage.Operation.ADD, "mist"), event.getEntity());
    }
}
