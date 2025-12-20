package net.dinomine.potioneer.beyonder.client.screen;

import com.mojang.authlib.GameProfile;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.client.ClientAllyData;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.AllySystem.AllyChangeMessageC2S;
import net.dinomine.potioneer.network.messages.AllySystem.AllyGroupSyncMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.Map;
import java.util.UUID;

public class BeyonderAllyScreen extends Screen {
    private static Component TITLE = Component.translatable("gui.potioneer.ally_menu");
    private static final ResourceLocation TEXTURE = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/ally_gui.png");

    private final int TEXTURE_WIDTH, TEXTURE_HEIGHT;
    private final int imageWidth, imageHeight;
    private int leftPos, topPos;

    private Button goToMainMenu, goToAbilitiesMenu, goToOptionsMenu;
    private Button createGroupButton, joinGroupButton;
    private EditBox groupName, groupPassword;
    private Button groupBtn1, groupBtn2, groupBtn3;
    private Button leaveGroup1, leaveGroup2, leaveGroup3;

    private String NAME_SUGGESTION = Component.translatable("gui.potioneer.group_name_suggestion").getString();
    private String PASSWORD_SUGGESTION = Component.translatable("gui.potioneer.group_password_suggestion").getString();

    private boolean draggingGroups = false;
    private boolean draggingPlayers = false;
    private int playerSliderY, groupSliderY;

    int groupOffset = 0;
    int playerOffset = 0;
    int groupRange = 110 - 68 - 15;
    int playerRange = groupRange;

    public BeyonderAllyScreen() {
        super(TITLE);
        this.imageWidth = 176;
        this.imageHeight = 183;
        TEXTURE_WIDTH = 214;
        TEXTURE_HEIGHT = 239;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if(pKeyCode == 69 && !groupName.isFocused() && !groupPassword.isFocused()) {
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
        this.groupSliderY = 0;
        this.playerSliderY = 0;
        ClientAllyData.requestGroups();

        //sub buttons
        goToAbilitiesMenu = new ImageButton(leftPos + 47, topPos + 165, 42, 18,
                234, 219, 0, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> {BeyonderScreen.goToAbilities();});
        addRenderableWidget(goToAbilitiesMenu);
        goToOptionsMenu = new ImageButton(leftPos + 89, topPos + 165, 42, 18,
                234, 219, 0, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> {BeyonderScreen.goToOptionsMenu();});
        addRenderableWidget(goToOptionsMenu);
        goToMainMenu = new ImageButton(leftPos + 4, topPos + 165, 43, 18,
                163, 208, 0, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> {BeyonderScreen.goToMainMenu();});
        addRenderableWidget(goToMainMenu);

        //group join and create
        createGroupButton = new ImageButton(leftPos + 141, topPos + 23, 18, 18,
                176, 105, 18, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> { createGroup();});
        addRenderableWidget(createGroupButton);
        createGroupButton.setTooltip(Tooltip.create(Component.translatable("gui.potioneer.create_group")));
        joinGroupButton = new ImageButton(leftPos + 141, topPos + 43, 18, 18,
                194, 105, 18, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> { joinGroup();});
        joinGroupButton.setTooltip(Tooltip.create(Component.translatable("gui.potioneer.join_group")));
        addRenderableWidget(joinGroupButton);

        groupName = new EditBox(this.font, leftPos + 8, topPos + 25, 130, 14, Component.translatable("gui.potioneer.group_name"));
        groupName.setSuggestion(NAME_SUGGESTION);
        groupName.setMaxLength(120);
        addRenderableWidget(groupName);
        groupPassword = new EditBox(this.font, leftPos + 8, topPos + 45, 130, 14, Component.translatable("gui.potioneer.group_password"));
        groupPassword.setSuggestion(PASSWORD_SUGGESTION);
        groupPassword.setMaxLength(120);
        addRenderableWidget(groupPassword);

        groupBtn1 = new ImageButton(leftPos + 6, topPos + 68, 135, 14,
                14, 211, 14, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> { showPlayersForGroup(btn, 0);});
        addRenderableWidget(groupBtn1);
        groupBtn2 = new ImageButton(leftPos + 6, topPos + 82, 135, 14,
                14, 211, 14, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> { showPlayersForGroup(btn, 1);});
        addRenderableWidget(groupBtn2);
        groupBtn3 = new ImageButton(leftPos + 6, topPos + 96, 135, 14,
                14, 211, 14, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> { showPlayersForGroup(btn, 2);});
        addRenderableWidget(groupBtn3);

        leaveGroup1 = new ImageButton(leftPos + 141, topPos + 68, 14, 14,
                149, 211, 14, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> { leaveGroup(0);});
        addRenderableWidget(leaveGroup1);
        leaveGroup2 = new ImageButton(leftPos + 141, topPos + 82, 14, 14,
                149, 211, 14, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> { leaveGroup(1);});
        addRenderableWidget(leaveGroup2);
        leaveGroup3 = new ImageButton(leftPos + 141, topPos + 96, 14, 14,
                149, 211, 14, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> { leaveGroup(2);});
        leaveGroup1.setTooltip(Tooltip.create(Component.translatable("gui.potioneer.leave_group")));
        leaveGroup2.setTooltip(Tooltip.create(Component.translatable("gui.potioneer.leave_group")));
        leaveGroup3.setTooltip(Tooltip.create(Component.translatable("gui.potioneer.leave_group")));
        addRenderableWidget(leaveGroup3);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderBackground(pGuiGraphics);
        pGuiGraphics.blit(TEXTURE, leftPos, topPos, imageWidth, imageHeight, 0,
                0, imageWidth, imageHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        //render title
        pGuiGraphics.drawString(this.font, TITLE, width/2 - this.font.width(TITLE)/2, topPos + 10, 0, false);

        //update text input suggestions
        if(groupName.getValue().isEmpty()){
            groupName.setSuggestion(NAME_SUGGESTION);
        } else {
            groupName.setSuggestion("");
        }
        if(groupPassword.getValue().isEmpty()){
            groupPassword.setSuggestion(PASSWORD_SUGGESTION);
        } else {
            groupPassword.setSuggestion("");
        }

        //create group button
        createGroupButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        joinGroupButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        groupName.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        groupPassword.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        //individual group buttons
        disableButtons(3);
        disableButtons(2);
        disableButtons(1);
        switch (ClientAllyData.getGroupsSize()){
            case 3:
                enableButtons(3);
                groupBtn3.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
                pGuiGraphics.drawString(this.font, ClientAllyData.getGroupNameForRender(2 + groupOffset), leftPos + 9, topPos + 99, 0, false);
                leaveGroup3.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            case 2:
                enableButtons(2);
                groupBtn2.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
                pGuiGraphics.drawString(this.font, ClientAllyData.getGroupNameForRender(1 + groupOffset), leftPos + 9, topPos + 85, 0, false);
                leaveGroup2.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            case 1:
                enableButtons(1);
                groupBtn1.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
                pGuiGraphics.drawString(this.font, ClientAllyData.getGroupNameForRender(groupOffset), leftPos + 9, topPos + 71, 0, false);
                leaveGroup1.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        for(int i = 0; i < ClientAllyData.getPlayerNumber(); i++){
            Map.Entry<UUID, String> entry = ClientAllyData.getEntryAt(i + playerOffset);
            if(entry == null) continue;
            pGuiGraphics.blit(TEXTURE, leftPos + 6, topPos + 116 + 14*i, 149, 14, 14,
                    183, 149, 14, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            pGuiGraphics.blit(getPlayerSkin(entry.getKey(), entry.getValue()), leftPos + 9, topPos + 119 + 14*i, 8, 8, 8,
                    8, 8, 8, 64, 64);
            pGuiGraphics.drawString(this.font, entry.getValue(), leftPos + 19, topPos + 119 + 14*i, 0, false);
        }

        //render sliders
        boolean enableGroupSlider = shouldMoveGroupSlider();
        boolean enablePlayerSlider = shouldMovePlayerSlider();
        pGuiGraphics.blit(TEXTURE, leftPos + 158, topPos + 68 + groupSliderY, 12, 15, enableGroupSlider ? 177 : 189,
                194, 12, 15, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        pGuiGraphics.blit(TEXTURE, leftPos + 158, topPos + 116 + playerSliderY, 12, 15, enablePlayerSlider ? 177 : 189,
                194, 12, 15, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if(pMouseX > leftPos + 157 && pMouseX < leftPos + 171
                && pMouseY > topPos + 67 && pMouseY < topPos + 159){
            if(shouldMoveGroupSlider() && pMouseY < topPos + 111){
                draggingGroups = true;
                return true;
            }
            if(pMouseY <= topPos + 115) return false;
            if(shouldMovePlayerSlider()){
                draggingPlayers = true;
                return true;
            }
            return false;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if(draggingGroups){
            double yLevel = pMouseY - topPos - 67 - 7;
            double percent = Mth.clamp(yLevel/groupRange, 0, 1);
            groupSliderY = (int) (percent * groupRange);
            groupOffset = (int) Math.floor(percent*(ClientAllyData.getTotalGroupSize()-3));
            return true;
        } else if(draggingPlayers){
            double yLevel = pMouseY - topPos - 115;
            double percent = Mth.clamp(yLevel/playerRange, 0, 1);
            playerSliderY = (int) (percent*playerRange);
            playerOffset = (int) Math.floor(percent*(ClientAllyData.getTotalPlayerNumber()-3));
            return true;
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if(shouldMoveGroupSlider() && pMouseY > topPos + 64 && pMouseY < topPos + 113){
            groupOffset = (int) Mth.clamp(groupOffset - pDelta, 0, Math.max(ClientAllyData.getTotalGroupSize() - 3, 0));
            groupSliderY = (int) (groupRange*(groupOffset / (float) (ClientAllyData.getTotalGroupSize()-3)));
            return true;
        }
        if(shouldMovePlayerSlider() && pMouseY > topPos + 113 && pMouseY < topPos + 161){
            playerOffset = (int) Mth.clamp(playerOffset - pDelta, 0, Math.max(ClientAllyData.getTotalPlayerNumber() - 3, 0));
            playerSliderY = (int) (playerRange*(playerOffset / (float) (ClientAllyData.getTotalPlayerNumber()-3)));
            return true;
        }
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    private boolean shouldMovePlayerSlider(){
        return ClientAllyData.getTotalPlayerNumber() > 3;
    }

    private boolean shouldMoveGroupSlider(){
        return ClientAllyData.getTotalGroupSize() > 3;
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        draggingGroups = false;
        draggingPlayers = false;
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    private void enableButtons(int idx){
        switch (idx){
            case 3:
                leaveGroup3.active = true;
                groupBtn3.active = true;
                leaveGroup3.visible = true;
                break;
            case 2:
                leaveGroup2.active = true;
                groupBtn2.active = true;
                leaveGroup2.visible = true;
                break;
            case 1:
                leaveGroup1.active = true;
                groupBtn1.active = true;
                leaveGroup1.visible = true;
        }
    }

    private void disableButtons(int idx){
        switch (idx){
            case 3:
                leaveGroup3.active = false;
                groupBtn3.active = false;
                leaveGroup3.visible = false;
                break;
            case 2:
                leaveGroup2.active = false;
                groupBtn2.active = false;
                leaveGroup2.visible = false;
                break;
            case 1:
                leaveGroup1.active = false;
                groupBtn1.active = false;
                leaveGroup1.visible = false;
        }
    }

    private void showPlayersForGroup(Button btn, int idx){
        if(ClientAllyData.getGroupsSize() < idx+1) return;
        playerOffset = 0;
        playerSliderY = 0;
        System.out.println("Showing players at " + ClientAllyData.getGroupNameById(idx + groupOffset));
        ClientAllyData.requestPlayers(ClientAllyData.getGroupNameById(idx + groupOffset));
    }

    private void createGroup(){
        PacketHandler.INSTANCE.sendToServer(AllyChangeMessageC2S.createGroup(groupName.getValue(), groupPassword.getValue()));
        PacketHandler.INSTANCE.sendToServer(AllyGroupSyncMessage.requestGroups());
    }

    private void joinGroup(){
        PacketHandler.INSTANCE.sendToServer(AllyChangeMessageC2S.joinGroup(groupName.getValue(), groupPassword.getValue()));
        PacketHandler.INSTANCE.sendToServer(AllyGroupSyncMessage.requestGroups());
    }

    private void leaveGroup(int idx){
        groupOffset = 0;
        groupSliderY = 0;
        PacketHandler.INSTANCE.sendToServer(AllyChangeMessageC2S.leaveGroup(ClientAllyData.getGroupNameById(idx + groupOffset)));
        PacketHandler.INSTANCE.sendToServer(AllyGroupSyncMessage.requestGroups());
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
    public static ResourceLocation getPlayerSkin(UUID uuid, String name) {
        GameProfile profile = new GameProfile(uuid, name); // Create profile with UUID + name
        SkinManager skinManager = Minecraft.getInstance().getSkinManager();
        ResourceLocation skin = skinManager.getInsecureSkinLocation(profile); // Won’t contact server, only local cache

        return skin; // Custom or cached skin
    }

    @Override
    public void removed() {
        ClientAllyData.clearPlayers();
        super.removed();
    }
}
