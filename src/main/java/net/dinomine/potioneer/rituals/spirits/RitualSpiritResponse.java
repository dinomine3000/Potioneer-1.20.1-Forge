package net.dinomine.potioneer.rituals.spirits;

import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.rituals.RitualInputData;
import net.dinomine.potioneer.rituals.RitualResponseLogic;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.List;
import java.util.Map;

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

    protected void defaultNormalResponse(RitualInputData inputData, Level level){
        String testString = inputData.thirdVerse().toLowerCase();
        if(testString.contains("aid")
                || testString.contains("help")) aidTarget(inputData, level);
        else if(testString.contains("guide")
                || testString.contains("guidance")) guideTarget(inputData, level);
        else if(testString.contains("imbue")
                || testString.contains("infuse")) imbue(inputData, level);
    }

    protected void defaultMethod(RitualInputData inputData){
    }

    protected void aidTarget(RitualInputData inputData, Level level){
    }

    protected void guideTarget(RitualInputData inputData, Level level){
    }

    protected void imbue(RitualInputData inputData, Level level){
    }

    public abstract boolean isValidIncense(String incenseId);

    public abstract boolean isValidItems(List<ItemStack> items);


    public static Player getPlayer(RitualInputData inputData, Level level, boolean targetCaster){
        Player player = level.getPlayerByUUID(inputData.target());
        if(player == null || targetCaster || isPlayerImmuneToTarget(player)){
            player = level.getPlayerByUUID(inputData.caster());
        }
//        if(player == null){
//            player = level.getNearestPlayer(inputData.pos().getX(), inputData.pos().getY(), inputData.pos().getZ(), 1024, null);
//        }
        return player;

    }

    private static boolean isPlayerImmuneToTarget(Player player){
        if(ModList.get().isLoaded("curios")){
            if(CuriosApi.getCuriosInventory(player).resolve().isPresent()){
                ICuriosItemHandler curiosInventory = CuriosApi.getCuriosInventory(player).resolve().get();
                Map<String, ICurioStacksHandler> curios = curiosInventory.getCurios();
                for(ICurioStacksHandler handler: curios.values()){
                    for(int i = 0; i < handler.getSlots(); i++){
                        ItemStack itemStack = handler.getStacks().getStackInSlot(i);
                        if(itemStack.is(ModItems.NAZAR.get())){
                            itemStack.setCount(0);
                            player.level().playSound( null, player, SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 2, 1);
//                            player.playSound(SoundEvents.ITEM_BREAK, 2, 1);
                            return true;
                        }
                    }
                }
            }
        }

        for(ItemStack stack: player.inventoryMenu.getItems()){
            if(stack.is(ModItems.NAZAR.get())){
                stack.setCount(0);
//                player.playSound(SoundEvents.ITEM_BREAK, 2, 1);
                player.level().playSound( null, player, SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 2, 1);
                return true;
            }
        }

        return false;
    }
}
