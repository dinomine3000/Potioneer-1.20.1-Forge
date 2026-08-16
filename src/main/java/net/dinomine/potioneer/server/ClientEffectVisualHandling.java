package net.dinomine.potioneer.server;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class ClientEffectVisualHandling {
    private static final List<Integer> mistEntities = new ArrayList<>();

    public static void addInvisibleEntity(Level level, int target){
        Entity ent = level.getEntity(target);
        if(!(ent instanceof LivingEntity livingEntity)) return;
        CapProvider.beyonder(livingEntity).ifPresent(cap -> {
            cap.getEffectsManager().addEffectNoRefresh(BeyonderEffects.MYSTERY_INVISIBLE.createInstance(9, 0, -1, false), cap, livingEntity);
        });
    }

    public static void removeInvisibleEntity(Level level, int target){
        Entity ent = level.getEntity(target);
        if(!(ent instanceof LivingEntity livingEntity)) return;
        CapProvider.beyonder(livingEntity).ifPresent(cap -> {
            cap.getEffectsManager().removeEffectImmediately(BeyonderEffects.MYSTERY_INVISIBLE.getEffectId(), cap, livingEntity);
        });
    }

    public static void addMistEntity(int target){
        mistEntities.add(target);
    }

    public static void removeMistEntity(int target){
        mistEntities.remove((Object) target);
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event){
        if(Minecraft.getInstance().level == null) return;
        for(int id: mistEntities){
            Entity ent = Minecraft.getInstance().level.getEntity(id);
            if(!(ent instanceof LivingEntity livingEntity)) continue;
            ParticleMaker.summonMistParticles(livingEntity);
        }
    }

}
