package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.effects.GeneralAreaEffectMessage;
import net.dinomine.potioneer.rituals.spirits.Deity;
import net.dinomine.potioneer.rituals.spirits.defaultGods.WheelOfFortuneResponse;
import net.dinomine.potioneer.sound.ModSounds;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WheelOfFortunePathway extends BeyonderPathway {
    public static final double MINER_ACTING_INC = 1/(64*3f);
    public static final double APPRAISER_ACTING_APPRAISE = 1/128d;
    public static final double APPRAISER_ACTING_MINING = 1/128d;
    public static final double GAMBLER_ACTING_SUC = 1/20d;
    public static final double GAMBLER_ACTING_FAIL = 1/50d;
    public static final double GAMBLER_ACTING_COOLDOWN = 1/256d;
    public static final double LUCK_ACTING_INC = 1/256d;
    public static final double LUCK_ACTING_EVENT = 1/128d;
        public static final double MISFORTUNE_ACTING_INC = 1/100d;

    public WheelOfFortunePathway() {
        super("Wheel_of_Fortune", 0x808080, new int[]{2500, 1500, 1200, 900, 600, 500, 400, 250, 200, 100});
    }

    @Override
    public int getX(){
        return 0;
    }

    @Override
    public int getY(){
        return 0;
    }

    @Override
    public int getAbilityX(){return 5;}

    @Override
    public float[] getStatsFor(int sequence){
        return switch (sequence%10){
            case 9 -> new float[]{0, 0, 2, 0, 0};
            case 8 -> new float[]{2, 0, 2, 0, 2};
            case 7 -> new float[]{4, 1, 4, 2, 2};
            case 6 -> new float[]{6, 1, 4, 2, 4};
            case 5 -> new float[]{10, 3, 8, 3, 6};
            default -> new float[]{0, 0, 0, 0, 0};
        };
    }

    @Override
    public int isRitualComplete(int sequenceLevel, Player player, Level pLevel) {
        if(sequenceLevel > 5) return 0;
        int diff = 0;

        Optional<LivingEntityBeyonderCapability> optCap = player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
        if(optCap.isEmpty()) return 0;
        LivingEntityBeyonderCapability cap = optCap.get();
        switch (sequenceLevel){
            case 5:
                int luck = cap.getLuckManager().getLuck();
                if(luck > -50) return 5;
                if(luck > -100) return 4;
                if(luck > -200) return 3;
                return 0;
        }
        return 0;
    }

    @Override
    public void applyRitualEffects(Player player, int sequenceLevel) {
        switch (sequenceLevel){
            case 5:
                List<LivingEntity> entitiesAround = AbilityFunctionHelper.getLivingEntitiesAround(player, 16);
                for(LivingEntity ent: entitiesAround){
                    ent.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                        if(ent.is(player) && cap.getLuckManager().getLuck() < -50) cap.getLuckManager().setLuck(-cap.getLuckManager().getLuck());
                        else cap.getLuckManager().setLuck(cap.getLuckManager().getLuck() * 10);
                        cap.getLuckManager().instantlyCastEvent(ent);
                    });
                }
                PacketHandler.sendMessageToClientsAround(player, 32, new GeneralAreaEffectMessage(ParticleMaker.Preset.AOE_END_ROD, player.getEyePosition().toVector3f(), 16));
//                ParticleMaker.particleExplosionRandom(player.level(), 16, player.getX(), player.getY(), player.getZ());
                player.level().playSound(null, player.getOnPos(), ModSounds.LUCK.get(), SoundSource.PLAYERS, 1, 1);
                break;
        }
    }

    @Override
    public Component getRitualDescriptionForSequence(int sequenceLevel) {
        if(sequenceLevel > 5) return Component.empty();
        return switch (sequenceLevel){
            case 5 -> Component.translatable("ritual.potioneer.source_of_misfortune");
            default -> Component.translatable("ritual.potioneer.source_of_misfortune");
        };
    }

    @Override
    public List<Ability> getAbilities(int sequence){
        return getAbilities(sequence%10, sequence%10);
    }

    @Override
    public List<Ability> getAbilities(int ofSequenceLevel, int atSequenceLevel) {
        ArrayList<Ability> abilities = new ArrayList<>();
        switch(ofSequenceLevel%10){
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                abilities.add(Abilities.PHASING.create(atSequenceLevel));
                abilities.add(Abilities.BET.create(atSequenceLevel));
                abilities.add(Abilities.RECORD_DAMAGE.create(atSequenceLevel));
                abilities.add(Abilities.MISFORTUNE.create(atSequenceLevel));
            case 6:
                abilities.add(Abilities.FATE.create(atSequenceLevel));
                abilities.add(Abilities.LUCK.create(atSequenceLevel));
                abilities.add(Abilities.HALF_COOLDOWN.create(atSequenceLevel));
                abilities.add(Abilities.WHEEL_DIVINATION.create(atSequenceLevel));
            case 7:
                abilities.add(Abilities.PATIENCE.create(atSequenceLevel));
                abilities.add(Abilities.VELOCITY.create(atSequenceLevel));
                abilities.add(Abilities.MINER_BONE_MEAL.create(atSequenceLevel));
                abilities.add(Abilities.FORCE_COOLDOWN_ABILITY.create(atSequenceLevel));
                abilities.add(Abilities.GAMBLING.create(atSequenceLevel));
            case 8:
                abilities.add(Abilities.WHEEL_KNOWLEDGE.create(atSequenceLevel));
                abilities.add(Abilities.TARGET_APPRAISAL.create(atSequenceLevel));
                abilities.add(Abilities.BLOCK_APPRAISAL.create(atSequenceLevel));
                abilities.add(Abilities.APPRAISAL.create(atSequenceLevel));
                abilities.add(Abilities.FORTUNE_ABILITY.create(atSequenceLevel));
                abilities.add(Abilities.SILK_TOUCH_ABILITY.create(atSequenceLevel));
                abilities.add(Abilities.CALAMITY_INCREASE.create(atSequenceLevel));
            case 9:
                abilities.add(Abilities.MINER_LIGHT.create(atSequenceLevel));
                abilities.add(Abilities.VOID_VISION.create(atSequenceLevel));
                abilities.add(Abilities.CONJURE_PICKAXE.create(atSequenceLevel));
                abilities.add(Abilities.MINING_SPEED.create(atSequenceLevel));
                abilities.add(Abilities.ZERO_DAMAGE.create(atSequenceLevel));
        }

        return abilities;

    }
    public static final int UNLUCK = 0;

    public static void playSound(Level level, BlockPos pos, int sound){
        switch (sound){
            case 0 -> level.playSound(null, pos, ModSounds.UNLUCK.get(), SoundSource.PLAYERS, 0.8f, (float) level.getRandom().triangle(1f, 0.2f));
        }
    }

    @Override
    public String getSequenceNameFromId(int seq, boolean show){
        return show ? getSequenceNameFromId(seq).replace("_", " ") : getSequenceNameFromId(seq).toLowerCase();
    }

    private String getSequenceNameFromId(int seq){
        return switch (seq%10) {
            case 9 -> "Miner";
            case 8 -> "Appraiser";
            case 7 -> "Nimble_Gambler";
            case 6 -> "Lucky_One";
            case 5 -> "Source_of_Misfortune";
            case 4 -> "Commander_of_Fate";
            default -> "";
        };
    }

    @Override
    public int getSequenceColorFromLevel(int seq){
        return switch (seq%10) {
            case 9 -> 10724259;
            case 8 -> 16383885;
            case 7 -> 14989311;
            default -> 0;
        };
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public Deity getDefaultDeity() {
        return new WheelOfFortuneResponse();
    }

    @Override
    public List<String> canCraftEffectCharms(int sequenceLevel) {
        List<String> res = new ArrayList<>();
        switch(sequenceLevel){
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                res.addAll(List.of(BeyonderEffects.WHEEL_BAD_LUCK.getEffectId(), BeyonderEffects.WHEEL_INSTANT_BAD_LUCK.getEffectId(), BeyonderEffects.WHEEL_CHAOTIC_LUCK.getEffectId()));
            case 6:
                res.addAll(List.of(BeyonderEffects.WHEEL_LUCK_EFFECT.getEffectId(), BeyonderEffects.WHEEL_FATE.getEffectId()));
            case 7:
                res.addAll(List.of(BeyonderEffects.WHEEL_COOLDOWN.getEffectId(), BeyonderEffects.WHEEL_PATIENCE.getEffectId(), BeyonderEffects.WHEEL_GAMBLING.getEffectId(), BeyonderEffects.WHEEL_VELOCITY.getEffectId()));
            case 8:
                res.addAll(List.of(BeyonderEffects.WHEEL_LUCK.getEffectId(), BeyonderEffects.WHEEL_INSTANT_LUCK.getEffectId()));
        }
        return res;
    }
}
