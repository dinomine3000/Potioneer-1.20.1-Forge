package net.dinomine.potioneer.util.misc;

import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancementDifficultyHelper {



    public static int calculateDifficultyClient(int pathwayId, int newSeq, int sanity, float actingProgress){
        //difference between the new sequence and current sequence
        //plus one more difficulty for every 25% sanity lost
        //plus 1 for each group of 8-6, 5-3 and 2-1 sequence levels
        //plus 1 or 2 for undigested potions (TODO)
        int levelDifference;
        if(pathwayId < -1){ //adds 5 points of difficulty for every level you skip
            levelDifference = 5*Math.max(9 - newSeq%10, 0);
        } else {
            levelDifference = 5*Math.max(pathwayId%10 - 1 - newSeq%10, 0);
        }
        int sanityDiff = Math.round(8f-sanity/12.5f); //from 0 to 8 more points depending on your sanity
        int groupDiff = 3-Math.floorDiv(newSeq%10, 3) + (newSeq%10 == 0 ? 2 : 0); //plus 1 for each group
        int actingDiff = (int) (2*(1- Mth.floor(actingProgress))); //up to 3 added points of difficulty for not digested potion
        //mind you, advancing without fully digesting a potion will lead to less maximum sanity.

        int diff = levelDifference + sanityDiff + groupDiff + actingDiff;
        if(pathwayId > -1){
            int level = newSeq%10;
            //if the target sequence is located between your current sequence and sequence 9,
            //aka, a previous sequence to your current one
            //add 4 points of difficulty
            //this is to prevent ppl from drinking previous potions without consequence
            if(level >= pathwayId%10) diff += 4;
        }
        // more points for demigod levels
        if(newSeq%10 < 5) diff += (int) Math.max((6 - newSeq%10)/1.5f, 0);
        return diff;
    }
}
