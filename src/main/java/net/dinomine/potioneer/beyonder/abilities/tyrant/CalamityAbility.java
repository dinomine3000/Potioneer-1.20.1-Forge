package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.AbilityWithOptions;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.util.misc.ModTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

public class CalamityAbility extends AbilityWithOptions {

    private static final int RAIN_COST = 50;
    private static final int THUNDER_COST = 75;
    public CalamityAbility(int sequenceLevel) {
        super(sequenceLevel);
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "calamity";
    }

    @Override
    protected boolean primaryWithArgument(LivingEntityBeyonderCapability cap, LivingEntity target, String args) {
        if(args.equalsIgnoreCase("rain")) {
        }
        else if(args.equalsIgnoreCase("leap")){
            Vec3 look = target.getLookAngle();
            double mult = 2 + 1.2*(8-getSequenceLevel());
            target.addDeltaMovement(look.multiply(mult, mult/2, mult));
            if(!target.level().isClientSide()){
                target.level().playSound(null, target, SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.PLAYERS, 1, 0.5f);
                cap.requestActiveSpiritualityCost(cost());
            }
        }
        else if(args.equalsIgnoreCase("luck")){

        } else if(args.equalsIgnoreCase("thunder")){
        }
        return false;
    }

    private boolean doRain(LivingEntityBeyonderCapability cap, LivingEntity target){
        if(cap.getSpirituality() < RAIN_COST) return false;
        if(target.level().isClientSide()) return true;
        ((ServerLevel) target.level()).setWeatherParameters(0, 20*60*(1 + 2*(7-getSequenceLevel())), true, false);
        setNextCooldownAs(20*5);
        cap.requestActiveSpiritualityCost(RAIN_COST);
        return true;
    }
    private boolean doThunder(LivingEntityBeyonderCapability cap, LivingEntity target){
        if(cap.getSpirituality() < THUNDER_COST) return false;
        if(target.level().isClientSide()) return true;
        ServerLevel level = (ServerLevel) target.level();
        boolean thundering = level.isThundering();
        int radius = thundering ? 128 : 32;
        ArrayList<LivingEntity> hits = AbilityFunctionHelper.getLivingEntitiesAround(target, radius);
        for(LivingEntity entity: hits){
            if(entity != target){
                summonLightning(cap, entity.position(), level, thundering);
                return true;
            }
        }
        HitResult hit = target.pick(radius, 0, false);
        if(hit instanceof BlockHitResult blockHit && !level.getBlockState(blockHit.getBlockPos()).is(Blocks.AIR)){
            summonLightning(cap, blockHit.getLocation(), level, thundering);
            return true;
        }
        return false;
    }
    private boolean doLuck(){return false;}
    private boolean doLeap(){return false;}
    private boolean doAir(){return false;}

    private void summonLightning(LivingEntityBeyonderCapability cap, Vec3 position, ServerLevel level, boolean thundering){
        LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
        lightning.setPos(position);
        int damage;
        double actingPercent = cap.getCharacteristicManager().getActingPercentForSequence(10 + getSequenceLevel());
        if(thundering){
            damage = (int) (-8 + 5*(10-getSequenceLevel()) + 4*actingPercent);
        } else {
            damage = -2 + 2*(10-getSequenceLevel()) + (int)(3 * actingPercent);
        }
        lightning.setDamage(damage);
        lightning.addTag(ModTags.PURIFYING_TAG);
        level.addFreshEntity(lightning);
        cap.requestActiveSpiritualityCost(thundering ? cost() / 2f : cost());

    }
}
