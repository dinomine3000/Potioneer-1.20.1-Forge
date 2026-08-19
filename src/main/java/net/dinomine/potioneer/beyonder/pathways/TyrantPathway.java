package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFactory;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.tyrant.ContractedEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.BeyonderStats;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.config.PotioneerAbilityConfig;
import net.dinomine.potioneer.rituals.spirits.Deity;
import net.dinomine.potioneer.rituals.spirits.defaultGods.TyrantResponse;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.*;

public class TyrantPathway extends BeyonderPathway {
    public static final double SWIMMER_ACTING = 1d/(20*20*60);
    public static final double WATER_MAGE_ACTING_SPELLS = 1/128d;
    public static final double WATER_MAGE_ACTING_DIVINATION = 1/64d;
    public static final double ENFORCER_ACTING_DAMAGE = 1/256d;
    public static final double ENFORCER_ACTING_ARREST = 1/128d;
    public static final double ENFORCER_ACTING_MIST = 1d/(20*60*30);
    public static final double MAGISTRATE_ACTING_BRIBE = 1/256d;
    public static final double MAGISTRATE_ACTING_BERSERK = 1/(20*60d*15);
    public static final double MAGISTRATE_ACTING_CONTRACT = 1/256d;
    public static final double MAGISTRATE_ACTING_CALAMITY = 1/256d;
    public static final double TRIBUNAL_ACTING_PYLON_RULE = 1/256d;
    public static final double TRIBUNAL_ACTING_PYLON_LAW = 1/(20*60*120d);
    public static final double TRIBUNAL_ACTING_PROHIBITION = 1/256d;

    public TyrantPathway(){
        super("Tyrant", 0x404080, new int[]{3400, 2500, 1800, 1300, 1000, 700, 425, 300, 140, 100});
    }

    @Override
    public Deity getDefaultDeity() {
        return new TyrantResponse();
    }

    @Override
    public int getX(){
        return 64;
    }

    @Override
    public int getY(){
        return 0;
    }

    @Override
    public int getAbilityX() {
        return 31;
    }

    public int getIconX() {
        return 64;
    }

    @Override
    public int isRitualComplete(int sequenceLevel, Player player, Level pLevel) {
        if(sequenceLevel > 5) return 0;
        int diff = 0;

        Optional<BeyonderCapability> optCap = player.getCapability(CapProvider.BEYONDER_STATS).resolve();
        if(optCap.isEmpty()) return 0;
        BeyonderCapability cap = optCap.get();
        switch (sequenceLevel){
            case 5:
                diff = 5;
                List<LivingEntity> hits = AbilityFunctionHelper.getLivingEntitiesAround(player, 16);
                for(LivingEntity ent: hits){
                    ContractedEffect eff = AbilityFunctionHelper.getEffectOnTarget(BeyonderEffects.TYRANT_CONTRACT.getEffectId(), ent);
                    if(eff == null) continue;
                    if(player.getUUID().equals(eff.getCasterId()) && eff.getTime() >= 20*60*60) diff--;
                }
                return Math.max(diff, 0);
        }
        return 0;
    }

    @Override
    public void applyRitualEffects(Player player, int sequenceLevel) {}

    @Override
    public Component getRitualDescriptionForSequence(int sequenceLevel) {
        if(sequenceLevel > 5) return Component.empty();
        return switch (sequenceLevel){
            case 5 -> PotioneerAbilityConfig.TYRANT_CAN_DO_CONTRACTS_TO_NON_ALLIES.get() ?
                    Component.translatable("ritual.potioneer.tribunal")
                    : Component.translatable("ritual.potioneer.tribunal_players");
            default -> Component.translatable("ritual.potioneer.source_of_misfortune");
        };
    }


    /*@Override
    public float[] getStatsFor(int sequenceLevel){
        return switch (sequenceLevel%10){
            case 9 -> new float[]{8, 0, 0, 1};
            case 8 -> new float[]{8, 0, 0, 2};
            case 7 -> new float[]{12, 2, 0, 5};
            case 6 -> new float[]{15, 2, 0, 5};
            case 5 -> new float[]{20, 3, 0, 7};
            default -> new float[]{0, 0, 0, 0};
        };
    }*/

    @Override
    public Map<BeyonderStats.StatType, Float> getStatsFor(int sequence) {
        Map<BeyonderStats.StatType, Float> stats = new EnumMap<>(BeyonderStats.StatType.class);

        switch (sequence % 10) {
            case 9 -> setStats(stats, 4, 1, 1, 4f);
            case 8 -> setStats(stats, 6, 1, 1, 3.5f);
            case 7 -> setStats(stats, 16, 4, 3, 3f);
            case 6 -> setStats(stats, 22, 6, 4, 2.5f);
            case 5 -> setStats(stats, 28, 10, 9, 2f);
            default -> setStats(stats, 35, 13, 11, 1.5f);
        }
        return stats;
    }


    @Override
    public List<AbilityFactory> getAbilities(int ofSequenceLevel){
        ArrayList<AbilityFactory> abilities = new ArrayList<>();
        switch(ofSequenceLevel%10){
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                abilities.add(Abilities.RULE_PYLON.get());
                abilities.add(Abilities.PROHIBITION.get());
                abilities.add(Abilities.AMPLIFICATION.get());
            case 6:
                abilities.add(Abilities.BRIBE.get());
                abilities.add(Abilities.EXILE.get());
                abilities.add(Abilities.CONTRACT.get());
                abilities.add(Abilities.TYRANT_CALAMITY.get());
                abilities.add(Abilities.BERSERK_RAGE.get());
                abilities.add(Abilities.ANCHOR_BLINKING.get());
            case 7:
                abilities.add(Abilities.AOJ.get());
                abilities.add(Abilities.TYRANT_AURA.get());
                abilities.add(Abilities.ARREST.get());
                abilities.add(Abilities.MIST.get());
                abilities.add(Abilities.MIST_BLINKING.get());
                abilities.add(Abilities.SENSE_OF_ORDER.get());
            case 8:
                abilities.add(Abilities.TYRANT_DIVINATION.get());
                abilities.add(Abilities.TYRANT_WATER_SPELLS.get());
            case 9:
                abilities.add(Abilities.WATER_AFFINITY.get());
                abilities.add(Abilities.WATER_SCALES.get());
                abilities.add(Abilities.OCEAN_ORDER.get());
        }
        Collections.reverse(abilities);
        return abilities;
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public String getSequenceNameFromId(int seq, boolean show){
        return show ? getSequenceName(seq).replace("_", " ") : getSequenceName(seq).toLowerCase();
    }

    private String getSequenceName(int seq){
        return switch (seq%10) {
            case 9 -> "Swimmer";
            case 8 -> "Water_Mage";
            case 7 -> "Hydroborn_Enforcer";
            case 6 -> "Chaotic_Magistrate";
            case 5 -> "Tribunal";
            default -> "";
        };
    }

    @Override
    public int getSequenceColorFromLevel(int seq){
        return switch (seq%10) {
            case 9 -> 0x8AF6FF;
            case 8 -> 0x82A1FF;
            case 7 -> 0x4814FC;
            case 6 -> 0x910DD4;
            case 5 -> 0xA6AD05;
            default -> 0;
        };
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
            case 8:
                res.addAll(List.of(BeyonderEffects.TYRANT_WATER_AFFINITY.getEffectId(), BeyonderEffects.TYRANT_WATER_PRISON.getEffectId()));
        }
        return res;
    }
    @Override
    public AbilityFactory getCogitationAbility() {
        return Abilities.COGITATION_WOF.get();
    }
}
