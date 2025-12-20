package net.dinomine.potioneer.item.custom;

import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.util.misc.ArtifactHelper;
import net.dinomine.potioneer.beyonder.pathways.BeyonderPathway;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;



@OnlyIn(Dist.CLIENT)
public class ArtifactTint implements ItemColor {

    @Override
    public int getColor(ItemStack itemStack, int i) {
        if(i != 1) return -1;
        int color = 0xFFFF0000;
        if(itemStack.hasTag() && itemStack.getTag().contains(ArtifactHelper.BEYONDER_TAG_ID)){
            int pathwayId = itemStack.getTag().getCompound(ArtifactHelper.BEYONDER_TAG_ID).getInt("id");
            color = BeyonderPathway.getSequenceColorFromLevel(pathwayId);
            if(itemStack.getTag().contains(ArtifactHelper.ARTIFACT_TAG_ID)){
                List<String> abilityId = ArtifactHelper.getArtifactIdsFromItem(itemStack);
                if(!abilityId.isEmpty()){
                    boolean enabled = ClientAbilitiesData.isEnabled(abilityId.get(0));
                    color = enabled ? color : (int)(color*0.3);
                }
            }
        }
        return color;
    }
}