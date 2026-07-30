package net.dinomine.potioneer.mob_effects;

import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class ClientEffectVisualHandling {
    private static final List<Integer> mistEntities = new ArrayList<>();

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
