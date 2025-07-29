package net.dinomine.potioneer.item.custom;

import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.util.misc.ArtifactHelper;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class NecklaceItem extends Item {

    public NecklaceItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack amulet = pPlayer.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack gem = pPlayer.getItemInHand(InteractionHand.OFF_HAND);
        if(pLevel.isClientSide()) return InteractionResultHolder.success(amulet);
        if(amulet.is(ModItems.AMULET.get())){
            if(amulet.hasTag() && amulet.getTag().contains(ArtifactHelper.ARTIFACT_TAG_ID)){
                System.out.println("Enabling/Disabling amulet");
                enableAmulet(amulet, !isAmuletEnabled(amulet));
            } else if(gem.is(ModItems.GEM.get())){
                System.out.println("Socketing gem");
                ArtifactHelper.makeAmuletGem(gem, "lucky_trend", 8, 0xFF0000);
                socketGem(amulet, gem);
            }
            return InteractionResultHolder.success(amulet);
        }
        return InteractionResultHolder.fail(amulet);
    }

    public static boolean isAmuletEnabled(ItemStack amulet){
        if(!amulet.hasTag() || !amulet.getTag().contains(ArtifactHelper.ARTIFACT_TAG_ID)) return false;
        return amulet.getTag().getCompound(ArtifactHelper.ARTIFACT_TAG_ID).getBoolean("enabled");
    }

    public static void enableAmulet(ItemStack amulet, boolean enable){
        if(!amulet.is(ModItems.AMULET.get())) return;
        if(!amulet.hasTag() || !amulet.getTag().contains(ArtifactHelper.ARTIFACT_TAG_ID)) return;

        CompoundTag artifactTag = amulet.getTag().getCompound(ArtifactHelper.ARTIFACT_TAG_ID);
        artifactTag.putBoolean("enabled", enable);
    }

    public static void socketGem(ItemStack amulet, ItemStack gem){
        if(!gem.hasTag() || !gem.getTag().contains(ArtifactHelper.GEM_TAG_ID)) return;
        CompoundTag root = amulet.getOrCreateTag();
        CompoundTag gemTag = gem.getTag().getCompound(ArtifactHelper.GEM_TAG_ID);
        gemTag.putBoolean("enabled", true);
        root.put(ArtifactHelper.ARTIFACT_TAG_ID, gemTag);
    }

    public static CompoundTag getGemTagInAmulet(ItemStack amulet){
        if(!amulet.hasTag() || !amulet.getTag().contains(ArtifactHelper.ARTIFACT_TAG_ID)) return new CompoundTag();
        return amulet.getTag().getCompound(ArtifactHelper.ARTIFACT_TAG_ID);
    }

    @OnlyIn(Dist.CLIENT)
    public static class NecklaceItemTint implements ItemColor {

        @Override
        public int getColor(ItemStack itemStack, int i) {
            if(i != 1) return -1;
            if(itemStack.hasTag()){
                if(!itemStack.getTag().getCompound(ArtifactHelper.ARTIFACT_TAG_ID).isEmpty()){
                    return itemStack.getTag().getCompound(ArtifactHelper.ARTIFACT_TAG_ID).getInt("color");
                }
            }
            return 0x00000000;
        }
    }
}
