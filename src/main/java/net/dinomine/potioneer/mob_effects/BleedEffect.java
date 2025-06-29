package net.dinomine.potioneer.mob_effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

public class BleedEffect extends MobEffect {
    protected BleedEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

}
