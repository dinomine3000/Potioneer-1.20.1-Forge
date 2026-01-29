package net.dinomine.potioneer.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraftforge.common.data.ExistingFileHelper;

import static net.dinomine.potioneer.beyonder.damages.PotioneerDamage.*;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

/**
 * i did all the damage type / damage sources thanks to the open source code of the Draconic Evolution mod.
 * I do not claim to have made this by myself. and by this i mean anything to do with damage source/damage type and damage tags creation or alteration.
 */
public class DamageTypeGenerator extends DamageTypeTagsProvider {
    public DamageTypeGenerator(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(DamageTypeTags.BYPASSES_ARMOR).add(LOW_SANITY, ASTEROID, CHRYON_PIERCE, CRIT);
        tag(DamageTypeTags.BYPASSES_SHIELD).add(LOW_SANITY, ASTEROID, CHRYON_PIERCE, CRIT);
        tag(DamageTypeTags.BYPASSES_RESISTANCE).add(LOW_SANITY, ASTEROID, CHRYON_PIERCE, CRIT);
        tag(DamageTypeTags.BYPASSES_EFFECTS).add(LOW_SANITY, ASTEROID, CHRYON_PIERCE, CRIT);
        tag(DamageTypeTags.BYPASSES_ENCHANTMENTS).add(LOW_SANITY, CHRYON_PIERCE, CRIT);
        tag(DamageTypeTags.BYPASSES_COOLDOWN).add(LOW_SANITY, CHRYON_PIERCE, CRIT);
        tag(DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS).add(LOW_SANITY, CRIT);
//        tag(DamageTypeTags.AVOIDS_GUARDIAN_THORNS).add();
        //eventually add Annihilation-type damage sources to the bypasses invulnerability
        tag(DamageTypeTags.BYPASSES_INVULNERABILITY).add(LOW_SANITY, LOW_SANITY_KILL);

        tag(Tags.ABSOLUTE).add(CRIT, LOW_SANITY, LOW_SANITY_KILL, LOW_SPIRITUALITY);
        tag(Tags.MENTAL).add(LOW_SANITY, LOW_SANITY_KILL, LOW_SPIRITUALITY);
    }
}
