package net.dinomine.potioneer.beyonder.abilities.paragon;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.menus.CrafterAnvilMenu;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

public class AnvilGuiAbility extends Ability {
    private boolean levelUp = false;
    public AnvilGuiAbility(int sequence){
        levelUp = sequence < 8;
        this.info = new AbilityInfo(109, levelUp ? 104 : 248, "Anvil Gui", 40 + sequence, levelUp ? 10 : 100, this.getCooldown(), "anvil_gui_" + (levelUp ? "2" : "1"));
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public boolean active(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(cap.getSpirituality() > info.cost() && target instanceof ServerPlayer player){
            if(levelUp) {
                NetworkHooks.openScreen(
                        player,
                        new SimpleMenuProvider((i, inventory, player1) ->
                                new CrafterAnvilMenu(i, inventory, ContainerLevelAccess.create(player1.level(), player1.getOnPos()), getSequence()),
                                Component.translatable("potioneer.menu.anvil_menu")),
                        buff -> buff.writeInt(getSequence()));

                cap.requestActiveSpiritualityCost(info.cost());
                return true;
            } else {
                ItemStack book = player.getMainHandItem();
                if(!book.is(Items.BOOK)) return false;
                RandomSource random = RandomSource.create();
                random.setSeed(player.getEnchantmentSeed());
                ItemStack fuel = player.getItemInHand(InteractionHand.OFF_HAND);
                System.out.println(fuel);

                int enchantmentLevel = ForgeEventFactory.onEnchantmentLevelSet(target.level(), target.getOnPos(), 2, fuel.getCount()*2, book, 3);

                List<EnchantmentInstance> list = EnchantmentHelper.selectEnchantment(random, book, enchantmentLevel, false);
                if (list.size() > 1) {
                    list.remove(random.nextInt(list.size()));
                }

                if (!list.isEmpty()) {
                    book.shrink(1);
                    book = new ItemStack(net.minecraft.world.item.Items.ENCHANTED_BOOK);
                    CompoundTag compoundtag = book.getTag();
                    if (compoundtag != null) {
                        book.setTag(compoundtag.copy());
                    }

                    for (EnchantmentInstance enchantmentInstance : list) {
                        EnchantedBookItem.addEnchantment(book, enchantmentInstance);
                    }
                    player.onEnchantmentPerformed(book,0);
                    player.awardStat(Stats.ENCHANT_ITEM);
                    if (player instanceof ServerPlayer) {
                        CriteriaTriggers.ENCHANTED_ITEM.trigger(player, book, 2);
                    }
                    player.addItem(book);
                    player.level().playSound(null, player.getOnPos(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, player.level().random.nextFloat() * 0.1F + 0.9F);

                    if(fuel.is(ModItems.GOLDEN_DROP.get()) && fuel.getCount() > 0){
                        fuel.shrink(1);
                    } else {
                        cap.requestActiveSpiritualityCost(info.cost());
                        return true;
                    }

                    //this.enchantSlots.setChanged();

                    //this.slotsChanged(this.enchantSlots);
                }
            }
        }
        return false;
    }

    @Override
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void activate(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void deactivate(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }
}
