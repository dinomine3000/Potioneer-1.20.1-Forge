package net.dinomine.potioneer.rituals.responses;

import net.dinomine.potioneer.beyonder.pathways.BeyonderPathway;
import net.dinomine.potioneer.beyonder.pathways.Pathways;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.block.entity.RitualPedestalBlockEntity;
import net.dinomine.potioneer.recipe.CharmRecipe;
import net.dinomine.potioneer.recipe.RitualContainer;
import net.dinomine.potioneer.rituals.RitualInputData;
import net.dinomine.potioneer.util.PotionIngredient;
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
                || testString.contains("help")) aidTarget(inputData, level);
        else if(testString.contains("imbue")
                || testString.contains("infuse")) imbue(inputData, level);
    }

    private void imbue(RitualInputData inputData, Level level) {
        Player player = level.getPlayerByUUID(inputData.caster());
        int inputPathway = inputData.pathwaySequenceId()%10;
        int sequenceLevel = 9;
        //if targeting someone else in the ritual, make a charm based on their level
        if(player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).isPresent() && player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve().isPresent()){
            LivingEntityBeyonderCapability cap = player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve().get();
            inputPathway = cap.getCharacteristicManager().getPathwayId();
            sequenceLevel = cap.getSequenceLevel();
        }
        //TODO maybe they can make charms if they have a characteristic in the offerings
        RitualContainer container = new RitualContainer(inputPathway, 10 - sequenceLevel, inputData.offerings());
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
