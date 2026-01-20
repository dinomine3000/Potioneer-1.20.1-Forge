package net.dinomine.potioneer.item.custom;

import net.dinomine.potioneer.beyonder.pathways.Pathways;
import net.dinomine.potioneer.util.misc.MysticalItemHelper;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;



@OnlyIn(Dist.CLIENT)
public class ArtifactTint implements ItemColor {

    @Override
    public int getColor(ItemStack itemStack, int i) {
        if(i != 1) return -1;
        int color = 0xFFFF0000;
        if(itemStack.hasTag() && itemStack.getTag().contains(MysticalItemHelper.BEYONDER_TAG_ID)){
            int pathwayId = itemStack.getTag().getCompound(MysticalItemHelper.BEYONDER_TAG_ID).getInt("id");
            color = Pathways.getPathwayBySequenceId(pathwayId).getSequenceColorFromLevel(pathwayId);
            if(itemStack.getTag().contains(MysticalItemHelper.ARTIFACT_TAG_ID)){
                boolean enabled = MysticalItemHelper.isArtifactEnabled(itemStack);
                //TODO adjust this once artifacts are done
                color = enabled ? color : (int)(color*0.3);
            }
        }
        return color;
    }
}