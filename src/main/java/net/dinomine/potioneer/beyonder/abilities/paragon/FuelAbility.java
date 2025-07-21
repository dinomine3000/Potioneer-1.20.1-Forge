package net.dinomine.potioneer.beyonder.abilities.paragon;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.util.misc.MysticismHelper;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.item.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FuelAbility extends Ability {
    private static final Item sourceItem = Items.CAKE;
    private static final float percentCost = 0.4f;
    private static final float percentDelta = 0.2f;
    private static final Logger log = LoggerFactory.getLogger(FuelAbility.class);

    public FuelAbility(int sequence){
        this.info = new AbilityInfo(109, 80, "Create Golden Drop", 40 + sequence, 0, this.getCooldown(), "fuel");
        this.isActive = true;
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public boolean active(LivingEntityBeyonderCapability cap, LivingEntity target) {
        enable(cap, target);
        float adjustedPercent = percentCost - ((float) (9 - getSequence()) / 9 * percentDelta);
        if(target.level().isClientSide()) {
            return cap.getSpirituality() > cap.getMaxSpirituality() * adjustedPercent;
        }
        if(cap.getSpirituality() > cap.getMaxSpirituality()*adjustedPercent){
            if(target instanceof Player player){
                ItemStack result = new ItemStack(ModItems.GOLDEN_DROP.get());
                MysticismHelper.updateOrApplyMysticismTag(result, adjustedPercent * cap.getMaxSpirituality(), player);
                if(!player.addItem(result)){
                    player.drop(result, false, true);
                }
                cap.requestActiveSpiritualityCost(cap.getMaxSpirituality()*adjustedPercent);
                return true;
            }
        }

        return false;
    }

    @Override
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void activate(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void deactivate(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }
}
