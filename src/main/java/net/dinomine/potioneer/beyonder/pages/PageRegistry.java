package net.dinomine.potioneer.beyonder.pages;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.block.ModBlocks;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.rituals.spirits.defaultGods.TyrantResponse;
import net.dinomine.potioneer.rituals.spirits.defaultGods.WheelOfFortuneResponse;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class PageRegistry {
    private static ResourceLocation ASSET_ATLAS = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/page_assets_atlas.png");
    private static HashMap<Integer, Page> PAGES = new HashMap<>();
    private static int i = 1;

    public static Page INTRO_PAGE = register(i++, () -> new TextPage(Page.Chapter.BEYONDER, "introduction"));
    public static Page FORMULA_PAGE = register(i++, () -> new ItemPage(Page.Chapter.BEYONDER, "formula", ModItems.FORMULA.get()));
    public static Page CAULDRON_PAGE = register(i++, () -> new CraftingTableRecipePage(Page.Chapter.BEYONDER, "cauldron", new ResourceLocation(Potioneer.MOD_ID, "potion_cauldron")));
    public static Page INGREDIENTS_PAGE = register(i++, () -> new TextPage(Page.Chapter.BEYONDER, "ingredients"));
    public static Page SPIRITUALITY_PAGE = register(i++, () -> new TextPage(Page.Chapter.BEYONDER, "spirituality"));
    public static Page SANITY_PAGE = register(i++, () -> new TextPage(Page.Chapter.BEYONDER, "sanity"));
    public static Page ADVANCING_PAGE = register(i++, () -> new TextPage(Page.Chapter.BEYONDER, "advancing"));
    public static Page ACTING_PAGE = register(i++, () -> new TextPage(Page.Chapter.BEYONDER, "acting"));
    public static Page SWITCHING_PAGE = register(i++, () -> new TextPage(Page.Chapter.BEYONDER, "mistakes"));
    public static Page CHARACTERISTIC_PAGE = register(i++, () -> new TextPage(Page.Chapter.CHARACTERISTICS, "characteristic"));
    public static Page LADY_FATE_1 = register(i++, () -> new TextPage(Page.Chapter.KNOWN_DEITIES, Component.translatable("page.potioneer.lady_fate"), Component.translatable("contents.potioneer.lady_fate", WheelOfFortuneResponse.PRAYER)));
    public static Page LADY_FATE_2 = register(i++, () -> new TextPage(Page.Chapter.KNOWN_DEITIES, "lady_fate_2"));
    public static Page KING_OF_HEROES_1 = register(i++, () -> new TextPage(Page.Chapter.KNOWN_DEITIES, Component.translatable("page.potioneer.king_of_heroes"), Component.translatable("contents.potioneer.king_of_heroes", TyrantResponse.PRAYER)));
    public static Page KING_OF_HEROES_2 = register(i++, () -> new TextPage(Page.Chapter.KNOWN_DEITIES, "king_of_heroes_2"));
    public static Page CHARM_TEMP_LUCK = register(i++, () -> new RitualRecipePage(Page.Chapter.CHARMS, Component.translatable("beyondereffect.potioneer.temp_luck"), "temp_luck_charm"));
    public static Page CHARM_INSTANT_LUCK = register(i++, () -> new RitualRecipePage(Page.Chapter.CHARMS, Component.translatable("beyondereffect.potioneer.instant_luck"), "instant_luck_charm"));
    public static Page RITUALS_CANDLES = register(i++, () -> new ItemPage(Page.Chapter.RITUALS, "rituals_candles", Items.CANDLE).withOffset(10));
    public static Page RITUALS_INK = register(i++, () -> new ItemPage(Page.Chapter.RITUALS, "rituals_ink", ModItems.INK_BOTTLE.get()).withOffset(10));
    public static Page RITUALS_DAGGER = register(i++, () -> new CraftingTableRecipePage(Page.Chapter.RITUALS, "rituals_dagger", new ResourceLocation(Potioneer.MOD_ID, "ritualistic_dagger")));
    public static Page RITUALS_OFFERINGS = register(i++, () -> new ItemPage(Page.Chapter.RITUALS, "rituals_offerings", ModBlocks.RITUAL_PEDESTAL.get().asItem()).withOffset(10));
    public static Page CHARMS_CRAFTING = register(i++, () -> new TextPage(Page.Chapter.CHARMS, "charm_101"));

    public static ArrayList<Integer> DEFAULT_PAGES = new ArrayList<>();
    static{
        DEFAULT_PAGES.add(1);
        DEFAULT_PAGES.add(2);
        DEFAULT_PAGES.add(3);
        DEFAULT_PAGES.add(4);
        DEFAULT_PAGES.add(5);
        DEFAULT_PAGES.add(6);
        DEFAULT_PAGES.add(7);
    }

    public static int getIdOfPage(Page page){
        return PAGES.keySet().stream().filter(key -> PAGES.get(key).equals(page)).findFirst().orElse(-1);
    }

    public static List<Integer> getIdOfPages(List<Page> pages){
        return PAGES.keySet().stream().filter(key -> pages.contains(PAGES.get(key))).toList();
    }

    public static List<Page> addDefaultPages(List<Page> pagesIn){
        pagesIn.addAll(DEFAULT_PAGES.stream().map(PageRegistry::getPageById).filter(page -> !pagesIn.contains(page)).toList());
        return pagesIn;
    }

    public static Page register(int numKey, Supplier<Page> constructor){
        PAGES.put(numKey, constructor.get());
        return constructor.get();
    }

    public static boolean pageExists(int pageId){
        return PAGES.containsKey(pageId);
    }

    public static Page getPageById(int pageId){
        return PAGES.get(pageId);
    }

    public static List<Integer> getAllKeys(){
        return PAGES.keySet().stream().toList();
    }

    public static List<Integer> getAllNonDefaultKeys(){
        return PAGES.keySet().stream().filter(pageId -> !DEFAULT_PAGES.contains(pageId)).toList();
    }

    public static List<Integer> getNewKeys(List<Integer> knownPages){
        return PAGES.keySet().stream().filter(key -> !knownPages.contains(key) && !DEFAULT_PAGES.contains(key)).toList();
    }
}
