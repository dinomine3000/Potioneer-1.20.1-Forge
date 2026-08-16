package net.dinomine.potioneer.server;

import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.effects.EntityEffectVisualMessage;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber
public class ServerEffectVisualHandling {
    private static final Set<Integer> mistEntities = new HashSet<>();
    private static final Set<Integer> invisibleEntities = new HashSet<>();

    // --- Mist Entity Handling ---
    public static void addMistEntity(LivingEntity target) {
        if (target.level().isClientSide()) return;
        mistEntities.add(target.getId());
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new EntityEffectVisualMessage(target.getId(), EntityEffectVisualMessage.Operation.ADD, "mist"));
    }

    public static void removeMistEntity(LivingEntity target) {
        if (target.level().isClientSide()) return;
        if (mistEntities.remove(target.getId())) {
            PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new EntityEffectVisualMessage(target.getId(), EntityEffectVisualMessage.Operation.REMOVE, "mist"));
        }
    }

    // --- Invisible Entity Handling ---
    public static void addInvisibleEntity(LivingEntity target) {
        if (target.level().isClientSide()) return;
        invisibleEntities.add(target.getId());
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new EntityEffectVisualMessage(target.getId(), EntityEffectVisualMessage.Operation.ADD, "invisible"));
    }

    public static void removeInvisibleEntity(LivingEntity target) {
        if (target.level().isClientSide()) return;
        if (invisibleEntities.remove(target.getId())) {
            PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new EntityEffectVisualMessage(target.getId(), EntityEffectVisualMessage.Operation.REMOVE, "invisible"));
        }
    }

    // --- Event Listeners ---
    @SubscribeEvent
    public static void onLivingDie(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        removeMistEntity(event.getEntity());
        removeInvisibleEntity(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity().level().isClientSide()) return;

        // Sync both existing mist and invisible entity sets to the newly joined player
        if (!mistEntities.isEmpty()) {
            PacketHandler.sendMessageSTC(new EntityEffectVisualMessage(mistEntities, EntityEffectVisualMessage.Operation.ADD, "mist"), event.getEntity());
        }
        if (!invisibleEntities.isEmpty()) {
            PacketHandler.sendMessageSTC(new EntityEffectVisualMessage(invisibleEntities, EntityEffectVisualMessage.Operation.ADD, "invisible"), event.getEntity());
        }
    }
}