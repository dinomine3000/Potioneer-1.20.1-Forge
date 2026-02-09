package net.dinomine.potioneer.beyonder.effects.misc;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific.SourceRecipientUpdateMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class AbstractSourceRecipientEffect extends BeyonderEffect {
    protected HashMap<UUID, Integer> sources = new HashMap<>();

    protected List<Player> getPlayerList(Level level){
        List<Player> res = new ArrayList<>();
        for(UUID id: sources.keySet()){
            Player player = level.getPlayerByUUID(id);
            if(player != null) res.add(player);
        }
        return res;
    }

    public void setSourceOnClient(HashMap<UUID, Integer> other){
        this.sources = other;
    }

    protected void addSource(UUID id, int time, @Nullable LivingEntity target){
        sources.put(id, time);
        if(target == null) return;
        PacketHandler.sendMessageSTC(new SourceRecipientUpdateMessage(getId(), sources), target);
    }

    protected void tickDownTime(LivingEntity target) {
        for(UUID id: new ArrayList<>(sources.keySet())){
            int time = sources.get(id);
            if(time < 0){
                sources.remove(id);
                PacketHandler.sendMessageSTC(new SourceRecipientUpdateMessage(getId(), sources), target);
            } else sources.put(id, time-1);
        }
        if(sources.isEmpty()){
            endEffectWhenPossible();
        }
    }

    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        nbt.putInt("mapSize", sources.size());
        int i = 0;
        for(UUID id: sources.keySet()){
            nbt.putUUID("entity_" + i, id);
            nbt.putInt("timeout_" + i, sources.get(id));
            i++;
        }
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        int size = nbt.getInt("mapSize");
        for(int i = 0; i < size; i++){
            UUID id = nbt.getUUID("entity_" + i);
            int time = nbt.getInt("timeout_" + i);
            sources.put(id, time);
        }
    }

}
