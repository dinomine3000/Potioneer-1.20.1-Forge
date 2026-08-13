package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.tyrant.IAreaOfJurisdiction;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class AoJViewerEffect extends BeyonderEffect {
    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {

    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        if(!target.level().isClientSide()) return;
        if(target.tickCount%20 == 0){
            List<BlockPos> centers = new ArrayList<>();
            List<Integer> sides = new ArrayList<>();
            for(Ability abl: cap.getAbilitiesManager().getAbilities()){
                if(abl instanceof IAreaOfJurisdiction aojAbl){
                    String dimensionLocation = target.level().dimension().location().toString();
                    centers.addAll(aojAbl.getCenters(dimensionLocation));
                    sides.addAll(aojAbl.getSides(dimensionLocation));
                }
            }
            if(!centers.isEmpty()) ParticleMaker.createAreaOfJurisdiction(target.level(), (int)(target.getY()), centers, sides);
        }
    }


    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {
    }
}
