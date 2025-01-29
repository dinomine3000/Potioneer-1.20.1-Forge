package net.dinomine.potioneer.beyonder.screen;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.Beyonder;
import net.dinomine.potioneer.savedata.PotionRecipeData;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

import java.util.ArrayList;

public class FormulaScreen extends Screen {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/formula_atlas.png");

    private PotionRecipeData data;
    private boolean error;

    private final int imageWidth, imageHeight;
    private int leftPos, topPos;

    public FormulaScreen(PotionRecipeData data, boolean error) {
        super(Component.literal("formula"));
        this.data = data;
        this.error = error;
        this.imageWidth = 512;
        this.imageHeight = 256;
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
    protected void init() {
        super.init();

        this.leftPos = (this.width - 180) / 2;
        this.topPos = (this.height - 200) / 2;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
//        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.blit(TEXTURE, this.leftPos, this.topPos-15, 200, 230, error ? 179 : 0, 0, 178, 220, imageWidth, imageHeight);

        int topOffset = 5;
        //title with potion name
        if(data.id() < 0){
            drawCenteredTextWithScale(pGuiGraphics,
                    Component.translatable("item.potioneer." + PotionRecipeData.getNameById(data.id())),
                    1.5f, this.leftPos + 100, this.topPos + topOffset, 0, false);
        } else {
            drawCenteredTextWithScale(pGuiGraphics,
                    Component.translatable("potioneer.beyonder.sequence." + Beyonder.getSequenceNameFromId(data.id(), false)),
                    1.5f, this.leftPos + 100, this.topPos + topOffset, 0, false);
        }
        topOffset += 15;
        //main ingredients
        drawTextWithScale(pGuiGraphics,
                Component.translatable("potioneer.gui.main_ingredients"), 1.2f, this.leftPos + 10,
                this.topPos + topOffset, 0, false);
        topOffset += 12;
        topOffset += 10*drawIngredients(pGuiGraphics, 1f, data.main(), this.topPos + topOffset);
        //supplementary ingredients
        drawTextWithScale(pGuiGraphics,
                Component.translatable("potioneer.gui.supplementary_ingredients"), 1.2f, this.leftPos + 10, this.topPos + topOffset, 0, false);
        topOffset += 12;
        drawIngredients(pGuiGraphics, 1f, data.supplementary(), this.topPos + topOffset);

        //fire and water status
        drawFire(pGuiGraphics, this.leftPos+30, this.topPos+160, 32, 32, data, pMouseX, pMouseY);
        if(!error)drawWater(pGuiGraphics, this.leftPos+140, this.topPos+160, 32, 32, data, pMouseX, pMouseY);
//        pGuiGraphics.blit(TEXTURE, this.leftPos + 140, this.topPos + 128, 32, 32, 185 + (data.waterLevel() - 1)*32, 0, 32, 32, 512, 512);
//        pGuiGraphics.blit(TEXTURE, this.leftPos + 140, this.topPos + 160, 32, 32, 185 + (data.fire() ? 0 : 1)*32, 32, 32, 32, 512, 512);
    }

    private void drawTextWithScale(GuiGraphics guiGraphics, String name, float scale, int px, int py, int color, boolean dropShadow){
        Matrix4f mat = new Matrix4f(
                scale, 0, 0, 0,
                0, scale, 0, 0,
                0, 0, scale, 0,
                0, 0, 0, scale
        );
        mat = mat.mul(guiGraphics.pose().last().pose());
        minecraft.font.drawInBatch(name, px/scale, py/scale, color, dropShadow,
                mat, guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0,
                15728880, minecraft.font.isBidirectional());
    }

    private void drawTextWithScale(GuiGraphics guiGraphics, Component name, float scale, int px, int py, int color, boolean dropShadow){
        Matrix4f mat = new Matrix4f(
                scale, 0, 0, 0,
                0, scale, 0, 0,
                0, 0, scale, 0,
                0, 0, 0, scale
        );
        mat = mat.mul(guiGraphics.pose().last().pose());
        minecraft.font.drawInBatch(name, px/scale, py/scale, color, dropShadow,
                mat, guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0,
                15728880);
    }

    private void drawCenteredTextWithScale(GuiGraphics guiGraphics, Component name, float scale, int px, int py, int color, boolean dropShadow){
        int offset = (int) ((px - minecraft.font.width(name)*scale / 2f));
        Matrix4f mat = new Matrix4f(
                scale, 0, 0, 0,
                0, scale, 0, 0,
                0, 0, scale, 0,
                0, 0, 0, scale
        );
        mat = mat.mul(guiGraphics.pose().last().pose());
        minecraft.font.drawInBatch(name, offset/scale, py/scale, color, dropShadow,
                mat, guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0,
                15728880);
    }

    private int drawIngredients(GuiGraphics pGuiGraphics, float scale, ArrayList<ItemStack> ingredients, int yOffset){
        int res = 0;
        for(int i = 0; i < ingredients.size(); i++){
            res++;
            drawTextWithScale(pGuiGraphics,
                    Component.literal(String.valueOf(ingredients.get(i).getCount())), scale, this.leftPos + 15,
                    yOffset + i*10, 0, false);
            drawTextWithScale(pGuiGraphics,
                    ingredients.get(i).getItem().getName(ingredients.get(i)), scale, this.leftPos + 25,
                    yOffset + i*10, 0, false);
//            pGuiGraphics.drawString(this.font, Component.literal(String.valueOf(ingredients.get(i).getCount())), this.leftPos + 15, yOffset + i*10, 0, false);
//            pGuiGraphics.drawString(this.font, ingredients.get(i).getItem().getName(ingredients.get(i)), this.leftPos + 25, yOffset + i*10, 0, false);
        }
        return res;
    }

    private void drawFire(GuiGraphics pGuiGraphics, int pX, int pY, int pWidth, int pHeight, PotionRecipeData data, int mouseX, int mouseY){
        pGuiGraphics.blit(TEXTURE, pX, pY, pWidth, pHeight, 400 + (data.fire() ? 0 : 1)*32,
                32, 32, 32, imageWidth, imageHeight);

        Component cp2 = Component.literal(data.fire() ? "Needs heat underneath." : "Doesn't need heat underneath." );
        ArrayList<Component> fire = new ArrayList<>();
        fire.add(cp2);
        if(mouseX > pX && mouseX < pX + 32
                && mouseY > pY && mouseY < pY + 32){
            pGuiGraphics.renderComponentTooltip(font, fire, mouseX , mouseY);
        }
    }

    private void drawWater(GuiGraphics pGuiGraphics, int pX, int pY, int pWidth, int pHeight, PotionRecipeData data, int mouseX, int mouseY){
        pGuiGraphics.blit(TEXTURE, pX, pY, pWidth, pHeight, 400 + (data.waterLevel() - 1)*32, 0,
                32, 32, imageWidth, imageHeight);

        Component cp = Component.literal("Needs " + (data.waterLevel() - 1) + " water bucket" + (data.waterLevel() > 2 ? "s." : "."));
        ArrayList<Component> comp = new ArrayList<>();
        comp.add(cp);
        if(mouseX > pX && mouseX < pX + 32
                && mouseY > pY && mouseY < pY + 32){
            pGuiGraphics.renderComponentTooltip(font, comp, mouseX , mouseY);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
