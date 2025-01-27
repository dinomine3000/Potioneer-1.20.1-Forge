package net.dinomine.potioneer.beyonder.effects.paragon;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class BeyonderDurabilityEffect extends BeyonderEffect {
    public BeyonderDurabilityEffect(int level, float cost, int time, boolean active, BeyonderEffects.EFFECT id){
        this.sequenceLevel = level;
        this.cost = cost;
        this.maxLife = time;
        this.ID = id;
        this.lifetime = 0;
        this.active = active;
        this.name = "Paragon Crafting";
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {
        System.out.println(maxLife);
    }

    @Override
    protected void doTick(EntityBeyonderManager cap, LivingEntity target) {
        if(lifetime % 60 == 0 && lifetime > 0){
            System.out.println("one second has ticked.");
            if(sequenceLevel < 9){
                if(target instanceof Player player){
                    Inventory inv = player.getInventory();
                    int size = inv.getContainerSize();
                    for (int i = 0; i < size; i++) {
                        if(inv.getItem(i).isDamageableItem()){
                            ItemStack item = inv.getItem(i);
                            item.setDamageValue(Math.min(item.getDamageValue() - 10*(10-sequenceLevel), 0));
                        }
                    }
                }
            }
            ItemStack mainItem = target.getMainHandItem();
            mainItem.setDamageValue(Math.max(mainItem.getDamageValue() - 5*(int)Math.pow(10-sequenceLevel, 2) - 15, 0));
            ItemStack offhandItem = target.getOffhandItem();
            offhandItem.setDamageValue(Math.max(offhandItem.getDamageValue() - 5*(int)Math.pow(10-sequenceLevel, 2) - 15, 0));
        }

    }

    @Override
    public void stopEffects(EntityBeyonderManager cap, LivingEntity target) {
    }
}
