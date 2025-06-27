package net.dinomine.potioneer.beyonder.misc;

import net.dinomine.potioneer.beyonder.abilities.Beyonder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record DivinationResult(boolean yesNo, List<BlockPos> positions, int sequence, float status, String clue, ItemStack item) {

    public static enum Status {
        PRETTY_GOOD,
        ALRIGHT,
        MEH,
        BAD,
        TERRIBLE
    }

    public Status getDescriptiveStatus(){
        if(status <= 0.2f) return Status.TERRIBLE;
        if(status <= 0.4f) return Status.BAD;
        if(status <= 0.6f) return Status.MEH;
        if(status <= 0.8f) return Status.ALRIGHT;
        return Status.PRETTY_GOOD;
    }

    @Override
    public String toString() {
        return "Yes/No? - " + yesNo
                + "\nPositions - " + positions.toString()
                + "\nSequence " + sequence + " " + Beyonder.getSequenceNameFromId(sequence, true)
                + "\nStatus value of " + status + " - " + getDescriptiveStatus()
                + "\nClue - " + clue
                + "\nAssociated Item - " + item.toString();
    }
}
