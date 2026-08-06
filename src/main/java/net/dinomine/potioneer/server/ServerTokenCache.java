package net.dinomine.potioneer.server;

import joptsimple.util.KeyValuePair;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber
public class ServerTokenCache {
    private static final Map<UUID, Integer> tokenMap = new HashMap<>();
    private static final Map<UUID, CompoundTag> tokenDataMap = new HashMap<>();

    public static void addToken(UUID token, int ttl, CompoundTag data){
        tokenMap.put(token, ttl);
        tokenDataMap.put(token, data);
    }

    public static boolean validateToken(UUID token){return tokenMap.containsKey(token);}
    public static CompoundTag getTokenData(UUID token, boolean invalidate){
        CompoundTag data = tokenDataMap.get(token);
        if(invalidate) invalidateToken(token);
        return data;
    }

    public static void invalidateToken(UUID token){
        tokenMap.remove(token);
        tokenDataMap.remove(token);
    }
    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent event){
        for(UUID token: new HashSet<>(tokenMap.keySet())){
            int ttl = tokenMap.get(token) - 1;
            if(ttl < 1){
                tokenDataMap.remove(token);
                tokenMap.remove(token);
            }
            else tokenMap.put(token, ttl);
        }
    }
}
