package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.minecraft.resources.ResourceLocation;

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

    public String getPathwayName(boolean capitalize){
        return capitalize ? this.name : this.name.toLowerCase();
    }

    public abstract int getX();

    public abstract int getY();

    public abstract int getAbilityX();


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

    public abstract List<Ability> getAbilities(int sequenceLevel);


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

    public abstract float[] getStatsFor(int sequence);
}
