package net.dinomine.potioneer.beyonder.abilities.mystery;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ReachAbility extends Ability {

    public ReachAbility(int sequence){
        this.info = new AbilityInfo(57, 104, "Extended reach", 20 + sequence, 0, getCooldown(), "reach");
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
                    .addTransientAttributeModifiers(getBlockReachModifier(getSequence()));
            player.getAttributes()
                    .addTransientAttributeModifiers(getEntityReachModifier(getSequence()));
        }
    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
        if(target instanceof Player player){
            player.getAttributes()
                    .removeAttributeModifiers(getBlockReachModifier(getSequence()));
            player.getAttributes()
                    .removeAttributeModifiers(getEntityReachModifier(getSequence()));
        }
    }

    //Credit to the create mod
    private static Multimap<Attribute, AttributeModifier> getBlockReachModifier(int sequence){
        AttributeModifier singleRangeAttributeModifier =
                new AttributeModifier(UUID.fromString("c42bbdf2-0d0d-458a-aaaa-ac7633691f66"),
                        "Beyonder range modifier", 3*(10-sequence),

                        AttributeModifier.Operation.ADDITION);
        Supplier<Multimap<Attribute, AttributeModifier>> rangeModifier = Suppliers.memoize(() ->
                // Holding an ExtendoGrip
                ImmutableMultimap.of(ForgeMod.BLOCK_REACH.get(), singleRangeAttributeModifier));
        return rangeModifier.get();
    }

    //Credit to the create mod
    private static Multimap<Attribute, AttributeModifier> getEntityReachModifier(int sequence){
        AttributeModifier singleRangeAttributeModifier =
                new AttributeModifier(UUID.fromString("d42bbdf2-0d0d-458a-aaaa-ac7633691f66"),
                        "Beyonder entity range modifier", 3*(10-sequence),

                        AttributeModifier.Operation.ADDITION);
        Supplier<Multimap<Attribute, AttributeModifier>> rangeModifier = Suppliers.memoize(() ->
                // Holding an ExtendoGrip
                ImmutableMultimap.of(ForgeMod.ENTITY_REACH.get(), singleRangeAttributeModifier));
        return rangeModifier.get();
    }

}
