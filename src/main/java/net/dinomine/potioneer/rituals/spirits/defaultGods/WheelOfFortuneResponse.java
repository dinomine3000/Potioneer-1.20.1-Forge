package net.dinomine.potioneer.rituals.spirits.defaultGods;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.pages.PageRegistry;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.config.PotioneerRitualsConfig;
import net.dinomine.potioneer.rituals.RitualInputData;
import net.dinomine.potioneer.rituals.RitualResponseLogic;
import net.dinomine.potioneer.rituals.criteria.*;
import net.dinomine.potioneer.rituals.responses.DefaultResponse;
import net.dinomine.potioneer.rituals.spirits.Deity;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

public class WheelOfFortuneResponse extends Deity {

    public static final String PRAYER = "Perpetually spinning wheel of time, the horizon between zero and infinity, the Goddess who controls Fate and destiny.";

    public WheelOfFortuneResponse(){
        super(0, PotioneerRitualsConfig.WOF_INGREDIENTS.get(), PotioneerRitualsConfig.WOF_INCENSE.get(),
                Component.translatable("deity.potioneer.lady_fate"), "Wiibal",
                PRAYER, PageRegistry.LADY_FATE_1);
    }

    @Override
    public Component getFieltyMessage() {
        return Component.translatable("reputation.potioneer.lady_fate", title);
    }

    protected void setupLogic() {
        ArrayList<ResponseCriteria> responseCriteria = new ArrayList<>();
        responseCriteria.add(new SequenceLevelCriteria(8));

        ArrayList<ResponseCriteria> punishmentCriteria = new ArrayList<>();
        punishmentCriteria.add(new PrayerCriteria(RitualInputData.FIRST_VERSE.INSULTING, RitualInputData.SECOND_VERSE.ARROGANT));

        RitualResponseLogic logic = new RitualResponseLogic(
                punishmentCriteria,
                responseCriteria,
                new DefaultResponse(this::punishmentLogic),
                new DefaultResponse(this::responseLogic)
        );

        setLogic(logic);
    }

    @Override
    protected void aidTarget(RitualInputData inputData, Level level) {
        super.aidTarget(inputData, level);
    }

    private void punishmentLogic(RitualInputData inputData, Level level){
        //TODO
    }

    private void responseLogic(RitualInputData inputData, Level level){
        String testString = inputData.thirdVerse().toLowerCase();
        if(testString.contains("bless")) giveLuck(inputData, level);
        else if(testString.contains("unluck") || testString.contains("misfortune")) giveUnluck(inputData, level);
        else if(testString.contains("trigger")) triggerLuck(inputData, level);
        else defaultNormalResponse(inputData, level);
    }

    private void giveLuck(RitualInputData inputData, Level level){
        Player target = getPlayer(inputData, level, false);
        if(target == null) return;
        target.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            cap.getLuckManager().grantLuck(target.getRandom().nextInt(-10, 100));
            level.playSound( null, target, SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 2, 1);
            System.out.println("Given luck!");
            for(ItemStack stack: RitualInputData.getLiveItemStacks(inputData, level)){
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
