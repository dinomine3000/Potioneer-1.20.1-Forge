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

    private final int imageWidth, imageHeight;
    private int leftPos, topPos;

    public FormulaScreen(PotionRecipeData data) {
        super(Component.literal("formula"));
        this.data = data;
        this.imageWidth = 200;
        this.imageHeight = 200;
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

        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height- this.imageHeight) / 2;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
//        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 200, 200, 0, 0, 200, 200, 512, 512);

        int topOffset = 10;
        //title with sequence name
        drawCenteredTextWithScale(pGuiGraphics,
                Component.translatable("potioneer.beyonder.sequence." + Beyonder.getSequenceNameFromId(data.id(), false)),
                1.5f, this.leftPos + this.imageWidth/2, this.topPos + topOffset, 0, false);
        topOffset += 25;
        //main ingredients
        drawTextWithScale(pGuiGraphics,
                Component.translatable("potioneer.gui.main_ingredients"), 1.2f, this.leftPos + 10,
                this.topPos + topOffset, 0, false);
        topOffset += 12;
        topOffset += 10*drawIngredients(pGuiGraphics, data.main(), this.topPos + topOffset);
        //supplementary ingredients
        drawTextWithScale(pGuiGraphics,
                Component.translatable("potioneer.gui.supplementary_ingredients"), 1.2f, this.leftPos + 10, this.topPos + topOffset, 0, false);
        topOffset += 12;
        drawIngredients(pGuiGraphics, data.supplementary(), this.topPos + topOffset);
        //fire and water status
        pGuiGraphics.blit(TEXTURE, this.leftPos + 150, this.topPos + 160, 25, 19, 201 + (data.waterLevel() - 1)*30, 0, 25, 19, 512, 512);
        pGuiGraphics.blit(TEXTURE, this.leftPos + 50, this.topPos + 160, 25, 19, 201 + (data.fire() ? 1 : 0)*30, 20, 25, 19, 512, 512);
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

    private int drawIngredients(GuiGraphics pGuiGraphics, ArrayList<ItemStack> ingredients, int yOffset){
        int res = 0;
        for(int i = 0; i < ingredients.size(); i++){
            res++;
            pGuiGraphics.drawString(this.font, Component.literal(String.valueOf(ingredients.get(i).getCount())), this.leftPos + 15, yOffset + i*10, 0, false);
            pGuiGraphics.drawString(this.font, ingredients.get(i).getItem().getName(ingredients.get(i)), this.leftPos + 25, yOffset + i*10, 0, false);
        }
        return res;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
