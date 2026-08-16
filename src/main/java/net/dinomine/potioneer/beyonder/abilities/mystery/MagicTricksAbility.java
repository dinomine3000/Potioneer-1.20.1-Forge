package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.AbilityOptions;
import net.dinomine.potioneer.beyonder.abilities.AbilityWithOptions;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public class MagicTricksAbility extends AbilityWithOptions {
    public MagicTricksAbility(int sequenceLevel) {
        super(sequenceLevel);
        AbilityOptions pOptions = new AbilityOptions()
                .addEmptyOption("paper", Component.literal("Paper Daggers"))
                .addEmptyOption("water", Component.literal("Water Affinity"))
                .addEmptyOption("fire", Component.literal("Fire Blink"))
                .addEmptyOption("ignite", Component.literal("Ignition"))
                .addEmptyOption("freeze", Component.literal("Freezing"))
                .addEmptyOption("effect", Component.literal("Effect Transfer"));
        AbilityOptions sOptions = new AbilityOptions()
                .addEmptyOption("flash", Component.literal("Flash Bang"))
                .addEmptyOption("noises", Component.literal("Noises"))
                .addEmptyOption("friction", Component.literal("No Friction"))
                .addEmptyOption("bouncy", Component.literal("Bounce"))
                .addEmptyOption("shock", Component.literal("Shock"))
                .addEmptyOption("fog", Component.literal("Fog"));
        setPrimaryOptions(pOptions);
        setSecondaryOptions(sOptions);
    }

    @Override
    protected boolean primaryWithArgument(BeyonderCapability cap, LivingEntity target, String args) {
        return super.primaryWithArgument(cap, target, args);
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "tricks";
    }
}
