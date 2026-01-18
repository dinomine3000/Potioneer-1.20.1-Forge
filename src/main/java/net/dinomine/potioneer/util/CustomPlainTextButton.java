package net.dinomine.potioneer.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;

public class CustomPlainTextButton extends Button {
    private final Font font;
    private final Component message;
    private final Component underlinedMessage;
    private final Integer customColor;
    private boolean dropShadows = true;

    public CustomPlainTextButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress, Font pFont) {
        this(pX, pY, pWidth, pHeight, pMessage, pOnPress, pFont, null);
    }

    public CustomPlainTextButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress, Font pFont, Integer color) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress, DEFAULT_NARRATION);
        this.font = pFont;
        this.message = pMessage;
        this.underlinedMessage = ComponentUtils.mergeStyles(pMessage.copy(), Style.EMPTY.withUnderlined(true));
        this.customColor = color;
    }

    public CustomPlainTextButton withDropShadows(boolean doDropShadows){
        this.dropShadows = doDropShadows;
        return this;
    }

    @Override
    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        Component $$4 = this.isHoveredOrFocused() ? this.underlinedMessage : this.message;
        pGuiGraphics.drawString(this.font, $$4, this.getX(), this.getY(), customColor == null ? 16777215 | Mth.ceil(this.alpha * 255.0F) << 24 : customColor, dropShadows);
    }
}
