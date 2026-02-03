package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.pathways.WheelOfFortunePathway;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific.AppraisalDataMessage;
import net.dinomine.potioneer.util.ParticleMaker;
import net.dinomine.potioneer.util.misc.MysticismHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class EntityAppraisalAbility extends Ability {

    public EntityAppraisalAbility(int sequence){
//        this.info = new AbilityInfo(5, 104, "Luck Check", sequence, 0, getMaxCooldown(), "luck_check" + (sequence > 7 ? "1" : "2"));
        super(sequence);
        this.isActive = true;
        this.isPassive = false;
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "target_appraisal_" + (sequenceLevel > 7 ? "1" : "2");
    }

    private LivingEntity getTarget(LivingEntity caster){
        LivingEntity appraisalTarget = AbilityFunctionHelper.getTargetEntity(caster, 3).orElse(caster);
        if(getSequenceLevel() > 7) return appraisalTarget;
        ItemStack item = caster.getMainHandItem();
        Player itemTarget = MysticismHelper.getPlayerFromMysticalItem(item, (ServerLevel) caster.level(), 0);
        if(itemTarget != null){
            appraisalTarget = itemTarget;
        }
        return appraisalTarget;
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(cap.getSpirituality() < cost()) return false;
        cap.requestActiveSpiritualityCost(cost());
        if(target.level().isClientSide()) return true;
        LivingEntity statAppraisalTarget = getTarget(target);
        if(statAppraisalTarget.getId() != target.getId())
            cap.getCharacteristicManager().progressActing(WheelOfFortunePathway.APPRAISER_ACTING_APPRAISE, 8);
        statAppraisalTarget.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(targetCap -> {
            if(sequenceLevel > 6){
                target.sendSystemMessage(Component.translatable("ability.potioneer.target_appraisal",
                        statAppraisalTarget.getDisplayName(), Math.ceil(statAppraisalTarget.getHealth()), Math.ceil(statAppraisalTarget.getMaxHealth()),
                        Math.round(targetCap.getSpirituality()), Math.max(targetCap.getMaxSpirituality(), 100), Math.round(targetCap.getSanity()), targetCap.getMaxSanity()));
            } else {
                PacketHandler.sendMessageSTC(new AppraisalDataMessage(statAppraisalTarget, false), target);
            }
        });
        ParticleMaker.summonAOEParticles(target.level(), statAppraisalTarget.getEyePosition(), 8, statAppraisalTarget.getBbWidth(), ParticleMaker.Preset.AOE_GRAVITY);
        return true;
    }

    @Override
    protected boolean secondary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(cap.getSpirituality() < cost()) return false;
        cap.requestActiveSpiritualityCost(cost());
        if(target.level().isClientSide()) return true;

        LivingEntity luckAppraisalTarget = getTarget(target);
        if(luckAppraisalTarget.getId() != target.getId())
            cap.getCharacteristicManager().progressActing(WheelOfFortunePathway.APPRAISER_ACTING_APPRAISE, 8);
        Optional<LivingEntityBeyonderCapability> optCap = luckAppraisalTarget.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
        if(optCap.isEmpty()) return false;
        LivingEntityBeyonderCapability targetCap = optCap.get();
        PlayerLuckManager luckMng = targetCap.getLuckManager();

        if(getSequenceLevel() > 7){
            target.sendSystemMessage(Component.translatable("ability.potioneer.luck_appraisal", luckAppraisalTarget.getDisplayName(), luckMng.getLuck()));
        } else if(getSequenceLevel() == 7){
            target.sendSystemMessage(Component.translatable("ability.potioneer.luck_appraisal_2", luckAppraisalTarget.getDisplayName(),
                    luckMng.getMinPassiveLuck(),
                    luckMng.getLuck(),
                    luckMng.getMaxPassiveLuck()));
        } else {
            PacketHandler.sendMessageSTC(new AppraisalDataMessage(luckAppraisalTarget, true), target);
            if(luckMng.getCurrentEvent() != null){
                target.sendSystemMessage(Component.translatableWithFallback("ability.potioneer.luck_event_appraisal", "%s has a luck event coming: %s",
                        luckAppraisalTarget.getDisplayName(), luckMng.getCurrentEvent().getForecast()));
            }
        }
        return true;
    }
}
