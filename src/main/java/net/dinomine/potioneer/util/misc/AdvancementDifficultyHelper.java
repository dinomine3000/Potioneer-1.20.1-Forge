package net.dinomine.potioneer.util.misc;

import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancementDifficultyHelper {



    public static int calculateDifficultyClient(int pathwaySequenceId, int newPathSeqId, int sanity, float actingProgress){
        //difference between the new sequence and current sequence
        //plus one more difficulty for every 25% sanity lost
        //plus 1 for each group of 8-6, 5-3 and 2-1 sequence levels
        //plus 1 or 2 for undigested potions (TODO)
        int levelDifference;
        if(pathwaySequenceId < -1){ //adds 5 points of difficulty for every level you skip
            levelDifference = 5*Math.max(9 - newPathSeqId%10, 0);
        } else {
            levelDifference = 5*Math.max(pathwaySequenceId%10 - 1 - newPathSeqId%10, 0);
        }
        int sanityDiff = Math.round(8f-sanity/12.5f); //from 0 to 8 more points depending on your sanity
        int groupDiff = 3-Math.floorDiv(newPathSeqId%10, 3) + (newPathSeqId%10 == 0 ? 2 : 0); //plus 1 for each group
        int actingDiff = (int) (2*(1- Mth.floor(actingProgress + 0.05))); //adds 2 points of difficulty if acting progress isnt at 100%
        //adding 0.05 bc i consider 95% digestion to be complete - this makes the bar sort of "jump" when you "fully digest it",
        //which could make it more satisfying. the added 5% only happens on client side, the server would still tick upwards, and truth be told
        //the client maintains that 95% true amount, but for all intents and purposes 95% is the same as 100%, though its a manual check
        //mind you, advancing without fully digesting a potion will lead to less maximum sanity.

        int diff = levelDifference + sanityDiff + groupDiff + actingDiff;
        if(pathwaySequenceId > -1){
            int level = newPathSeqId%10;
            //if the target sequence is located between your current sequence and sequence 9,
            //aka, a previous sequence to your current one
            //add 4 points of difficulty
            //this is to prevent ppl from drinking previous potions without consequence
            if(level >= pathwaySequenceId%10) diff += 4;
        }
        // more points for demigod levels
        if(newPathSeqId%10 < 5) diff += (int) Math.max((6 - newPathSeqId%10)/1.5f, 0);
        return diff;
    }
}
