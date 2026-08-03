package net.dinomine.potioneer.item.custom;

import net.dinomine.potioneer.beyonder.pathways.Pathways;
import net.dinomine.potioneer.util.misc.ModTags;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static net.dinomine.potioneer.util.misc.ModTags.BeyonderInfoTag.getAssociatedPathSeqLevel;
import static net.dinomine.potioneer.util.misc.ModTags.getTagFromItem;
import static net.dinomine.potioneer.util.misc.ModTags.hasTag;


@OnlyIn(Dist.CLIENT)
public class ArtifactTint implements ItemColor {

    @Override
    public int getColor(ItemStack itemStack, int i) {
        if(i != 1) return -1;
        int color = 0xFFFF0000;
        if(hasTag(ModTags.TAGS.BEYONDER, itemStack)
                && hasTag(ModTags.TAGS.ARTIFACT, itemStack)){
            int pathwayId = getAssociatedPathSeqLevel(
                    getTagFromItem(ModTags.TAGS.BEYONDER, itemStack)
            );
            color = Pathways.getPathwayBySequenceId(pathwayId).getSequenceColorFromLevel(pathwayId);

            boolean enabled = ModTags.ArtifactInfoTag.isArtifactEnabled(itemStack);
            //TODO adjust this once artifacts are done
            if(enabled) return color;
            float factor = 0.3f; // 10% brightness

            int r = (color >> 16) & 0xFF;
            int g = (color >> 8)  & 0xFF;
            int b = color & 0xFF;

            r = Math.max(0, (int)(r * factor));
            g = Math.max(0, (int)(g * factor));
            b = Math.max(0, (int)(b * factor));

            return (r << 16) | (g << 8) | b;
        }
        return color;
    }
}