package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.config.PotioneerAbilityConfig;
import net.dinomine.potioneer.util.misc.ModTags;
import net.dinomine.potioneer.util.misc.MysticismHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ConjurePickaxeAbility extends Ability {

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "pick";
    }

    public ConjurePickaxeAbility(int sequence){
        super(sequence, PotioneerAbilityConfig.CONJURE_PICKAXE_COOLDOWN.get());
        withCost(PotioneerAbilityConfig.CONJURE_PICKAXE_COST.get());
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return cap.getSpiritualityPercent() >= cost();
        if(!(target instanceof Player player)) return false;
        CompoundTag tag = getData();
        if(!tag.contains("pickaxe")){
            target.sendSystemMessage(Component.translatableWithFallback("message.potioneer.no_pick_saved", "Theres no pickaxe saved. Right-Click to save the pickaxe in your hand."));
            return false;
        }
        if(cap.getSpirituality() >= cost()){
            ItemStack pickaxe = ItemStack.of(tag.getCompound("pickaxe"));
            pickaxe.setDamageValue(pickaxe.getMaxDamage()/2);
            MysticismHelper.updateOrApplyMysticismTag(pickaxe, cost(), player);
            if(!player.addItem(pickaxe)){
                player.drop(pickaxe, false, true);
            }
            cap.requestActiveSpiritualityCost(cost());
            return true;
        }
        return false;
    }

    @Override
    protected boolean secondary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        ItemStack stack = target.getMainHandItem();
        if(stack.isEmpty() || !stack.is(ItemTags.PICKAXES)) return false;
        CompoundTag tag = getData();
        ItemStack pickaxe = stack.copy();
        if(sequenceLevel > 3){
            pickaxe.removeTagKey(ModTags.TAGS.ARTIFACT.getTagId());
            pickaxe.removeTagKey(ModTags.TAGS.BEYONDER.getTagId());
        }
        target.level().playSound(null, target.getOnPos(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1, 1);
        tag.put("pickaxe", pickaxe.save(new CompoundTag()));
        setData(tag, target);
        target.sendSystemMessage(Component.translatable("message.potioneer.saved_pickaxe", stack.getDisplayName().getString()));
        return false;
    }
}
