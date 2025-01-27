package net.dinomine.potioneer.beyonder.abilities.redpriest;

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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RepairItemRecipe;

import java.util.UUID;
import java.util.function.Supplier;

public class StatBonusAbility extends Ability {

    public StatBonusAbility(int sequence){
        this.info = new AbilityInfo(83, 32, "Stat Bonus", 30 + sequence, 0, this.getCooldown());
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
            player.getAttributes().addTransientAttributeModifiers(getModifier(getSequence()));
        }
    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
        if(target instanceof Player player){
            player.getAttributes().removeAttributeModifiers(getModifier(getSequence()));
        }
    }

    private static Multimap<Attribute, AttributeModifier> getModifier(int sequence){

        AttributeModifier healthMod =
                new AttributeModifier(UUID.fromString("c42bbdf2-9d9d-458a-adaf-ac2633691f66"),
                        "Beyonder range modifier", 5*(10-sequence),
                        AttributeModifier.Operation.ADDITION);
        Supplier<Multimap<Attribute, AttributeModifier>> hpMod = Suppliers.memoize(() ->
                // Holding an ExtendoGrip
                ImmutableMultimap.of(Attributes.MAX_HEALTH, healthMod));
        return hpMod.get();
    }
}
