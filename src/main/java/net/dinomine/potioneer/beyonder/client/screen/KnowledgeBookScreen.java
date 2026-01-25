package net.dinomine.potioneer.beyonder.client.screen;

import ca.weblite.objc.Client;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.client.ClientStatsData;
import net.dinomine.potioneer.beyonder.pages.Page;
import net.dinomine.potioneer.beyonder.pages.PageRegistry;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.recipe.PotionCauldronRecipe;
import net.dinomine.potioneer.util.CustomPlainTextButton;
import net.dinomine.potioneer.util.CustomTextImageButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KnowledgeBookScreen extends Screen {
    private static final Component TITLE = Component.translatable("gui." + Potioneer.MOD_ID + ".knowledge_book");
    private static final ResourceLocation TEXTURE = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/book.png");
    private final int TEXTURE_WIDTH, TEXTURE_HEIGHT;

    private static final int MAX_PAGES = 7;

    private final int imageWidth, imageHeight;
    private int leftPos, topPos;

    private List<Page> pages = new ArrayList<>();
    private boolean onMainPage;
    private Page.Chapter currentChapter;
    private Page currentPage;
    private List<Page.Chapter> knownChapters;

    private static final int SCROLL_HEIGHT = 126;

    private Map<Page.Chapter, CustomTextImageButton> chapterButtons = new LinkedHashMap<>();
    private Map<Page, CustomPlainTextButton> pageButtons = new LinkedHashMap<>();

    private int minWindow, maxWindow;

    public KnowledgeBookScreen() {
        super(TITLE);
        this.TEXTURE_WIDTH = 300;
        this.TEXTURE_HEIGHT = 256;
        this.imageWidth = 292;
        this.imageHeight = 180;
        onMainPage = true;
        ClientStatsData.getCapability().ifPresent(cap -> {
           pages = new ArrayList<>(cap.getPageList().stream().map(PageRegistry::getPageById).toList());
        });
        PageRegistry.addDefaultPages(pages);
        if(ClientStatsData.getCapability().isPresent()){
            LivingEntityBeyonderCapability cap = ClientStatsData.getCapability().get();
            for(Page ablPage: cap.getAbilitiesManager().getPagesFromAbilities()){
                if(!pages.contains(ablPage)) pages.add(ablPage);
            }
        }
        this.minWindow = 0;
        this.maxWindow = Math.min(MAX_PAGES, pages.size());
    }
    
    public KnowledgeBookScreen(int pageId){
        this();
        if(PageRegistry.pageExists(pageId)){
            currentPage = PageRegistry.getPageById(pageId);
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        currentPage = null;
    }

    @Override
    protected void init() {
        super.init();

        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        knownChapters = new ArrayList<>();
        for(Page page: pages){
            if(!knownChapters.contains(page.chapter)) knownChapters.add(page.chapter);
        }

        for(int i = 0; i < pages.size(); i++){
            Page page = pages.get(i);
            CustomPlainTextButton btn = new CustomPlainTextButton(0, 0, 104, 19, page.getTitle(), lamBtn -> openPage(page), this.font, 0xAAAAAA).withDropShadows(true);
            pageButtons.put(page, btn);
            btn.visible = false;
            btn.active = false;
            addRenderableWidget(btn);
        }

        for(int i = 0; i < knownChapters.size(); i++){
            Page.Chapter chapter = knownChapters.get(i);
            CustomTextImageButton btn = new CustomTextImageButton(leftPos + 20 + Math.floorDiv(i, 6)*150, topPos + 15 + 25*(i%6),
                    104, 19, 62, 183, 20, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, lamBtn -> openChapter(chapter))
                    .withText(Component.translatable("chapter.potioneer." + chapter.toString().toLowerCase()), this.font, 0, false);
            chapterButtons.put(chapter, btn);
            addRenderableWidget(btn);
        }
        if(currentPage != null){
            openChapter(currentPage.chapter, true);
        }
    }

    private void openChapter(Page.Chapter chapter){
        openChapter(chapter, onMainPage);
    }
    private void openChapter(Page.Chapter chapter, boolean openChapter){
        onMainPage = !openChapter;
        int i = -1;
        for(Page.Chapter iChapter: chapterButtons.keySet()){
            i++;
            if(iChapter == chapter){
                CustomTextImageButton btn = chapterButtons.get(iChapter);
                btn.setX(leftPos + 20 + (onMainPage ? Math.floorDiv(i, 6)*150 : 0));
                btn.setY(topPos + 15 + (onMainPage ? 25*(i%6) : 0));
                btn.setText(onMainPage ?
                        Component.translatable("chapter.potioneer." + chapter.toString().toLowerCase()) :
                        Component.translatable("chapter.potioneer.return"));
                continue;
            }
            chapterButtons.get(iChapter).visible = onMainPage;
            chapterButtons.get(iChapter).active = onMainPage;
        }
        if(!onMainPage){
            List<Page> pages1 = getPagesOfChapter(chapter);
            if(pages1.isEmpty()) return;
            if(currentPage == null) currentPage = pages1.get(0);
            currentChapter = chapter;
            minWindow = 0;
            maxWindow = Math.min(pages1.size(), MAX_PAGES);
        } else {
            currentPage = null;
        }
    }

    private void openPage(Page page){
        currentPage = page;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if(pKeyCode == 69) {
            this.onClose();
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        pGuiGraphics.blit(TEXTURE, leftPos, topPos, imageWidth, imageHeight, 0,
                0, imageWidth, imageHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        Page page = currentPage;
        if(!onMainPage && page != null && currentChapter != null){
            renderPageButtons(pGuiGraphics);
            pGuiGraphics.drawString(this.font, page.getTitle(), leftPos + 221 - this.font.width(page.getTitle())/2, topPos + 13, 0, false);
            page.draw(pGuiGraphics, TEXTURE, leftPos + 160, topPos + 25, imageWidth, imageHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        } else {
            pageButtons.values().forEach(btn -> {btn.visible = false; btn.active = false;});
        }
    }

    private List<Page> getPagesOfChapter(Page.Chapter chapter){
        return pages.stream().filter(page -> page.chapter == chapter).toList();
    }

    private void renderPageButtons(GuiGraphics pGuiGraphics) {
        List<Page> chapterPages = getPagesOfChapter(currentChapter);
        pageButtons.values().forEach(btn ->{ btn.active = false; btn.visible = false;});
        for(int i = minWindow; i < maxWindow; i++){
            Page page = chapterPages.get(i);
            Button btn = pageButtons.get(page);
            btn.visible = true;
            btn.active = true;
            btn.setX(leftPos + 16);
            btn.setY(topPos + 40 + (i-minWindow)*19);
        }
        if(chapterPages.size() > MAX_PAGES){
            int diff = chapterPages.size() - MAX_PAGES;
            int scrollHeight = SCROLL_HEIGHT/(1 + diff);
            pGuiGraphics.blit(TEXTURE, leftPos + 128, topPos + 40, 3, SCROLL_HEIGHT, 297, 6, 3, 169, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            pGuiGraphics.blit(TEXTURE, leftPos + 128, topPos + 40 + (maxWindow-MAX_PAGES)*scrollHeight, 3, scrollHeight, 294, 6, 3, 10, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if(pDelta == 0) return false;
        if(pMouseX > leftPos + 8 && pMouseX < leftPos + 8 + 133
            && pMouseY > topPos + 35 && pMouseY < topPos + 35 + 140){
            List<Page> pages = getPagesOfChapter(currentChapter);
            if(pages.size() < MAX_PAGES) return false;
            if(pDelta > 0 && minWindow <= 0) return false;
            if(pDelta < 0 && maxWindow >= pages.size()) return false;
            minWindow += pDelta < 0 ? 1 : -1;
            maxWindow += pDelta < 0 ? 1 : -1;
        }
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
