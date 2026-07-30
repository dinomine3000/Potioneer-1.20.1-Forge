package net.dinomine.potioneer.mob_effects;

import com.eliotlash.mclib.math.functions.limit.Min;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.effects.EntityEffectVisualMessage;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
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
    public static void tick(TickEvent event){
        if(Minecraft.getInstance().level == null) return;
        for(int id: mistEntities){
            Entity ent = Minecraft.getInstance().level.getEntity(id);
            if(!(ent instanceof LivingEntity livingEntity)) continue;
            ParticleMaker.summonMistParticles(livingEntity);
        }
    }

}
