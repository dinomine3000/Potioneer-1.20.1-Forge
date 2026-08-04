package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.AbilityOptions;
import net.dinomine.potioneer.beyonder.abilities.AbilityWithOptions;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.dinomine.potioneer.config.PotioneerAbilityConfig;
import net.dinomine.potioneer.entities.ModEntities;
import net.dinomine.potioneer.entities.custom.WindShearProjectile;
import net.dinomine.potioneer.util.misc.ModTags;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CalamityAbility extends AbilityWithOptions {

    private static final Supplier<Integer> RAIN_COST = PotioneerAbilityConfig.CALAMITY_COST_RAIN;
    private static final Supplier<Integer> RAIN_COOLDOWN = PotioneerAbilityConfig.CALAMITY_COOLDOWN_RAIN;
    private static final Supplier<Integer> THUNDER_COST = PotioneerAbilityConfig.CALAMITY_COST_THUNDER;
    private static final Supplier<Integer> THUNDER_COOLDOWN = PotioneerAbilityConfig.CALAMITY_COOLDOWN_THUNDER;
    private static final Supplier<Integer> LUCK_COST = PotioneerAbilityConfig.CALAMITY_COST_LUCK;
    private static final Supplier<Integer> LUCK_COOLDOWN = PotioneerAbilityConfig.CALAMITY_COOLDOWN_LUCK;
    private static final Supplier<Integer> LEAP_COST = PotioneerAbilityConfig.CALAMITY_COST_LEAP;
    private static final Supplier<Integer> LEAP_COOLDOWN = PotioneerAbilityConfig.CALAMITY_COOLDOWN_LEAP;
    private static final Supplier<Integer> ASTEROID_COST = PotioneerAbilityConfig.CALAMITY_COST_ASTEROID;
    private static final Supplier<Integer> ASTEROID_COOLDOWN = PotioneerAbilityConfig.CALAMITY_COOLDOWN_ASTEROID;
    private static final Supplier<Integer> WIND_COST = PotioneerAbilityConfig.CALAMITY_COST_WIND;
    private static final Supplier<Integer> WIND_COOLDOWN = PotioneerAbilityConfig.CALAMITY_COOLDOWN_WIND;

    public CalamityAbility(int sequenceLevel) {
        super(sequenceLevel);
        AbilityOptions options = new AbilityOptions()
                .addEmptyOption("thunder", Component.literal("Thunder Strike"))
                .addEmptyOption("luck", Component.literal("Bad Luck"))
                .addEmptyOption("leap", Component.literal("Air Leap"))
                .addEmptyOption("wind", Component.literal("Wind Shear"))
                .addEmptyOption("meteor", Component.literal("Meteor"))
                .addEmptyOption("rain", Component.literal("Rain"));
        setPrimaryOptions(options);
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "calamity";
    }

    @Override
    protected boolean primaryWithArgument(LivingEntityBeyonderCapability cap, LivingEntity target, String args) {
        if(args.equalsIgnoreCase("rain")) return doRain(cap, target);
        else if(args.equalsIgnoreCase("leap")) return doLeap(cap, target);
        else if(args.equalsIgnoreCase("luck")) return doLuck(cap, target);
        else if(args.equalsIgnoreCase("thunder")) return doThunder(cap, target);
        else if(args.equalsIgnoreCase("meteor")) return doMeteor(cap, target);
        else if(args.equalsIgnoreCase("wind")) return doWindShear(cap, target);
        return false;
    }

    private boolean doWindShear(LivingEntityBeyonderCapability cap, LivingEntity caster){
        if(cap.getSpirituality() < WIND_COST.get()) return false;
        if(caster.level().isClientSide()) return true;
        Vec3 lookVector = caster.getLookAngle();
        for(int i = 0; i < caster.getRandom().nextInt(5, 8); i++){
            WindShearProjectile projectile = new WindShearProjectile(ModEntities.WIND_SHEAR_PROJECTILE.get(), caster.level());
            projectile.setPos(caster.getEyePosition());
            projectile.setOwner(caster);
            projectile.shoot(lookVector.x, lookVector.y, lookVector.z, 3, 5);
            caster.level().addFreshEntity(projectile);
        }
        caster.level().playSound(null, caster.getOnPos(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1F, (float) caster.getRandom().triangle(1, 0.2));
        cap.requestActiveSpiritualityCost(WIND_COST.get());
        setNextCooldownAs(WIND_COOLDOWN.get());
        return true;
    }

    private boolean doMeteor(LivingEntityBeyonderCapability cap, LivingEntity caster){
        if(cap.getSpirituality() < ASTEROID_COST.get()) return false;
        if(caster.level().isClientSide()) return true;
        List<LivingEntity> hits = AbilityFunctionHelper.getNonAllyLivingEntitiesAround(caster, 8);
        hits.forEach(ent -> AbilityFunctionHelper.summonAsteroid(ent.getOnPos(), ent.level(), caster));

        cap.requestActiveSpiritualityCost(ASTEROID_COST.get());
        setNextCooldownAs(ASTEROID_COOLDOWN.get());
        return true;
    }

    private boolean doRain(LivingEntityBeyonderCapability cap, LivingEntity target){
        if(cap.getSpirituality() < RAIN_COST.get()) return false;
        if(target.level().isClientSide()) return true;
        ((ServerLevel) target.level()).setWeatherParameters(0, 20*60*(1 + 2*(7-getSequenceLevel())), true, false);
        setNextCooldownAs(RAIN_COOLDOWN.get());
        cap.requestActiveSpiritualityCost(RAIN_COST.get());
        return true;
    }
    private boolean doThunder(LivingEntityBeyonderCapability cap, LivingEntity target){
        if(cap.getSpirituality() < THUNDER_COST.get()) return false;
        if(target.level().isClientSide()) return true;
        ServerLevel level = (ServerLevel) target.level();
        boolean thundering = level.isThundering();
        int radius = thundering ? 128 : 32;
        boolean castFlag = false;
        ArrayList<LivingEntity> hits = AbilityFunctionHelper.getNonAllyLivingEntitiesAround(level, target, radius);
        for(LivingEntity entity: hits){
            if(entity != target){
                summonLightning(cap, entity.position(), level, thundering, target);
                castFlag = true;
            }
        }
        if(!castFlag) return false;
        cap.requestActiveSpiritualityCost(thundering ? THUNDER_COST.get() / 2f : THUNDER_COST.get());
        setNextCooldownAs(THUNDER_COOLDOWN.get());
        return true;
    }
    private boolean doLuck(LivingEntityBeyonderCapability cap, LivingEntity caster) {
        if (cap.getSpirituality() < LUCK_COST.get()) return false;
        ArrayList<LivingEntity> victims = AbilityFunctionHelper.getNonAllyLivingEntitiesAround(caster, caster.getAttributeValue(ForgeMod.ENTITY_REACH.get()) + 0.5d);
        if (victims.isEmpty()) return false;
        if (caster.level().isClientSide()) return false;
        victims.forEach(ent -> doBadLuckTo(cap, ent));
        cap.requestActiveSpiritualityCost(LUCK_COST.get());
        setNextCooldownAs(LUCK_COOLDOWN.get());
        return true;
    }
    private boolean doLeap(LivingEntityBeyonderCapability cap, LivingEntity target){
        if(cap.getSpirituality() < LEAP_COST.get()) return false;
        if(target.isInWater()) return false;
        Vec3 look = target.getLookAngle();
        float scalar = target.level().isRaining() || target.level().isThundering() ? 4f : 2f;
        double mult = 2 + scalar*(7-getSequenceLevel());
        AbilityFunctionHelper.pushEntity(target, look.multiply(mult, mult/2, mult));
        if(!target.level().isClientSide()){
            target.level().playSound(null, target, SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.PLAYERS, 1, 0.5f);
            cap.requestActiveSpiritualityCost(LEAP_COST.get());
            setNextCooldownAs(LEAP_COOLDOWN.get());
        }
        return true;
    }
    private boolean doAir(){
        return false;
    }

    private void doBadLuckTo(LivingEntityBeyonderCapability casterCap, LivingEntity target){
        target.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(targetCap -> {
            PlayerLuckManager targetLuck = targetCap.getLuckManager();
            PlayerLuckManager exchangeManager = casterCap.getLuckManager().getDiffManager(targetLuck);
            targetCap.getLuckManager().consumeLuck(exchangeManager.getRandomNumber(50, 250, true, target.getRandom()));
            targetCap.getLuckManager().castOrHurryEvent(target, targetCap);
        });
    }
    private void summonLightning(LivingEntityBeyonderCapability cap, Vec3 position, ServerLevel level, boolean thundering, LivingEntity caster){
        LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
        lightning.setPos(position);
        int damage;
        double actingPercent = cap.getCharacteristicManager().getActingPercentForSequence(10 + getSequenceLevel());
        if(thundering){
            damage = (int) (-8 + 5*(10-getSequenceLevel()) + 4*actingPercent);
        } else {
            damage = -2 + 2*(10-getSequenceLevel()) + (int)(3 * actingPercent);
        }
        if(caster instanceof ServerPlayer player)
            lightning.setCause(player);
        lightning.setDamage(damage);
        lightning.addTag(ModTags.PURIFYING_TAG);
        level.addFreshEntity(lightning);

    }
}
