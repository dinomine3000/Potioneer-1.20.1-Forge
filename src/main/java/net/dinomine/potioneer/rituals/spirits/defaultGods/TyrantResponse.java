package net.dinomine.potioneer.rituals.spirits.defaultGods;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.pages.PageRegistry;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.config.PotioneerRitualsConfig;
import net.dinomine.potioneer.rituals.RitualInputData;
import net.dinomine.potioneer.rituals.RitualResponseLogic;
import net.dinomine.potioneer.rituals.criteria.*;
import net.dinomine.potioneer.rituals.responses.DefaultResponse;
import net.dinomine.potioneer.rituals.spirits.Deity;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class TyrantResponse extends Deity {

    public static final String PRAYER = "You are the Order born among Chaos, the great conqueror of the Golden World, My ruler for all time, The tyrant over all mongrels";

    public TyrantResponse(){
        super(0, PotioneerRitualsConfig.TYRANT_INGREDIENTS.get(), PotioneerRitualsConfig.TYRANT_INCENSE.get(),
                Component.translatable("deity.potioneer.king_of_heroes"), "Leodero",
                PRAYER, PageRegistry.KING_OF_HEROES_1);
    }

    @Override
    public Component getFieltyMessage() {
        return Component.translatable("reputation.potioneer.king_of_heroes", title);
    }

    @Override
    public void onTrueNameSpoken(LivingEntity target, LivingEntityBeyonderCapability cap) {
        cap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.byId(BeyonderEffects.TYRANT_LIGHTNING_TARGET.getEffectId(), 1, 0, 20*10, true), cap, target);
        cap.requestActiveSpiritualityCost(1000);
        cap.changeSanity(-25);
    }

    protected void setupLogic() {
        ArrayList<ResponseCriteria> responseCriteria = new ArrayList<>();
        responseCriteria.add(new AndCriteria(List.of(new ReputationCriteria(3, 1), new PrayerCriteria(RitualInputData.FIRST_VERSE.DEFERENT, RitualInputData.SECOND_VERSE.MODEST))));

        ArrayList<ResponseCriteria> punishmentCriteria = new ArrayList<>();
        punishmentCriteria.add(new OrCriteria(List.of(new PathwayCriteria(1), new PrayerCriteria(RitualInputData.FIRST_VERSE.INSULTING, RitualInputData.SECOND_VERSE.ARROGANT))));

        RitualResponseLogic logic = new RitualResponseLogic(
                punishmentCriteria,
                responseCriteria,
                new DefaultResponse(this::punishmentLogic),
                new DefaultResponse(this::responseLogic)
        );

        setLogic(logic);
    }

    private void punishmentLogic(RitualInputData inputData, Level level){
        Player player = getPlayer(inputData, level, true);
        if(player == null) return;
        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            cap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.byId(BeyonderEffects.TYRANT_LIGHTNING_TARGET.getEffectId(), 5, 0, 20*2, true),
                    cap, player);
        });
    }

    private void responseLogic(RitualInputData inputData, Level level){
        defaultNormalResponse(inputData, level);
    }
}
