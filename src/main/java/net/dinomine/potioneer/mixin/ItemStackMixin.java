package net.dinomine.potioneer.mixin;

import net.dinomine.potioneer.event.DurabilityHurtEvent;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
            return;
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
            return;
        }
    }
}
