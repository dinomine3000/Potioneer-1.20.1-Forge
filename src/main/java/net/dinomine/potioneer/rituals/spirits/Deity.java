package net.dinomine.potioneer.rituals.spirits;

import net.dinomine.potioneer.beyonder.pages.Page;
import net.dinomine.potioneer.beyonder.pathways.Pathways;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.block.entity.RitualPedestalBlockEntity;
import net.dinomine.potioneer.recipe.CharmRecipe;
import net.dinomine.potioneer.recipe.RitualContainer;
import net.dinomine.potioneer.rituals.RitualInputData;
import net.dinomine.potioneer.util.PotionIngredient;
import net.dinomine.potioneer.util.RoughStringMatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public abstract class Deity extends EvilSpirit {

    protected int pathwayId;
    protected String incense;
    protected Component title;
    protected String trueName;
    protected String prayer;
    protected Page page;

    public Deity(int pathwayId, List<String> validItems, String incense, Component title, String trueName, String prayer, Page page){
        super();
        setupLogic();
        this.pathwayId = pathwayId;
        this.itemsId = validItems;
        this.incense = incense;
        this.title = title;
        this.trueName = trueName;
        this.prayer = prayer;
        this.page = page;
    }

    public Page getInfoPage(){ return this.page;}

    public boolean matchPrayer(String message){
        return RoughStringMatcher.roughlyMatches(this.prayer, message, 0.7d, 0.7d, 10);
    }

    public Component getFieltyMessage(){
        return Component.translatable("reputation.potioneer.following", title);
    }

    public Component getTitle(){
        return title;
    }

    public String getTrueName(){
        return trueName;
    }

    public void onTrueNameSpoken(LivingEntity target, LivingEntityBeyonderCapability cap){}

    protected abstract void setupLogic();

    @Override
    protected void guideTarget(RitualInputData inputData, Level level) {
        if(inputData.offerings().isEmpty()) return;

    }

    @Override
    protected void imbue(RitualInputData inputData, Level level) {
        Player targetPlayer = getPlayer(inputData, level, false);
        int inputPathway = pathwayId;
        int reputation = 0; // stand in for the actual reputation calculation TODO
        if(targetPlayer.getCapability(BeyonderStatsProvider.BEYONDER_STATS).isPresent() && targetPlayer.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve().isPresent()){
            reputation = targetPlayer.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve().get().getReputation(inputPathway);
        }
        //if targeting someone else in the ritual, make a charm based on their level
        if(targetPlayer.getUUID().compareTo(inputData.caster()) != 0){
            if(targetPlayer.getCapability(BeyonderStatsProvider.BEYONDER_STATS).isPresent()){
                inputPathway = targetPlayer.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve().get().getCharacteristicManager().getPathwayId();
                reputation = (int) ((9 - targetPlayer.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve().get().getSequenceLevel())/2f);
            }
        }
        RitualContainer container = new RitualContainer(inputPathway, reputation, inputData.offerings());
        Optional<CharmRecipe> recipeOptional = level.getRecipeManager().getRecipeFor(CharmRecipe.Type.INSTANCE, container, level);
        if(recipeOptional.isPresent()){
            CharmRecipe rec = recipeOptional.get();
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

    @Override
    public boolean isValidIncense(String incenseId){
//        return incenseId.equalsIgnoreCase(incense);
        return true;
    }

    @Override
    public CompoundTag saveToNBT() {
        CompoundTag deityTag = new CompoundTag();
        deityTag.putInt("pathwayId", pathwayId);
        return deityTag;
    }

    public static Deity getDeityFromNBT(CompoundTag tag){
        int pathway = tag.getInt("pathwayId");
        return Pathways.getPathwayById(pathway).getDefaultDeity();
    }
}
