package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.rituals.spirits.Deity;
import net.dinomine.potioneer.rituals.spirits.defaultGods.WheelOfFortuneResponse;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class WheelOfFortunePathway extends BeyonderPathway {

    public WheelOfFortunePathway() {
        super("Wheel_of_Fortune", 0x808080, new int[]{2500, 1500, 1200, 900, 600, 450, 375, 250, 200, 100});
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
            case 8 -> new float[]{0, 0, 4, 0, 2};
            case 7 -> new float[]{4, 0, 6, 2, 2};
            case 6 -> new float[]{5, 0, 8, 2, 4};
            case 5 -> new float[]{6, 0, 8, 2, 4};
            default -> new float[]{0, 0, 0, 0, 0};
        };
    }

    @Override
    public int isRitualComplete(int sequenceLevel, Player player, Level pLevel) {
        if(sequenceLevel > 5) return 0;
        return 0;
    }

    @Override
    public void applyRitualEffects(Player player, int sequenceLevel) {}

    @Override
    public Component getRitualDescriptionForSequence(int sequenceLevel) {
        return Component.empty();
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
            case 6:
            case 7:
//                abilities.add(Abilities.MINER_BONE_MEAL.create(atSequenceLevel));
//                abilities.add(Abilities.PATIENCE.create(atSequenceLevel));
//                abilities.add(Abilities.DODGE_DAMAGE.create(atSequenceLevel));
//                abilities.add(Abilities.CALAMITY_INCREASE.create(atSequenceLevel));
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
            case 8:
                res.addAll(List.of(BeyonderEffects.WHEEL_TEMP_LUCK.getEffectId(), BeyonderEffects.WHEEL_INSTANT_LUCK.getEffectId()));
        }
        return res;
    }
}
