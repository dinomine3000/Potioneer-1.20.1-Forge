package net.dinomine.potioneer.beyonder.abilities.mystery;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;
import java.util.function.Supplier;

public class StepUpAbility extends Ability {

    public StepUpAbility(int sequence){
        this.info = new AbilityInfo(57, 152, "Step Assist", 20 + sequence, 0, this.getCooldown());
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {
        activate(cap, target);
    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        flipEnable(cap, target);
        return true;
    }

    @Override
    public void passive(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {
        if(target instanceof Player player){
            player.getAttributes()
                    .addTransientAttributeModifiers(getStepModifier(getSequence()));
        }

    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
        if(target instanceof Player player){
            player.getAttributes()
                    .removeAttributeModifiers(getStepModifier(getSequence()));
        }
    }

    //Credit to the create mod
    private static Multimap<Attribute, AttributeModifier> getStepModifier(int sequence){
        AttributeModifier singleRangeAttributeModifier =
                new AttributeModifier(UUID.fromString("d42bbdf2-0d0d-458a-dddd-ac7633691f66"),
                        "Beyonder step modifier", 0.5f,

                        AttributeModifier.Operation.ADDITION);
        Supplier<Multimap<Attribute, AttributeModifier>> rangeModifier = Suppliers.memoize(() ->
                // Holding an ExtendoGrip
                ImmutableMultimap.of(ForgeMod.STEP_HEIGHT_ADDITION.get(), singleRangeAttributeModifier));
        return rangeModifier.get();
    }
}
