package net.dinomine.potioneer.rituals.spirits;

import net.dinomine.potioneer.rituals.RitualInputData;
import net.dinomine.potioneer.rituals.RitualResponseLogic;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class RitualSpiritResponse {
    protected RitualResponseLogic responseLogic;

    public RitualSpiritResponse(@Nullable RitualResponseLogic responseLogic){
        this.responseLogic = responseLogic;
    }

    protected void setLogic(RitualResponseLogic newLogic){
        responseLogic = newLogic;
    }

    public void respondTo(RitualInputData inputData, Level level){
        responseLogic.onRitualToEntity(inputData, level);
    }

    public boolean identifiedBy(RitualInputData data){
        return isValidIncense(data.incense()) || isValidItems(data.offerings());
    }

    protected void defaultNormalResponse(RitualInputData inputData){
        String testString = inputData.thirdVerse().toLowerCase();
        if(testString.contains("aid")
                || testString.contains("help")) aidTarget(inputData);
        else if(testString.contains("guide")
                || testString.contains("guidance")) guideTarget(inputData);
        else if(testString.contains("imbue")
                || testString.contains("infuse")) inbueTarget(inputData);
    }

    protected void defaultMethod(RitualInputData inputData){
    }

    protected void aidTarget(RitualInputData inputData){
    }

    protected void guideTarget(RitualInputData inputData){
    }

    protected void inbueTarget(RitualInputData inputData){
    }

    public abstract boolean isValidIncense(String incenseId);

    public abstract boolean isValidItems(List<ItemStack> items);


    public static Player getPlayer(RitualInputData inputData, Level level, boolean targetCaster){
        Player player = level.getPlayerByUUID(inputData.target());
        if(player == null || targetCaster){
            player = level.getPlayerByUUID(inputData.caster());
        }
//        if(player == null){
//            player = level.getNearestPlayer(inputData.pos().getX(), inputData.pos().getY(), inputData.pos().getZ(), 1024, null);
//        }
        return player;

    }
}
