package net.dinomine.potioneer.rituals.spirits.defaultGods;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.config.PotioneerRitualsConfig;
import net.dinomine.potioneer.rituals.RitualInputData;
import net.dinomine.potioneer.rituals.RitualResponseLogic;
import net.dinomine.potioneer.rituals.criteria.PathwayCriteria;
import net.dinomine.potioneer.rituals.criteria.ResponseCriteria;
import net.dinomine.potioneer.rituals.criteria.SequenceLevelCriteria;
import net.dinomine.potioneer.rituals.responses.DefaultResponse;
import net.dinomine.potioneer.rituals.spirits.Deity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class WheelOfFortuneResponse extends Deity {

    public WheelOfFortuneResponse(){
        super(0, PotioneerRitualsConfig.WOF_INGREDIENTS.get(), PotioneerRitualsConfig.WOF_INCENSE.get());
    }

    protected void setupLogic() {
        ArrayList<ResponseCriteria> responseCriteria = new ArrayList<>();
        responseCriteria.add(new SequenceLevelCriteria(8));

        ArrayList<ResponseCriteria> punishmentCriteria = new ArrayList<>();
        punishmentCriteria.add(new PathwayCriteria(0));

        RitualResponseLogic logic = new RitualResponseLogic(
                punishmentCriteria,
                responseCriteria,
                new DefaultResponse(this::punishmentLogic),
                new DefaultResponse(this::responseLogic)
        );

        setLogic(logic);
    }
    
    private void punishmentLogic(RitualInputData inputData, Level level){
        Player player = getPlayer(inputData, level, false);
        if(player == null) return;
        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            cap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.byId(BeyonderEffects.TYRANT_LIGHTNING_TARGET.getEffectId(), 5, 0, 20*5, true),
                    cap, level.getPlayerByUUID(inputData.caster()));
        });
    }

    private void responseLogic(RitualInputData inputData, Level level){
        String testString = inputData.thirdVerse().toLowerCase();
        if(testString.contains("bless")) giveLuck(inputData, level);
        else if(testString.contains("unluck") || testString.contains("misfortune")) giveUnluck(inputData, level);
        else if(testString.contains("trigger")) triggerLuck(inputData, level);
        else defaultNormalResponse(inputData);
    }

    private void giveLuck(RitualInputData inputData, Level level){
        Player target = getPlayer(inputData, level, false);
        if(target == null) return;
        target.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            cap.getLuckManager().grantLuck(target.getRandom().nextInt(-10, 100));
            level.playSound( null, target, SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 2, 1);
            System.out.println("Given luck!");
            for(ItemStack stack: inputData.offerings()){
                if(stack.is(Items.DIAMOND))
                    stack.setCount(0);
                //stack = ItemStack.EMPTY;
            }
        });
    }
    private void giveUnluck(RitualInputData inputData, Level level){
        Player target = getPlayer(inputData, level, false);
        if(target == null) return;
        target.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            cap.getLuckManager().consumeLuck(target.getRandom().nextInt(-10, 100));
            level.playSound( null, target, SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 2, 1);
            System.out.println("Given unluck!");
        });
    }
    private void triggerLuck(RitualInputData inputData, Level level){
        Player target = getPlayer(inputData, level, false);
        if(target == null) return;
        target.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            cap.getLuckManager().castEvent(target);
            level.playSound( null, target, SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 2, 1);
            System.out.println("Cast Event!");
        });
    }
}
