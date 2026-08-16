package net.dinomine.potioneer.rituals.responses;

import net.dinomine.potioneer.beyonder.pathways.BeyonderPathway;
import net.dinomine.potioneer.beyonder.pathways.Pathways;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.block.entity.RitualPedestalBlockEntity;
import net.dinomine.potioneer.recipe.CharmRecipe;
import net.dinomine.potioneer.recipe.RitualContainer;
import net.dinomine.potioneer.rituals.RitualInputData;
import net.dinomine.potioneer.rituals.spirits.RitualSpiritResponse;
import net.dinomine.potioneer.util.PotionIngredient;
import net.dinomine.potioneer.util.misc.ModNbtUtils;
import net.dinomine.potioneer.util.misc.MysticalItemHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class PlayerResponse extends SpiritResponse {
    @Override
    public void enactResponse(RitualInputData inputData, Level level) {
        defaultNormalResponse(inputData, level);
    }

    @Override
    public CompoundTag saveToNBT() {
        return envelopTag(new CompoundTag(), "player");
    }

    protected void defaultNormalResponse(RitualInputData inputData, Level level){
        String testString = inputData.thirdVerse().toLowerCase();
        if(testString.contains("aid")
                || testString.contains("help")) makeCharm(inputData, level);
        else if(testString.contains("imbue")
                || testString.contains("infuse")) chargeItem(inputData, level);
    }

    private static final float CHARGE_PERCENT = 0.3f;
    private void chargeItem(RitualInputData inputData, Level level){
        Player player = level.getPlayerByUUID(inputData.caster());
        if(player == null) return;
        Optional<BeyonderCapability> optCap = CapProvider.beyonder(player);
        if(optCap.isEmpty()) return;
        BeyonderCapability cap = optCap.get();

        if(cap.getSpirituality() < 0.3f*cap.getMaxSpirituality()) {
            System.out.println("Not enough spirituality");
            return;
        }
        ItemStack toCharge = ItemStack.EMPTY;
        for(ItemStack stack: inputData.offerings()){
            if(MysticalItemHelper.isChargeableArtifact(stack)){
                toCharge = stack;
                break;
            }
        }
        if(toCharge.isEmpty()){
            System.out.println("no chargeable item");
            return;
        }

        float spir = cap.getMaxSpirituality()*0.3f;
        cap.requestActiveSpiritualityCost(spir);
        ItemStack res = MysticalItemHelper.chargeArtifact(toCharge.copy(), spir, player);

        for(RitualPedestalBlockEntity be: RitualInputData.getPedestalsOfRitual(inputData, level)){
            if(ItemStack.matches(be.getRenderStack(), toCharge)){
                be.forcefullySetItem(res);
                System.out.println("Charge Successful");
                return;
            }
        }
        System.out.println("Could not find original");
    }

    private void makeCharm(RitualInputData inputData, Level level) {
        Player player = level.getPlayerByUUID(inputData.caster());
        if(player == null) return;
        int inputPathway = inputData.pathwaySequenceId()%10;
        int sequenceLevel = 9;
        //if targeting someone else in the ritual, make a charm based on their level
        if(player.getCapability(CapProvider.BEYONDER_STATS).resolve().isPresent()){
            BeyonderCapability cap = player.getCapability(CapProvider.BEYONDER_STATS).resolve().get();
            inputPathway = cap.getCharacteristicManager().getPathwayId();
            sequenceLevel = cap.getSequenceLevel();
        }
        //TODO maybe they can make charms if they have a characteristic in the offerings
        RitualContainer container = new RitualContainer(inputPathway, 9 - sequenceLevel, inputData.offerings()).withDesiredLevel(RitualSpiritResponse.getDesiredLevel(inputData.thirdVerse()));
        List<CharmRecipe> recipeMatches = level.getRecipeManager().getRecipesFor(CharmRecipe.Type.INSTANCE, container, level);
        BeyonderPathway pathway = Pathways.getPathwayById(inputPathway);
        List<String> availableCharms = pathway.canCraftEffectCharms(sequenceLevel);
        if(availableCharms.isEmpty()) return;
        for(CharmRecipe rec: recipeMatches) {
            if(!availableCharms.contains(rec.getEffectId())) continue;
            PotionIngredient baseMaterial = rec.getBaseMaterial();
            ItemStack charm = rec.assemble(container, level.registryAccess());

            boolean flag = false;
            for(RitualPedestalBlockEntity be: RitualInputData.getPedestalsOfRitual(inputData, level)){
                if(!flag && baseMaterial.is(be.getRenderStack())){
                    be.forcefullySetItem(charm);
                    flag = true;
                } else {
                    be.forcefullySetItem(ItemStack.EMPTY);
                }
            }
        }

    }

    private void aidTarget(RitualInputData inputData, Level level) {
    }
}
