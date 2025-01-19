package net.dinomine.potioneer.beyonder.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.dinomine.potioneer.Potioneer;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;

public final class KeyBindings {
    public static final KeyBindings INSTANCE = new KeyBindings();

    private KeyBindings(){}

    private static final String CATEGORY = "key.categories." + Potioneer.MOD_ID;

    public final KeyMapping beyonderMenuKey = new KeyMapping(
            "key." + Potioneer.MOD_ID + ".beyonder_menu_key",
            KeyConflictContext.IN_GAME,
            InputConstants.getKey(InputConstants.KEY_G, -1),
            CATEGORY
    );

    public final KeyMapping quickAbilityKey = new KeyMapping(
            "key." + Potioneer.MOD_ID + ".quick_ability_key",
            KeyConflictContext.IN_GAME,
            InputConstants.getKey(InputConstants.KEY_R, -1),
            CATEGORY
    );

    public final KeyMapping showHotbarKey = new KeyMapping(
            "key." + Potioneer.MOD_ID + ".show_hotbar_key",
            KeyConflictContext.IN_GAME,
            InputConstants.getKey(InputConstants.KEY_LALT, -1),
            CATEGORY
    );
}
