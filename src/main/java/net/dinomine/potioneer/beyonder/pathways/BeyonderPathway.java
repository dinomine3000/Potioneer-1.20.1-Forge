package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.rituals.spirits.Deity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

public abstract class BeyonderPathway {
    /**
     * Should be like "Duke_of_Entropy" that way we can show it as is (remove the _) or translate it (lowercase it to get the translation key)
     */
    protected String name;
    protected int color;
    public int[] maxSpirituality;

    public BeyonderPathway(String name, int color, int[] maxSpirituality){
        this.name = name;
        this.color = color;
        this.maxSpirituality = maxSpirituality;
    }

    public int getMaxSpirituality(int seqLevel){
        return maxSpirituality[seqLevel%10];
    }

    public int getId(){
        return Pathways.getPathwayIdFromPathway(this);
    }

    public int getColor(){
        return this.color;
    }

    public Component getPathwayName(){
        return Component.translatable("potioneer.beyonder.pathway." + getPathwayName(false));
    }

    public String getPathwayName(boolean capitalize){
        return capitalize ? this.name : this.name.toLowerCase();
    }

    public abstract int getX();

    public abstract int getY();

    public abstract int getAbilityX();

    public Deity getDefaultDeity(){return null;}

    public abstract int getSequenceColorFromLevel(int sequenceLevel);
//    {
//        int seq = id%10;
//        int color;
//        int pathway = id == -1 ? -1 : (Math.floorDiv(id, 10));
//        color = switch (pathway) {
//            case 0 -> WheelOfFortunePathway.getSequenceColor(seq);
//            case 1 -> TyrantPathway.getSequenceColor(seq);
//            case 2 -> MysteryPathway.getSequenceColor(seq);
//            case 3 -> RedPriestPathway.getSequenceColor(seq);
//            case 4 -> ParagonPathway.getSequenceColor(seq);
//            default -> 0;
//        };
//        if(color == 0) color = 16742143;
//        return color;
//    }

    /**
     * return all abilities given at this level or lower, with their sequence levels being the highest.
     * @param sequenceLevel, like 5
     * @return a list of all abilities given from sequences 9-5, all at level 5
     */
    public abstract List<Ability> getAbilities(int sequenceLevel);

    /**
     * return all abilities given at sequenceLevel level or lower, with their sequence levels being atSequenceLevel.
     * @param sequenceLevel, like 5
     * @param atSequenceLevel, like 3
     * @return a list of all abilities given from sequences 9-5, all at level 3
     */
    public abstract List<Ability> getAbilities(int sequenceLevel, int atSequenceLevel);

    public Component getSequenceComponentFromId(int sequenceLevel){
        return Component.translatable("potioneer.beyonder.sequence." + getSequenceNameFromId(sequenceLevel, false));
    }

    public abstract String getSequenceNameFromId(int sequenceLevel, boolean show);
//    {
//        int seq = id % 10;
//        return switch(Math.floorDiv(id, 10)){
//            case 0 -> WheelOfFortunePathway.getSequenceName(seq, show);
//            case 1 -> TyrantPathway.getSequenceName(seq, show);
//            case 2 -> MysteryPathway.getSequenceName(seq, show);
//            case 3 -> RedPriestPathway.getSequenceName(seq, show);
//            case 4 -> ParagonPathway.getSequenceName(seq, show);
//            case 5 -> DevPathway.getSequenceName(seq, show);
//            default -> show ? "None" : "none";
//        };
//    }

    /**
     * gets the stats for the specific sequence level
     * @param sequence
     * @return {hp, dmg, armor, toughness, knocknack resistance}
     */
    public abstract float[] getStatsFor(int sequence);

    public int getIconX() {
        return 0;
    }

    public int getIconY() {
        return 0;
    }

    /**
     * method to evaluate if the player completed the respective ritual
     * @param sequenceLevel
     * @param player
     * @param pLevel
     * @return the added difficulty. 0 if the ritual was a complete success. Should be at least 5 if it failed completely.
     */
    public abstract int isRitualComplete(int sequenceLevel, Player player, Level pLevel);

    /**
     * function to apply ritual effects to the player once they finish advancing.
     * @param player
     * @param sequenceLevel
     */
    public abstract void applyRitualEffects(Player player, int sequenceLevel);

    public abstract Component getRitualDescriptionForSequence(int sequenceLevel);

    public List<String> canCraftEffectCharms(int sequenceLevel){return List.of();}
}
