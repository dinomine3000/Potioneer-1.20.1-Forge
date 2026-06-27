package net.dinomine.potioneer.beyonder.abilities.misc;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.AbilityOptions;
import net.dinomine.potioneer.beyonder.abilities.AbilityWithOptions;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class BlankOptionsAbility extends AbilityWithOptions {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/ability_icon_atlas.png");
    /**
     * pass the sequence level or pathway-sequence id to define the abilities sequence level
     * abilities that depend on changing pathways like Cogitation, that exists for every pathway, need to process their own pathway-sequence id here.
     * I dont ask specifically for sequence level OR pathway id, but if you want to choose one, pass along the pathwaySequenceId.
     *
     * @param sequenceLevel
     */
    public BlankOptionsAbility(int sequenceLevel) {
        super(sequenceLevel);
        addPrimaryOptions(new AbilityOptions()
                .addOption("big fireball", Component.literal("Big Fireball"), TEXTURE_LOCATION, 5, 32, 16, 24)
                .addOption("test fireball", Component.literal("Test"), TEXTURE_LOCATION, 83, 80, 16, 24)
                .addOption("amount", new AbilityOptions()
                        .addOption("small fireball", Component.literal("Small Fireball"), TEXTURE_LOCATION, 83, 104, 16, 24)
                        .addOption("medium fireball", Component.literal("Medium Fireball"), TEXTURE_LOCATION, 83, 56, 16, 24)
                        .addOption("big fireball", Component.literal("Big Fireball"), TEXTURE_LOCATION, 83, 32, 16, 24)
                        .addOption("raven", Component.literal("Ravens"), TEXTURE_LOCATION, 109, 32, 16, 24),
                    Component.literal("Smaller Options"), TEXTURE_LOCATION, 57, 56, 16, 24)
        );
    }

    @Override
    protected boolean primaryWithArgument(LivingEntityBeyonderCapability cap, LivingEntity target, String option) {
        //args now contains, guaranteed, an option defined above
        System.out.println("Cast ability with option " + option);
        return true;
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "";
    }
}
