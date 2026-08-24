package net.dinomine.potioneer.beyonder.abilities.misc;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.pages.Page;
import net.dinomine.potioneer.beyonder.pages.PageRegistry;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.config.PotioneerGameplayConfig;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.OpenScreenMessage;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public abstract class MysticalKnowledgeAbility extends Ability {
    protected MysticalKnowledgeAbility() {
        this.isPassive = true;
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        List<Page> pages = getPages();
        if(pages.isEmpty()) return false;
        PacketHandler.sendMessageSTC(new OpenScreenMessage(OpenScreenMessage.Screen.Book, PageRegistry.getIdOfPage(pages.get(0))), target);
        return true;
    }

    @Override
    public void onUpgrade(int oldLevel, int newLevel, BeyonderCapability cap, LivingEntity target) {
        if(!PotioneerGameplayConfig.LOSE_PAGES_ON_DROP_SEQUENCE.get()){
            cap.addPages(getPageIds());
        }
    }

    protected abstract List<Page> getPages(int sequenceLevel);

    @Override
    public List<Page> getPages(){
        List<Page> result = new ArrayList<>(List.of());
        result.addAll(getPages(getSequenceLevel()));
        return result;
    }

    protected List<Integer> getPageIds(){
        return PageRegistry.getIdOfPages(getPages(getSequenceLevel()));
    }

    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        if(!PotioneerGameplayConfig.LOSE_PAGES_ON_DROP_SEQUENCE.get()){
            cap.addPages(getPageIds());
        }
    }
}
