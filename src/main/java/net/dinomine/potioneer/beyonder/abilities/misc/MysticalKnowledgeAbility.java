package net.dinomine.potioneer.beyonder.abilities.misc;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.pages.Page;
import net.dinomine.potioneer.beyonder.pages.PageRegistry;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.config.PotioneerCommonConfig;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.OpenScreenMessage;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class MysticalKnowledgeAbility extends Ability {
    /**
     * pass the sequence level or pathway-sequence id to define the abilities sequence level
     * abilities that depend on changing pathways like Cogitation, that exists for every pathway, need to process their own pathway-sequence id here.
     * I dont ask specifically for sequence level OR pathway id, but if you want to choose one, pass along the pathwaySequenceId.
     *
     * @param sequenceLevel
     */
    protected MysticalKnowledgeAbility(int sequenceLevel) {
        super(sequenceLevel);
        this.isPassive = true;
        this.isActive = false;
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        List<Page> pages = getPages();
        if(pages.isEmpty()) return false;
        PacketHandler.sendMessageSTC(new OpenScreenMessage(OpenScreenMessage.Screen.Book, PageRegistry.getIdOfPage(pages.get(0))), target);
        return true;
    }

    @Override
    public void onUpgrade(int oldLevel, int newLevel, LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(!PotioneerCommonConfig.LOSE_PAGES_ON_DROP_SEQUENCE.get()){
            cap.addPages(getPageIds());
        }
    }

    protected abstract List<Page> getPages(int sequenceLevel);

    @Override
    public List<Page> getPages(){
        List<Page> result = new ArrayList<>(List.of());
        result.addAll(getPages(sequenceLevel));
        return result;
    }

    protected List<Integer> getPageIds(){
        return PageRegistry.getIdOfPages(getPages(getSequenceLevel()));
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        if(!PotioneerCommonConfig.LOSE_PAGES_ON_DROP_SEQUENCE.get()){
            cap.addPages(getPageIds());
        }
    }
}
