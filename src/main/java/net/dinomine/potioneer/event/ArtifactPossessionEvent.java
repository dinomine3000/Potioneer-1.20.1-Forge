package net.dinomine.potioneer.event;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.util.misc.ArtifactHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

public abstract class ArtifactPossessionEvent extends LivingEvent {
    private final ArtifactHolder artifact;

    public ArtifactPossessionEvent(ArtifactHolder artifact, LivingEntity caster){
        super(caster);
        this.artifact = artifact;
    }

    public ArtifactHolder getArtifact(){
        return artifact;
    }

    public static class Gained extends ArtifactPossessionEvent {

        public Gained(ArtifactHolder abl, LivingEntity caster) {
            super(abl, caster);
        }
    }

    public static class Lost extends ArtifactPossessionEvent {

        public Lost(ArtifactHolder abl, LivingEntity caster) {
            super(abl, caster);
        }
    }

}
