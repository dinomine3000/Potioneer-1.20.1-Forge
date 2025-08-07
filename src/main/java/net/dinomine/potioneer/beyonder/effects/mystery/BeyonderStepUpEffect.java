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

public class BeyonderStepUpEffect extends BeyonderEffect {
    public BeyonderStepUpEffect(int level, float cost, int time, boolean active, BeyonderEffects.EFFECT id){
        super(level, cost, time, active, id);
        this.name = "Step Up";
    }


    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target instanceof Player player){
            player.getAttributes()
                    .addTransientAttributeModifiers(getStepModifier(sequenceLevel));
        }
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target instanceof Player player){
            player.getAttributes()
                    .removeAttributeModifiers(getStepModifier(sequenceLevel));
        }
    }


    //Credit to the create mod
    private static Multimap<Attribute, AttributeModifier> getStepModifier(int sequence){
        AttributeModifier singleRangeAttributeModifier =
                new AttributeModifier(UUID.fromString("e3461150-2bdc-40b9-b526-d16e2104e6c7"),
                        "Beyonder step modifier", 0.5f,

                        AttributeModifier.Operation.ADDITION);
        Supplier<Multimap<Attribute, AttributeModifier>> rangeModifier = Suppliers.memoize(() ->
                // Holding an ExtendoGrip
                ImmutableMultimap.of(ForgeMod.STEP_HEIGHT_ADDITION.get(), singleRangeAttributeModifier));
        return rangeModifier.get();
    }
}
