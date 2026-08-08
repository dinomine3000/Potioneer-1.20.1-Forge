package net.dinomine.potioneer.beyonder.client.screen;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.AbilityKey;
import net.dinomine.potioneer.beyonder.abilities.tyrant.ContractAbility;
import net.dinomine.potioneer.beyonder.abilities.tyrant.ContractAbility.ContractOption;
import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific.SignContractMessage;
import net.dinomine.potioneer.util.CustomImageButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ContractScreen extends Screen {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/contract_menu.png");

    private static final int FILE_WIDTH = 230, FILE_HEIGHT = 70;
    private int leftPos, topPos;
    private int imgWidth, imgHeight;

    private final AbilityKey key;
    private final List<ContractAbility.ContractOption> conditions = new ArrayList<>();
    private final List<ContractAbility.ContractOption> rewards = new ArrayList<>();
    private final int targetId;
    private static final int MAX_ARGUMENTS = 5;
    private static final int CONDITION_Y = 0;
    private static final int REWARD_Y = 100;

    private int rewardOffset = 0;
    private int conditionOffset = 0;
    private int[] rewardArgOffsets = new int[MAX_ARGUMENTS];
    private int[] conditionArgOffsets = new int[MAX_ARGUMENTS];
    private ImageButton[] conditionArgumentButtons = new ImageButton[MAX_ARGUMENTS];
    private ImageButton[] rewardArgumentButtons = new ImageButton[MAX_ARGUMENTS];
    private ImageButton castButton;

    private ContractAbility.ContractOption chosenCondition = null;
    private ContractAbility.ContractOption chosenReward = null;
    private boolean isPlayerSigningContract = false;

    private ImageButton conditionBack, conditionForward, conditionArgument;
    private ImageButton rewardBack, rewardForward, rewardArgument;

    private final Component entityName;

    public ContractScreen(List<ContractAbility.ContractOption> options, int targetId, AbilityKey ablKey) {
        super(Component.literal("Contract"));
        for(ContractAbility.ContractOption opt: options){
            if(opt.isCondition()) conditions.add(opt);
            else rewards.add(opt);
        }
        this.key = ablKey;
        this.targetId = targetId;
        entityName = Minecraft.getInstance().level.getEntity(targetId).getDisplayName();
    }

    public ContractScreen(ContractOption condition, ContractOption reward, int targetId, boolean signingContract) {
        super(Component.literal("Contract"));
        chosenCondition = condition;
        chosenReward = reward;
        this.targetId = targetId;
        this.key = null;
        entityName = Minecraft.getInstance().level.getEntity(targetId).getDisplayName();
        this.isPlayerSigningContract = signingContract;
    }

    @Override
    protected void init() {
        super.init();

        this.imgWidth = 134;
        this.imgHeight = 166;
        this.leftPos = (this.width - imgWidth) / 2;
        this.topPos = (this.height - imgHeight) / 2;


        if(isPlayerSigningContract || !showOnlyContract()){
            castButton = new ImageButton(leftPos + imgWidth + 65, topPos + 5, 32, 32, 198, 0, 32, TEXTURE, FILE_WIDTH, FILE_HEIGHT, btn -> cast());
            addRenderableWidget(castButton);
        }
        if(showOnlyContract()) return;

        chosenCondition = conditions.get(0);
        chosenReward = rewards.get(0);
        int btnOffset = 10;
        conditionBack = new ImageButton(leftPos - 120, topPos + CONDITION_Y + btnOffset, 11, 17, 62, 1, 18, TEXTURE, FILE_WIDTH, FILE_HEIGHT, btn -> cycleOption(-1, true));
        conditionForward = new ImageButton(leftPos + 30, topPos + CONDITION_Y + btnOffset, 11, 17, 48, 1, 18, TEXTURE, FILE_WIDTH, FILE_HEIGHT, btn -> cycleOption(1, true));
        rewardBack = new ImageButton(leftPos - 120, topPos + REWARD_Y + btnOffset, 11, 17, 62, 1, 18, TEXTURE, FILE_WIDTH, FILE_HEIGHT, btn -> cycleOption(-1, false));
        rewardForward = new ImageButton(leftPos + 30, topPos + REWARD_Y + btnOffset, 11, 17, 48, 1, 18, TEXTURE, FILE_WIDTH, FILE_HEIGHT, btn -> cycleOption(1, false));
        addRenderableWidget(conditionBack);
        addRenderableWidget(conditionForward);
        addRenderableWidget(rewardBack);
        addRenderableWidget(rewardForward);

        for(int i = 0; i < MAX_ARGUMENTS; i++){
            int finalI = i;
            conditionArgumentButtons[i] = new CustomImageButton(leftPos - 100, topPos + CONDITION_Y + 40 + i*20,
                    120, 20, 75, 0, 20, TEXTURE, FILE_WIDTH, FILE_HEIGHT, btn -> cycleArgument(finalI, true,true), btn -> cycleArgument(finalI, true, false));
            rewardArgumentButtons[i] = new CustomImageButton(leftPos - 100, topPos + REWARD_Y + 40 + i*20,
                    120, 20, 75, 0, 20, TEXTURE, FILE_WIDTH, FILE_HEIGHT, btn -> cycleArgument(finalI, false, true), btn -> cycleArgument(finalI, false, false));
            addRenderableWidget(conditionArgumentButtons[i]);
            addRenderableWidget(rewardArgumentButtons[i]);
        }
    }
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        //super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        if(!showOnlyContract()){
            //render boxes for options
            pGuiGraphics.blitNineSlicedSized(TEXTURE, leftPos - 100, topPos + CONDITION_Y, 120, 40, 4, 44, 36, 0, 0, FILE_WIDTH, FILE_HEIGHT);
            pGuiGraphics.drawWordWrap(this.font, chosenCondition.getPreviewComponent(), leftPos - 95, topPos + CONDITION_Y + 5, 110, 0);

            pGuiGraphics.blitNineSlicedSized(TEXTURE, leftPos - 100, topPos + REWARD_Y, 120, 40, 4, 44, 36, 0, 0, FILE_WIDTH, FILE_HEIGHT);
            pGuiGraphics.drawWordWrap(this.font, chosenReward.getPreviewComponent(), leftPos - 95, topPos + REWARD_Y + 5, 110, 0);
            //render buttons
            conditionBack.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            conditionForward.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            rewardBack.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            rewardForward.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

            //render arguments
            for(int i = 0; i < chosenCondition.getArgumentsToChoose(); i++){
                int iLeft = leftPos - 100;
                int iTop = topPos + CONDITION_Y + 40 + i*20;
                conditionArgumentButtons[i].render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
                String argument = chosenCondition.getArguments().get(conditionArgOffsets[i]);
                Component argName = chosenCondition.getComponentForArgument(argument);
                pGuiGraphics.drawString(this.font, argName, iLeft + 120/2 - this.font.width(argName)/2, iTop + 5, 0xffffff, false);
            }
            for(int i = 0; i < chosenReward.getArgumentsToChoose(); i++){
                int iLeft = leftPos - 100;
                int iTop = topPos + REWARD_Y + 40 + i*20;
                rewardArgumentButtons[i].render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
                String argument = chosenReward.getArguments().get(rewardArgOffsets[i]);
                Component argName = chosenReward.getComponentForArgument(argument);
                pGuiGraphics.drawString(this.font, argName, iLeft + 120/2 - this.font.width(argName)/2, iTop + 5, 0xffffff, false);
            }

        }

        if(!showOnlyContract() || isPlayerSigningContract)
            castButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);


        //gather contract data
        Component finalConditionComponent;
        if(chosenCondition.getArguments().isEmpty()) finalConditionComponent = chosenCondition.getFinalComponent();
        else{
            int argAmount = chosenCondition.getArgumentsToChoose();
            Object[] conditionArgs = new Object[argAmount];
            for(int i = 0; i < argAmount; i++){
                conditionArgs[i] = chosenCondition.getComponentForArgument(chosenCondition.getArguments().get(showOnlyContract() ? i : conditionArgOffsets[i]));
            }
            finalConditionComponent = chosenCondition.getFinalComponent(conditionArgs);
        }

        Component finalRewardComponent;
        if(chosenReward.getArguments().isEmpty()) finalRewardComponent = chosenReward.getFinalComponent();
        else{
            int argAmount = chosenReward.getArgumentsToChoose();
            Object[] rewardArgs = new Object[argAmount];
            for(int i = 0; i < argAmount; i++){
                rewardArgs[i] = chosenReward.getComponentForArgument(chosenReward.getArguments().get(showOnlyContract() ? i : rewardArgOffsets[i]));
            }
            finalRewardComponent = chosenReward.getFinalComponent(rewardArgs);
        }

        //render contract
        int wordWrapWidth = imgWidth - 10;
        int sliceSize = 4;
        Component contract = Component.translatable("gui.potioneer.final_contract", entityName, finalConditionComponent, finalRewardComponent);
        List<FormattedCharSequence> lines = font.split(contract, wordWrapWidth);
        int contractHeight = lines.size()*font.lineHeight + 2*sliceSize;

        int contractLeft = showOnlyContract() ? leftPos : leftPos + 60;
        pGuiGraphics.blitNineSlicedSized(TEXTURE, contractLeft, topPos, imgWidth, contractHeight, sliceSize,44, 36, 0, 0, FILE_WIDTH, FILE_HEIGHT);
        pGuiGraphics.drawWordWrap(this.font, contract, contractLeft + 5, topPos + 5, wordWrapWidth, 0);

    }

    private void cycleArgument(int argIdx, boolean conditions, boolean lClick){
        if(conditions){
            if(chosenCondition.getArguments().isEmpty()) conditionArgOffsets[argIdx] = 0;
            else conditionArgOffsets[argIdx] = (conditionArgOffsets[argIdx] + (lClick ? 1 : -1) + chosenCondition.getArguments().size()) % chosenCondition.getArguments().size();
        } else {
            if(chosenReward.getArguments().isEmpty()) rewardArgOffsets[argIdx] = 0;
            else rewardArgOffsets[argIdx] = (rewardArgOffsets[argIdx] + (lClick ? 1 : -1) + chosenReward.getArguments().size()) % chosenReward.getArguments().size();

        }
    }

    private void cycleOption(int diff, boolean cycleConditions){
        if(cycleConditions){
            int size = conditions.size();
            conditionOffset = (conditionOffset + diff + size) % size;
            chosenCondition = conditions.get(conditionOffset);
            conditionArgOffsets = new int[MAX_ARGUMENTS];
        }
        else{
            int size = rewards.size();
            rewardOffset = (rewardOffset + diff + size) % size;
            chosenReward = rewards.get(rewardOffset);
            rewardArgOffsets = new int[MAX_ARGUMENTS];
        }
    }

    private int getConditionArgumentCount(){return chosenCondition == null ? 0 : chosenCondition.getArgumentsToChoose();}
    private int getRewardArgumentCount(){return chosenReward == null ? 0 : chosenReward.getArgumentsToChoose();}

    private void cast(){
        if(isPlayerSigningContract){
            PacketHandler.sendMessageCTS(new SignContractMessage(chosenCondition, chosenReward, targetId));
            this.onClose();
            return;
        }
        if(chosenCondition == null || chosenReward == null) return;
        /*if(chosenCondition.getArguments().size() != chosenCondition.getArgumentsToChoose()) return;
        if(chosenReward.getArguments().size() != chosenReward.getArgumentsToChoose()) return;*/

        CompoundTag args = new CompoundTag();
        args.putInt("target", targetId);
        List<String> conditionArgs = new ArrayList<>();
        for(int i = 0; i < chosenCondition.getArgumentsToChoose(); i++){
            conditionArgs.add(chosenCondition.getArguments().get(conditionArgOffsets[i]));
        }
        if(hasRepeated(conditionArgs)) return;
        List<String> rewardArgs = new ArrayList<>();
        for(int i = 0; i < chosenReward.getArgumentsToChoose(); i++){
            rewardArgs.add(chosenReward.getArguments().get(rewardArgOffsets[i]));
        }
        if(hasRepeated(rewardArgs)) return;
        args.put("condition", chosenCondition.saveToNbt(conditionArgs));
        args.put("reward", chosenReward.saveToNbt(rewardArgs));

        ClientAbilitiesData.useAbility(Minecraft.getInstance().player, key, true, args);
        this.onClose();
    }

    private boolean hasRepeated(List<String> arguments){
        return Set.copyOf(arguments).size() < arguments.size();
    }

    public static void viewExistingContract(ContractOption condition, ContractOption reward, int targetId){
        Minecraft.getInstance().setScreen(new ContractScreen(condition, reward, targetId, false));
    }
    public static void openContractToSign(ContractOption condition, ContractOption reward, int targetId){
        Minecraft.getInstance().setScreen(new ContractScreen(condition, reward, targetId, true));
    }

    private boolean showOnlyContract(){return key == null;}

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
