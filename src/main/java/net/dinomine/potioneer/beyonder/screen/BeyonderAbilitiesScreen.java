package net.dinomine.potioneer.beyonder.screen;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.beyonder.client.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.LoomScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Supplier;

import static net.dinomine.potioneer.beyonder.client.HUD.AbilitiesHotbarHUD.*;

public class BeyonderAbilitiesScreen extends Screen {
    private static final Component TITLE = Component.translatable("gui." + Potioneer.MOD_ID + ".beyonder_menu");
    private static final ResourceLocation TEXTURE = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/ability_gui_dino.png");
    private static final ResourceLocation ABILITY_ICONS = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/ability_icon_atlas.png");

    private final int imageWidth, imageHeight;
    private final int TEXTURE_WIDTH, TEXTURE_HEIGHT;
    private int leftPos, topPos;

    private boolean beyonder;
    private int selectedCaret;
    private int buttonOffset;
    private boolean dragging;
    private ArrayList<AbilityInfo> abilities;
    private ArrayList<ImageButton> buttons;
    private float dClickCountdown = 0;

    private Button addToHotbarButton;
    private Button removeFromHotbarButton;

    public BeyonderAbilitiesScreen() {
        super(TITLE);
        this.imageWidth = 176;
        this.imageHeight = 183;
        this.TEXTURE_WIDTH = 214;
        this.TEXTURE_HEIGHT = 226;
        selectedCaret = 0;
        buttonOffset = 0;
        dragging = false;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if(pKeyCode == 69 || pKeyCode == KeyBindings.INSTANCE.beyonderMenuKey.getKey().getValue()) {
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
        abilities = new ArrayList<>(ClientAbilitiesData.getAbilities());
        beyonder = !abilities.isEmpty();
        Collections.reverse(abilities);
        buttons = new ArrayList<>();
        if(beyonder){
            for(int i = 0; i < Math.min(6, abilities.size()); i++){
                createButtons(i);
            }
            ImageButton castAbilityButton = new ImageButton(leftPos + 12, topPos + 13, 38, 46, 176, 13,
                    46, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, hbut -> {
                castAbilityAt();
            });
            addRenderableWidget(castAbilityButton);

            addToHotbarButton = new ImageButton(leftPos + 145, topPos + 50, 18, 18,
                    176, 105, 18, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> {addAbilityToHotbar();});
            addToHotbarButton.setTooltip(Tooltip.create(Component.literal("Add to hotbar")));
            removeFromHotbarButton = new ImageButton(leftPos + 145, topPos + 50, 18, 18,
                    194, 105, 18, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> {addAbilityToHotbar();});
            removeFromHotbarButton.setTooltip(Tooltip.create(Component.literal("Remove from hotbar")));
            addRenderableWidget(addToHotbarButton);
            addRenderableWidget(removeFromHotbarButton);
            updateHotbarButton();
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        if(dClickCountdown > 0){
            dClickCountdown -= pPartialTick;
        }
        pGuiGraphics.blit(TEXTURE, leftPos, topPos, imageWidth, imageHeight, 0,
                0, imageWidth, imageHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        if(beyonder){
            drawAbilityIcon(pGuiGraphics, leftPos + 19, topPos + 18, 1.5f, selectedCaret, true);
            renderExtraButtonItems(pGuiGraphics);
        }
        drawScroll(pGuiGraphics, leftPos + 158, topPos + 73);
    }

    private void createButtons(int i){
        ImageButton btn = new ImageButton(leftPos + 6, topPos + 74 + i*14, 151, 14, 14, 183,
                14, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, hbut -> {
            changeCaret(i);
        });
        addRenderableWidget(btn);
        buttons.add(btn);
    }

    private void addAbilityToHotbar(){
        int caretToAdd = abilities.size() - 1 - selectedCaret;
        if(!ClientAbilitiesData.getHotbar().contains(caretToAdd)){
            ClientAbilitiesData.getHotbar().add(caretToAdd);
        } else {
            ClientAbilitiesData.getHotbar().remove((Object) caretToAdd);
        }
        ClientAbilitiesData.updateCaret();
        updateHotbarButton();
        ClientAbilitiesData.setHotbarChanged();
    }

    private void updateHotbarButton(){
        int abilityCaret = abilities.size() - 1 - selectedCaret;
        if(!ClientAbilitiesData.getHotbar().contains(abilityCaret)){
            addToHotbarButton.active = true;
            addToHotbarButton.visible = true;
            removeFromHotbarButton.active = false;
            removeFromHotbarButton.visible = false;
        } else {
            addToHotbarButton.active = false;
            addToHotbarButton.visible = false;
            removeFromHotbarButton.active = true;
            removeFromHotbarButton.visible = true;
        }
    }

    private void castAbilityAt(){
        ClientAbilitiesData.useAbility(Minecraft.getInstance().player, abilities.size() - 1 - selectedCaret, false);
    }

    private void changeCaret(int buttonIdx){
        if(selectedCaret == buttonIdx + buttonOffset && dClickCountdown > 0){
            castAbilityAt();
            dClickCountdown = 0;
        } else {
            this.selectedCaret = buttonIdx + buttonOffset;
            dClickCountdown = 7;
        }
        updateHotbarButton();
    }

    private void drawAbilityIcon(GuiGraphics pGuiGraphics, int posX, int posY, float scale, int abilityIndex, boolean main){
        AbilityInfo data = abilities.get(abilityIndex);

        //name title
        if(main){
            Component name = Component.literal(data.name());
            pGuiGraphics.drawString(this.font, name, leftPos + 20 + imageWidth/2 - this.font.width(name)/2, topPos + 5, 0, false);
        }

        //cooldown gradient
        if(main){
            int caret = abilities.size() - 1 - abilityIndex;
            float percent = Mth.clamp(1 - ((float) ClientAbilitiesData.getCooldown(caret, false) / ClientAbilitiesData.getMaxCooldown(caret, false)),
                    0, 1);

            pGuiGraphics.fillGradient(posX - 7, (int) (posY - 5 + percent*46),
                    (int) (posX + 7 + scale*ICON_WIDTH), (int) (posY + 5 + ICON_HEIGHT*scale), 0xDD696969, 0xDD424242);

        }
        //icon itself
        pGuiGraphics.blit(ABILITY_ICONS, posX, posY, (int) (scale * ICON_WIDTH), (int)(scale * ICON_HEIGHT),
                data.posX(), data.posY(), ICON_WIDTH, ICON_HEIGHT, ICONS_WIDTH, ICONS_HEIGHT);

        //enabled gradient
        if(main){
            //enabled gradient
            int caret = abilities.size() - 1 - abilityIndex;
            if(!ClientAbilitiesData.isEnabled(caret, false)){
                pGuiGraphics.fillGradient(posX - 7, posY -5,
                        (int) (posX + 7 + scale*ICON_WIDTH), (int) (posY + 5 + ICON_HEIGHT*scale), 0xDD999999, 0xDD666666);
            }
            //barrier symbol if ability is disabled
            if(ClientAbilitiesData.getCooldown(caret, false) < 0){
                //Copied from the icons part
                pGuiGraphics.blit(ABILITY_ICONS, posX, posY,
                        (int)(ICON_WIDTH*scale), (int)(ICON_HEIGHT*scale), 130, 4, ICON_WIDTH, ICON_HEIGHT, ICONS_WIDTH, ICONS_HEIGHT);
            }
        }
    }

    private void renderExtraButtonItems(GuiGraphics pGuiGraphics){
        for(int i = 0; i < Math.min(abilities.size(), 6); i++){
            pGuiGraphics.fillGradient(leftPos + 9, topPos + 75 + i*14, leftPos + 19, topPos + 87 + i*14,
                    0x22000000, 0x11000000);

            drawAbilityIcon(pGuiGraphics, leftPos + 10, topPos + 75 + i*14, 0.5f, i + buttonOffset, false);
            pGuiGraphics.drawString(this.font, Component.literal(abilities.get(i + buttonOffset).name()),
                    leftPos + 22, topPos + 77 + i*14, 0, false);
        }
    }

    private void drawScroll(GuiGraphics pGuiGraphics, int posX, int posY){
        int size = abilities.size();
        float percent = size > 6 ? (float) buttonOffset / (size - 6) : 0;
        pGuiGraphics.blit(TEXTURE, posX, (int)(posY + percent*71), 12, 15,
                size > 6 ? 0 : 12, 211, 12, 15, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        dragging = false;
        if(pMouseX > leftPos + 156 && pMouseX < leftPos + 171 && pMouseY > topPos + 71 && pMouseY < topPos + 160 && abilities.size() > 6){
            dragging = true;
            float interval = (float) 1 /(abilities.size() - 6);
            float mousePercent = Mth.clamp((float) (pMouseY - topPos - 72) / 88f, 0, 1);
            int offset = Math.round(mousePercent / interval);
            if(offset > abilities.size() - 6){
                System.out.println("Offset was overcome. normalizing it...");
                offset = abilities.size() - 6;
            }
            buttonOffset = offset;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if(dragging && abilities.size() > 6){
            float interval = (float) 1 /(abilities.size() - 6);
            float mousePercent = Mth.clamp((float) (pMouseY - topPos - 72) / 88f, 0, 1);
            int offset = Math.round(mousePercent / interval);
            if(offset > abilities.size() - 6){
                System.out.println("Offset was overcome. normalizing it...");
                offset = abilities.size() - 6;
            }
            buttonOffset = offset;
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if(abilities.size() > 6){
            buttonOffset = Mth.clamp(buttonOffset + (int)(-pDelta), 0, abilities.size() - 6);
        }
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }
}
