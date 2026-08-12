package net.dinomine.potioneer.mixin;

import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.event.DurabilityHurtEvent;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.util.misc.ModNbtUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AdventureModeCheck;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    public abstract boolean isDamageableItem();

    @Shadow
    public abstract int getDamageValue();

    @Shadow
    public abstract int getMaxDamage();

    @Shadow
    public abstract void setDamageValue(int arg1);

    @Shadow @Nullable private AdventureModeCheck adventurePlaceCheck;

    @Unique
    private ItemStack potioneer$self(){
        return (ItemStack) (Object) this;
    }

    /**
     * @author dinomine3000
     * @reason paragon and wheel of fortune beyonders should have abilities that allow them to negate or reduce damage taken to items.
     * this should happen even if the item will break, so i cant just "recover X durability", especially in cases like the Axe that can take more damage.
     */
    @Inject(method = "hurt(ILnet/minecraft/util/RandomSource;Lnet/minecraft/server/level/ServerPlayer;)Z",
            at = @At("HEAD"), cancellable = true)
    public void onHurt(int pAmount, RandomSource pRandom, ServerPlayer pUser, CallbackInfoReturnable<Boolean> cir) {
        int hold = pAmount;
        DurabilityHurtEvent event = new DurabilityHurtEvent(pUser, pAmount, potioneer$self());
        boolean canceled = MinecraftForge.EVENT_BUS.post(event);
        if(canceled){
            cir.setReturnValue(false);
            return;
        }
        if(event.getAmount() == hold){
            return;
        }
        pAmount = event.getAmount();
        if (!this.isDamageableItem()) {
            cir.setReturnValue(false);
        } else {
            if (pAmount > 0) {
                int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, potioneer$self());
                int j = 0;

                for(int k = 0; i > 0 && k < pAmount; ++k) {
                    if (DigDurabilityEnchantment.shouldIgnoreDurabilityDrop(potioneer$self(), i, pRandom)) {
                        ++j;
                    }
                }

                pAmount -= j;
                if (pAmount <= 0) {
                    cir.setReturnValue(false);
                    return;
                }
            }

            if (pUser != null && pAmount != 0) {
                CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(pUser, potioneer$self(), this.getDamageValue() + pAmount);
            }

            int l = this.getDamageValue() + pAmount;
            this.setDamageValue(l);
            cir.setReturnValue(l >= this.getMaxDamage());
        }
    }


    /**
     * @author dinomine3000
     * @reason artifacts items cant break. if they break, the player should get a "useless" item with the same abilities and characteristics
     */
    @Inject(method = "hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
    public <T extends LivingEntity> void hurtAndBreak(int pAmount, T pEntity, Consumer<T> pOnBroken, CallbackInfo ci) {
        if(!(pEntity instanceof Player player)) return;
        ItemStack stack = potioneer$self();
        if(stack.getCount() <= 1){
            //give player mock item with same relevant tags
            ItemStack broken = new ItemStack(ModItems.BROKEN_ARTIFACT.get());
            CompoundTag artifactTag = ModNbtUtils.getTagFromItem(ModNbtUtils.TAGS.ARTIFACT, stack);
            CompoundTag beyonderTag = ModNbtUtils.getTagFromItem(ModNbtUtils.TAGS.BEYONDER, stack);
            CompoundTag mystTag = ModNbtUtils.getTagFromItem(ModNbtUtils.TAGS.MYSTICISM, stack);

            //dont check myst tag, that one doesnt really matter here. other tools with spirituality can be destroyed without issue.
            if(artifactTag == null && beyonderTag == null) return;

            ModNbtUtils.setItemRootTag(broken, artifactTag, ModNbtUtils.TAGS.ARTIFACT);
            ModNbtUtils.setItemRootTag(broken, beyonderTag, ModNbtUtils.TAGS.BEYONDER);
            ModNbtUtils.setItemRootTag(broken, mystTag, ModNbtUtils.TAGS.MYSTICISM);

            if(stack.hasCustomHoverName())
                broken.setHoverName(stack.getHoverName());

            if(!player.addItem(broken.copy())){
                player.drop(broken, false, true);
            }

            player.getCapability(CapProvider.BEYONDER_STATS).ifPresent(cap -> {
                cap.getAbilitiesManager().updateArtifact(ModNbtUtils.ArtifactInfoTag.getArtifactId(artifactTag), player, broken);
            });
        }
    }
}
