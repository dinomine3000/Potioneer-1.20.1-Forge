package net.dinomine.potioneer.beyonder.screen;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.beyonder.client.ClientStatsData;
import net.dinomine.potioneer.beyonder.client.KeyBindings;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import static net.dinomine.potioneer.beyonder.client.HUD.AbilitiesHotbarHUD.*;

public class BeyonderScreen extends Screen {
    private static final Component TITLE = Component.translatable("gui." + Potioneer.MOD_ID + ".beyonder_menu");
    private static final Component ABILITIES_BUTTON = Component.translatable("gui." + Potioneer.MOD_ID + ".beyonder_menu.abilities_button");
    private static final ResourceLocation TEXTURE = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/beyonder_menu_screen2.png");
    private static final ResourceLocation ICONS = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/ability_icon_atlas.png");
    private static final ResourceLocation PATHWAY_ICONS = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/pathway_icons.png");
    private static final ResourceLocation SPIRITUALITY = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/spirituality.png");

    private final int TEXTURE_WIDTH, TEXTURE_HEIGHT;

    private Component PATHWAY;
    private Component SEQUENCE;
    private Component SEQUENCE_LEVEL;
    private int color;
    private int pathwayId;
    private float tick = 0;

    private final int imageWidth, imageHeight;
    private int leftPos, topPos;

    private Button goToAbilitiesMenu;

    public BeyonderScreen() {
        super(TITLE);
        this.imageWidth = 176;
        this.imageHeight = 183;
        this.TEXTURE_WIDTH = 1100;
        this.TEXTURE_HEIGHT = 262;
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

        Minecraft.getInstance().player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            this.PATHWAY = Component.translatable(Potioneer.MOD_ID + ".beyonder.pathway." + cap.getPathwayName(false));
            this.SEQUENCE = Component.translatable(Potioneer.MOD_ID + ".beyonder.sequence." + cap.getSequenceName(false));
            this.color = cap.getPathwayColor();
            this.pathwayId = Math.floorDiv(cap.getPathwayId(), 10);


            //this.PATHWAY = Component.literal("Path");
            //this.SEQUENCE = Component.literal("sequence");
            //this.color = 0x404080;
            //this.SEQUENCE_LEVEL = Component.literal(String.valueOf(cap.getSequence()));

        });
        /*this.button = addRenderableWidget(
                Button.builder(
                    ABILITIES_BUTTON,
                        btn ->
                )
        )*/
        goToAbilitiesMenu = new ImageButton(leftPos + 47, topPos + 165, 42, 18,
                234, 219, 0, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> {goToAbilities();});
        addRenderableWidget(goToAbilitiesMenu);
    }

    private void goToAbilities(){
        Minecraft.getInstance().setScreen(new BeyonderAbilitiesScreen());
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        //blit pathway-related background
        pGuiGraphics.blit(TEXTURE, leftPos, topPos, imageWidth, imageHeight, (pathwayId + 1)*imageWidth,
                0, imageWidth, imageHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        //blit spirituality fog
        int slowdown = 4;
        tick = (tick + pPartialTick) % (62*slowdown);
        int frame = ((int) (tick / slowdown)) % 31;
        float mana_percent = Mth.clamp(Math.round(100f* ClientStatsData.getPlayerSpirituality() / ClientStatsData.getPlayerMaxSpirituality())/100f, 0, 1);
        float sanity = Mth.clamp(ClientStatsData.getPlayerSanity()/100f, 0f, 1f);
        int sanity_percent = switch((int)(sanity*4)){
            case 0 -> 3;
            case 1 -> 2;
            case 2 -> 1;
            default -> 0;
        };
        int sideLength = 49;
        pGuiGraphics.blit(SPIRITUALITY, leftPos+7, topPos + 7 + (int)(sideLength-mana_percent*sideLength),
                sideLength, (int)(sideLength*mana_percent),
                10 + sanity_percent*64, 10 + frame*64 + (int)((1-mana_percent)*43),
                43, (int)(43*mana_percent),
                256, 1984);


        //blit default overlay
        pGuiGraphics.blit(TEXTURE, leftPos, topPos, imageWidth, imageHeight, 0,
                0, imageWidth, imageHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        //blit pathway icon
        pGuiGraphics.blit(PATHWAY_ICONS, leftPos, topPos + 1, 64, 64, (pathwayId%4)*64,
                Math.floorDiv(pathwayId, 4)*64, 64, 64, 256, 128);

        //blit spirituality percent overlay
        if(pMouseX > leftPos + 7 && pMouseX < leftPos + 56
                && pMouseY > topPos + 7 && pMouseY < topPos + 56){
            String metric = Math.round(ClientStatsData.getPlayerSpirituality()) + " / " + ClientStatsData.getPlayerMaxSpirituality();
            int diff = this.font.width(metric)/2;
            pGuiGraphics.fillGradient(leftPos + 30 - diff, topPos + 48, leftPos + 33 + diff, topPos + 59, 0x00101010, 0x30000000 + 0x101010);
            pGuiGraphics.drawString(this.font, metric,
                    leftPos + 32 - diff, topPos + 50, this.color, false);
        }

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        float scale = Math.min(1, 106f/this.font.width(PATHWAY.getString()));
        drawScaledString(pGuiGraphics, this.font, PATHWAY.getString(), this.leftPos + 64, this.topPos + 22, scale, this.color);
        //pGuiGraphics.drawString(this.font, PATHWAY, this.leftPos + 64, this.topPos + 10, this.color, false);
        pGuiGraphics.drawString(this.font, Minecraft.getInstance().player.getDisplayName(), this.leftPos + 64, this.topPos + 10, 0x707070, false);
        pGuiGraphics.drawWordWrap(this.font, SEQUENCE, this.leftPos + 63, this.topPos + 45, 100, 0xFF909090);

        if(!ClientAbilitiesData.getHotbar().isEmpty()){
            for(int i = 0; i < Math.min(3, ClientAbilitiesData.getHotbar().size()); i++){
                drawAbilityIcon(pGuiGraphics, leftPos + 8 + 15*i, topPos + 130, 0.65f, ClientAbilitiesData.getAbilityAt(i));
            }
        }

        // draw little human guy
        int x = this.leftPos + 33;
        int y = this.topPos + 120;
        Player player = Minecraft.getInstance().player;
        InventoryScreen.renderEntityInInventoryFollowsMouse(
                pGuiGraphics, x, y, 28,
                (float) (x - pMouseX),
                (float) (y - pMouseY),
                player
        );

        //draw stats
        pGuiGraphics.drawWordWrap(this.font, Component.translatable("potioneer.beyonder.stat_hp").append(" " + ClientStatsData.getStat(0)), leftPos + 63, topPos + 67, 100, this.color);
        pGuiGraphics.drawWordWrap(this.font, Component.translatable("potioneer.beyonder.stat_dmg").append(" " + ClientStatsData.getStat(1)), leftPos + 63, topPos + 80, 100, this.color);
        pGuiGraphics.drawWordWrap(this.font, Component.translatable("potioneer.beyonder.stat_armor").append(" " + ClientStatsData.getStat(2)), leftPos + 63, topPos + 93, 100, this.color);
        pGuiGraphics.drawWordWrap(this.font, Component.translatable("potioneer.beyonder.stat_tough").append(" " + ClientStatsData.getStat(3)), leftPos + 63, topPos + 106, 100, this.color);
        pGuiGraphics.drawWordWrap(this.font, Component.translatable("potioneer.beyonder.stat_knock").append(" " + ClientStatsData.getStat(4)), leftPos + 63, topPos + 119, 100, this.color);
    }

    public void drawScaledString(GuiGraphics guiGraphics, Font font, String text, int x, int y, float scale, int color) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0);
        guiGraphics.pose().scale(scale, scale, 1f); // Scale X and Y
        guiGraphics.drawString(font, text, 0, 0, color, false);
        guiGraphics.pose().popPose();
    }

    private static void drawAbilityIcon(GuiGraphics pGuiGraphics, int pX, int pY, float scale, AbilityInfo info){
        pGuiGraphics.blit(ICONS, pX + (int) (5*scale), pY + (int)(4*scale), (int)(ICON_WIDTH*scale), (int)(ICON_HEIGHT*scale), info.posX(), info.posY(), ICON_WIDTH, ICON_HEIGHT, ICONS_WIDTH, ICONS_HEIGHT);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
