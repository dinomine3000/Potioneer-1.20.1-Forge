package net.dinomine.potioneer.beyonder.effects.tyrant;

import com.lowdragmc.photon.client.fx.EntityEffect;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.savedata.AllySystemSaveData;
import net.dinomine.potioneer.sound.ModSounds;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;

public class WaterJetEffect extends BeyonderEffect {
    private static final float MAGNITUDE = 0.1f;
    private static final float RANGE = 10f;
    public static final int DURATION = 20*5;

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(!target.level().isClientSide()) ParticleMaker.createWaterJet(target);
        target.playSound(ModSounds.WATER_JET.get(), 1, (float) target.getRandom().triangle(1, 0.2));
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        Vec3 look = target.getLookAngle();
        Vec3 pushAngle = look.normalize().scale(MAGNITUDE);
        AllySystemSaveData data = AllySystemSaveData.from((ServerLevel) target.level());
        AbilityFunctionHelper.getLivingEntitiesLooking(target, RANGE, 1, false).forEach(ent -> {
            if(data.areEntitiesAllies(target, ent)) return;
            ent.push(pushAngle.x, pushAngle.y, pushAngle.z);
            ent.hasImpulse = true;
            ent.hurtMarked = true;
        });
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }
}
