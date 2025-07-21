package net.dinomine.potioneer.rituals.spirits.defaultGods;

import net.dinomine.potioneer.config.PotioneerRitualsConfig;
import net.dinomine.potioneer.rituals.RitualInputData;
import net.dinomine.potioneer.rituals.RitualResponseLogic;
import net.dinomine.potioneer.rituals.criteria.PathwayCriteria;
import net.dinomine.potioneer.rituals.criteria.ResponseCriteria;
import net.dinomine.potioneer.rituals.criteria.SequenceLevelCriteria;
import net.dinomine.potioneer.rituals.responses.DefaultResponse;
import net.dinomine.potioneer.rituals.spirits.RitualSpiritResponse;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class WheelOfFortuneResponse extends RitualSpiritResponse {

    public WheelOfFortuneResponse(){
        super(null);
        setupLogic();
    }

    private void setupLogic() {
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

    private void punishmentLogic(RitualInputData inputData){
    }

    private void responseLogic(RitualInputData inputData){

    }

    @Override
    protected void aidTarget(RitualInputData inputData) {

    }

    @Override
    protected void guideTarget(RitualInputData inputData) {

    }

    @Override
    protected void inbueTarget(RitualInputData inputData) {

    }

    public boolean isValidIncense(String incenseId){
        return incenseId.equalsIgnoreCase(PotioneerRitualsConfig.WOF_INCENSE.get());
    }

    public boolean isValidItems(List<ItemStack> items){
        return items.stream().anyMatch(stack -> PotioneerRitualsConfig.WOF_INGREDIENTS.get().contains(stack.getItem().toString()));
    }
}
