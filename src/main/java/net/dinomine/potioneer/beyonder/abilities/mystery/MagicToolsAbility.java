package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.misc.MysticalKnowledgeAbility;
import net.dinomine.potioneer.beyonder.pages.Page;
import net.dinomine.potioneer.beyonder.pages.PageRegistry;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.util.misc.MysticalItemHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class MagicToolsAbility extends MysticalKnowledgeAbility {
    public MagicToolsAbility(int sequenceLevel) {
        super(sequenceLevel);
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
        if(!(target instanceof Player player)) return false;
        float cost = 0.3f*cap.getMaxSpirituality();
        if(cap.getSpirituality() < cost) return false;
        Ability abl = cap.getAbilitiesManager().getQuickAbility();
        if(abl == null) return false;
        ItemStack stack = target.getMainHandItem();
        if(!MysticalItemHelper.isValidItemForArtifact(stack)) return false;
        ItemStack item = MysticalItemHelper.generateMysticalItem(stack, abl.getAbilityId(), abl.getSequenceLevel(), 0);
        MysticalItemHelper.chargeArtifact(item, cost, player);
        cap.requestActiveSpiritualityCost(cost);
        return true;
    }

    @Override
    protected boolean secondary(BeyonderCapability cap, LivingEntity target) {
        return super.primary(cap, target);
    }

    @Override
    protected List<Page> getPages(int sequenceLevel) {
        return List.of(PageRegistry.RITUALS_DAGGER, PageRegistry.RITUALS_INK, PageRegistry.ITEM_CHARGING, PageRegistry.SINGLE_CANDLE);
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "magic_tools";
    }
}
