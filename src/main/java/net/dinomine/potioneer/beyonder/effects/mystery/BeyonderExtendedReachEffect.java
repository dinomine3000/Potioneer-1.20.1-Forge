package net.dinomine.potioneer.beyonder.effects.mystery;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;
import java.util.function.Supplier;

public class BeyonderExtendedReachEffect extends BeyonderEffect {
    private static UUID entityId = UUID.fromString("d7f8685b-fda0-4c4f-862d-088bad3a8983");
    private static UUID blockId = UUID.fromString("a513a95f-5433-49ae-a928-e500cd0e8a84");
    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target instanceof Player player){
            player.getAttributes()
                    .addTransientAttributeModifiers(getBlockReachModifier(sequenceLevel));
            player.getAttributes()
                    .addTransientAttributeModifiers(getEntityReachModifier(sequenceLevel));
        }
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target instanceof Player player){
            player.getAttributes()
                    .removeAttributeModifiers(getBlockReachModifier(sequenceLevel));
            player.getAttributes()
                    .removeAttributeModifiers(getEntityReachModifier(sequenceLevel));
        }
    }


    //Credit to the create mod
    private static Multimap<Attribute, AttributeModifier> getBlockReachModifier(int sequence){
        AttributeModifier singleRangeAttributeModifier =
                new AttributeModifier(blockId,
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
                new AttributeModifier(entityId,
                        "Beyonder entity range modifier", 3*(10-sequence),

                        AttributeModifier.Operation.ADDITION);
        Supplier<Multimap<Attribute, AttributeModifier>> rangeModifier = Suppliers.memoize(() ->
                // Holding an ExtendoGrip
                ImmutableMultimap.of(ForgeMod.ENTITY_REACH.get(), singleRangeAttributeModifier));
        return rangeModifier.get();
    }
}
