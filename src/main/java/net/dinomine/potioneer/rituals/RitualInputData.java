package net.dinomine.potioneer.rituals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public record RitualInputData(FIRST_VERSE firstVerse, SECOND_VERSE secondVerse,
                              @Nullable Player caster, BlockPos casterPosition,
                              @Nullable Player target, BlockPos targetPosition,
                              int pathwayId,
                              List<ItemStack> offerings, ACTION action,
                              String incense) {
    public enum FIRST_VERSE{
        EXALTATION, //mystery
        INSULTING, //red priest
        RESPECTFUL, //paragon
        CASUAL, //wheel of fortune
        DEFERENT //tyrant
    }

    public enum SECOND_VERSE{
        MEEK, //mystery
        ARROGANT, //red priest
        CURIOUS, //paragon
        COMPOSED, //wheel of fortune
        MODEST //tyrant
    }

    public enum ACTION{
        GUIDANCE, //pathway related items
        IMBUEMENT, //charm creation
        AID, //effect giving

        TELEPORT, //mystery
        CURSE, //mystery

        FIX, //paragon
        DUPLICATE, //paragon

        WEATHER, //tyrant
        DIVINATION, //tyrant

        BUFF, //red priest
        BLESSING, //red priest

        GIVE_LUCK, //wheel of fortune
        GIVE_UNLUCK, //wheel of fortune
        TRIGGER_LUCK_EVENT //wheel of fortune
    }


}
