package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.minecraft.util.RandomSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraftforge.common.ForgeMod;

import java.util.ArrayList;
import java.util.List;

public class TheftAbility extends Ability {
    private static final int LEVEL_FOR_TRADE_THEFT = 6;
    public TheftAbility(int sequenceLevel) {
        super(sequenceLevel);
        defaultMaxCooldown = 20*2;
        withCost(10);
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return sequenceLevel < 8 ? "theft_2" : "theft";
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity caster) {
        if(cap.getSpirituality() < cost() && sequenceLevel > 7) return false;
        if(caster.level().isClientSide()) return true;

        float extraReach = 0.5f + (9-sequenceLevel)*0.5f;
        LivingEntity target = AbilityFunctionHelper.getLivingEntityLooking(caster, caster.getAttributeBaseValue(ForgeMod.ENTITY_REACH.get()) + extraReach, 0);
        if(target == null) return false;

        cap.requestActiveSpiritualityCost(cost());
        ItemStack ogStack = null;
        ItemStack toGive = null;
        if(!target.getMainHandItem().isEmpty()){
            ogStack = target.getMainHandItem();
            toGive = ogStack.copy();
        } else {
            //50% of the time, prefer armor
            if(caster.getRandom().nextBoolean()){
                for (ItemStack armorStack : target.getArmorSlots()) {
                    if (armorStack.isEmpty()) continue;
                    ogStack = armorStack;
                    toGive = armorStack.copy();
                }
            }
            //if it landed on the other 50%, or it didnt find armor
            if(ogStack == null){
                if(target instanceof Villager villager){
                    //try to steal a trade.
                    if(sequenceLevel <= LEVEL_FOR_TRADE_THEFT || cap.getLuckManager().passesLuckCheck(0.1f, 50, 0, caster.getRandom())){
                        toGive = stealVillagerTrade(villager, sequenceLevel <= LEVEL_FOR_TRADE_THEFT ? Integer.MAX_VALUE : 2 * (9 - sequenceLevel));
                    }
                    //otherwise, steal their inventory (where they keep them carrots!!)
                    if(toGive == null || toGive.isEmpty()){
                        List<ItemStack> items = new ArrayList<>();
                        SimpleContainer inventory = villager.getInventory();

                        for (int i = 0; i < inventory.getContainerSize(); i++) {
                            ItemStack stack = inventory.getItem(i);
                            if (!stack.isEmpty()) items.add(stack);
                            if(!items.isEmpty()){
                                ogStack = items.get(caster.getRandom().nextInt(items.size()));
                                toGive = ogStack.copy();
                            }
                        }
                    }
                }
                else if(target instanceof Player playerTarget){
                    List<ItemStack> items = playerTarget.inventoryMenu.getItems().stream().filter(item -> !item.isEmpty()).toList();
                    if(!items.isEmpty()){
                        ogStack = items.get(caster.getRandom().nextInt(items.size()));
                        toGive = ogStack.copy();
                    }
                } else {
                    List<ItemStack> items = new ArrayList<>();
                    for(ItemStack stack: target.getAllSlots()) if(!stack.isEmpty()) items.add(stack);
                    if(!items.isEmpty()){
                        ogStack = items.get(caster.getRandom().nextInt(items.size()));
                        toGive = ogStack.copy();
                    }
                }
            }
        }

        if(toGive != null && !toGive.isEmpty() && caster instanceof Player player && !player.addItem(toGive)) player.drop(toGive, true, true);
        if(ogStack != null){
            ogStack.setCount(0);
            setNextCooldownAs(20*7);
        }

        CapProvider.beyonder(target).ifPresent(otherCap -> {
            float toSteal = cap.getMaxSpirituality()*0.1f;
            otherCap.changeSpirituality(-toSteal);
            cap.changeSpirituality(toSteal);
            setNextCooldownAs(20*15);
        });
        return true;
    }

    private ItemStack stealVillagerTrade(Villager villager, int maxCount){
        List<MerchantOffer> offers = villager.getOffers().stream()
                .filter(off -> !off.isOutOfStock())
                .toList();
        if (offers.isEmpty()) return ItemStack.EMPTY;

        MerchantOffer toSteal = offers.get(RandomSource.create().nextInt(offers.size()));

        int itemPerTrade = toSteal.getResult().getCount();
        int remainingTrades = toSteal.getMaxUses() - toSteal.getUses();
        int totalItemsInStock = remainingTrades * itemPerTrade;

        if (totalItemsInStock <= 0) return ItemStack.EMPTY;

        int itemsToSteal = Math.min(totalItemsInStock, maxCount);
        int tradesToConsume = (int) Math.ceil((double) itemsToSteal / itemPerTrade);

        for (int i = 0; i < tradesToConsume; i++) {
            toSteal.increaseUses();
        }
        ItemStack stackToGive = toSteal.assemble();
        stackToGive.setCount(itemsToSteal);
        return stackToGive;
    }
}
