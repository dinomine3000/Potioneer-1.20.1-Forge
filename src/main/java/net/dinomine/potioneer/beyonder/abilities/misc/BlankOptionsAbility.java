package net.dinomine.potioneer.beyonder.abilities.misc;

import net.dinomine.potioneer.beyonder.abilities.AbilityOptions;
import net.dinomine.potioneer.beyonder.abilities.AbilityWithOptions;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public class BlankOptionsAbility extends AbilityWithOptions {
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
                .addOption("big fireball")
                .addOption("amount", new AbilityOptions()
                        .addOption("small fireball")
                        .addOption("raven"))
        );
    }

    @Override
    protected boolean primaryWithArgument(LivingEntityBeyonderCapability cap, LivingEntity target, String option) {
        //args now contains, guaranteed, an option defined above
        return true;
    }

    @Override
    protected boolean secondary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        //do something quirky. no secondary arguments defined, so this is always called on cast.
        return true;
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "";
    }
}
