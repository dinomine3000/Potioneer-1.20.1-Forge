package net.dinomine.potioneer.beyonder.pages;

import net.dinomine.potioneer.Potioneer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class PageRegistry {
    private static ResourceLocation ASSET_ATLAS = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/page_assets_atlas.png");
    private static HashMap<Integer, Page> PAGES = new HashMap<>();
    private static int i = 1;

    public static Page INTRO_PAGE = register(i++, () -> new TextPage(Page.Chapter.BEYONDER, Component.translatable("page.potioneer.introduction"), Component.translatable("contents.potioneer.introduction")));
    public static Page FORMULA_PAGE = register(i++, () -> new ImagePage(Page.Chapter.BEYONDER, Component.translatable("page.potioneer.formula"), Component.translatable("contents.potioneer.formulaTop"), Component.translatable("contents.potioneer.formulaBottom"), ASSET_ATLAS, 0, 0, 16, 16).withScale(2f));
    public static Page CAULDRON_PAGE = register(i++, () -> new CraftingTableRecipePage(Page.Chapter.BEYONDER, Component.translatable("page.potioneer.cauldron"), Component.translatable("contents.potioneer.cauldronTop"), Component.translatable("contents.potioneer.cauldronBottom"), new ResourceLocation(Potioneer.MOD_ID, "potion_cauldron")));
    public static Page INGREDIENTS_PAGE = register(i++, () -> new TextPage(Page.Chapter.BEYONDER, Component.translatable("page.potioneer.ingredients"), Component.translatable("contents.potioneer.ingredients")));
    public static Page SPIRITUALITY_PAGE = register(i++, () -> new TextPage(Page.Chapter.BEYONDER, Component.translatable("page.potioneer.spirituality"), Component.translatable("contents.potioneer.spirituality")));
    public static Page SANITY_PAGE = register(i++, () -> new TextPage(Page.Chapter.BEYONDER, Component.translatable("page.potioneer.sanity"), Component.translatable("contents.potioneer.sanity")));
    public static Page ADVANCING_PAGE = register(i++, () -> new TextPage(Page.Chapter.BEYONDER, Component.translatable("page.potioneer.advancing"), Component.translatable("contents.potioneer.advancing")));
    public static Page ACTING_PAGE = register(i++, () -> new TextPage(Page.Chapter.BEYONDER, Component.translatable("page.potioneer.acting"), Component.translatable("contents.potioneer.acting")));
    public static Page CHARACTERISTIC_PAGE = register(i++, () -> new TextPage(Page.Chapter.CHARACTERISTICS, Component.translatable("page.potioneer.characteristic"), Component.translatable("contents.potioneer.characteristic")));

    public static List<Page> addDefaultPages(List<Page> pages){
        if(!pages.contains(PageRegistry.INTRO_PAGE)) pages.add(PageRegistry.INTRO_PAGE);
        if(!pages.contains(PageRegistry.FORMULA_PAGE)) pages.add(PageRegistry.FORMULA_PAGE);
        if(!pages.contains(PageRegistry.CAULDRON_PAGE)) pages.add(PageRegistry.CAULDRON_PAGE);
        if(!pages.contains(PageRegistry.INGREDIENTS_PAGE)) pages.add(PageRegistry.INGREDIENTS_PAGE);
        if(!pages.contains(PageRegistry.SPIRITUALITY_PAGE)) pages.add(PageRegistry.SPIRITUALITY_PAGE);
        if(!pages.contains(PageRegistry.SANITY_PAGE)) pages.add(PageRegistry.SANITY_PAGE);
        if(!pages.contains(PageRegistry.ADVANCING_PAGE)) pages.add(PageRegistry.ADVANCING_PAGE);
        return pages;
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
}
