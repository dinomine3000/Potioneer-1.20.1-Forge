package net.dinomine.potioneer.beyonder.abilities.tyrant;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;
import java.util.function.Supplier;

public class WaterAffinityAbility extends Ability {

    public WaterAffinityAbility(int sequence){
        this.info = new AbilityInfo(31, 32, "Water Affinity", 10 + sequence, sequence < 8 ? 40 : 20, this.getCooldown(), "water_affinity_" + (sequence < 8 ? "2" : "1"));
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {
        activate(cap, target);
    }

    @Override
    public String toString() {
        return "water";
    }

    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        flipEnable(cap, target);
        return true;
    }

    @Override
    public void passive(EntityBeyonderManager cap, LivingEntity target) {
        if(isEnabled(cap.getAbilitiesManager())){
            if(!cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.TYRANT_WATER_AFFINITY, getSequence())){
                cap.getEffectsManager().addEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.TYRANT_WATER_AFFINITY,
                        getSequence(), info.cost(), -1, true), cap, target);
            }
            if(cap.getSpirituality() < 1) flipEnable(cap, target);
        }
    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {
        if(target instanceof Player player){
            player.getAttributes()
                    .addTransientAttributeModifiers(getEntitySwimSpeedModifier(getSequence()));
        }
    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.TYRANT_WATER_AFFINITY, getSequence())){
            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.TYRANT_WATER_AFFINITY, getSequence(), cap, target);
        }
        if(target instanceof Player player){
            player.getAttributes()
                    .removeAttributeModifiers(getEntitySwimSpeedModifier(getSequence()));
        }
    }

    //Credit to the create mod
    private static Multimap<Attribute, AttributeModifier> getEntitySwimSpeedModifier(int sequence){
        AttributeModifier singleRangeAttributeModifier =
                new AttributeModifier(UUID.fromString("d42aaaa2-0d0d-458a-aaaa-ac7633691f66"),
                        "Beyonder swim speed modifier", (sequence-8.7-8.5)*(sequence-3.6-8.5)*0.08,

                        AttributeModifier.Operation.MULTIPLY_BASE);
        Supplier<Multimap<Attribute, AttributeModifier>> swimSpeedModifier = Suppliers.memoize(() ->
                // Holding an ExtendoGrip
                ImmutableMultimap.of(ForgeMod.SWIM_SPEED.get(), singleRangeAttributeModifier));
        return swimSpeedModifier.get();
    }
}
