package net.dinomine.potioneer.beyonder.abilities.misc;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.AbilityOptions;
import net.dinomine.potioneer.beyonder.abilities.AbilityWithOptions;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class BlankOptionsAbility extends AbilityWithOptions {
    private static final int TEXTURE_WIDTH = 180, TEXTURE_HEIGHT = 632;
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
        addPrimaryOptions(new AbilityOptions(TEXTURE_LOCATION, TEXTURE_WIDTH, TEXTURE_HEIGHT)
                .addOption("big fireball", Component.literal("Big Fireball"), 5, 32, 16, 24)
                .addOption("test fireball", Component.literal("Test"), 83, 80, 16, 24)
                .addOption("amount", new AbilityOptions(TEXTURE_LOCATION, TEXTURE_WIDTH, TEXTURE_HEIGHT)
                        .addOption("small fireball", Component.literal("Small Fireball"), 83, 104, 16, 24)
                        .addOption("medium fireball", Component.literal("Medium Fireball"), 83, 56, 16, 24)
                        .addOption("big fireball", Component.literal("Big Fireball"), 83, 32, 16, 24)
                        .addOption("raven", Component.literal("Ravens"), 109, 32, 16, 24),
                    Component.literal("Smaller Options"), 57, 56, 16, 24)
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
