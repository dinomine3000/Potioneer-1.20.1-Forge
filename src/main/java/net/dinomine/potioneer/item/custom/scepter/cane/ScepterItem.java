package net.dinomine.potioneer.item.custom.scepter.cane;

import net.dinomine.potioneer.entities.ModEntities;
import net.dinomine.potioneer.entities.custom.DivinationRodEntity;
import net.dinomine.potioneer.entities.custom.SeaGodScepterEntity;
import net.dinomine.potioneer.item.custom.DivinationRodItem;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class ScepterItem extends DivinationRodItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ScepterItem(Properties pProperties) {
        super(pProperties, 7, -3.1f, Tiers.DIAMOND);
    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return false;
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        pContext.getLevel().playSound(pContext.getPlayer(), pContext.getClickedPos(), SoundEvents.METAL_PLACE, SoundSource.PLAYERS);
        if(pContext.getLevel().isClientSide()) return InteractionResult.SUCCESS;
        DivinationRodEntity entity = new SeaGodScepterEntity(ModEntities.SEA_GOD_SCEPTER.get(), pContext.getLevel(), pContext.getItemInHand().copy());
        Vec3 pos = pContext.getClickedPos().relative(pContext.getClickedFace()).getCenter().add(0, -0.5f, 0);

        entity.moveTo(pos.x, pos.y, pos.z, pContext.getRotation(), 0);
        pContext.getLevel().addFreshEntity(entity);
        if(!pContext.getPlayer().isCreative()){
            pContext.getPlayer().setItemInHand(pContext.getHand(), ItemStack.EMPTY);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean isEnchantable(ItemStack pStack) {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private ScepterItemRenderer renderer = null;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                // Don't instantiate until ready. This prevents race conditions breaking things
                if (this.renderer == null)
                    this.renderer = new ScepterItemRenderer();

                return renderer;
            }

        });
    }
}
