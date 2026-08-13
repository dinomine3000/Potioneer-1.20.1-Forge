package net.dinomine.potioneer.beyonder.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.tyrant.RulePylonAbility;
import net.dinomine.potioneer.beyonder.abilities.tyrant.RulePylonAbility.*;
import net.dinomine.potioneer.beyonder.client.KeyBindings;
import net.dinomine.potioneer.block.entity.RulePylonBlockEntity;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific.RulePylonMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

import java.util.*;
import java.util.function.Supplier;

public class RulePylonScreen extends Screen {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/pylon_menu.png");
    private static final int TEXTURE_WIDTH = 215, TEXTURE_HEIGHT = 295;
    private static final int IMAGE_WIDTH = 176, IMAGE_HEIGHT = 161;

    private static final int MAX_COUNT = 4;

    private ImageButton addLawButton;
    private ImageButton addRuleButton;
    private ImageButton saveButton;
    private int leftPos, topPos;

    private static Collection<Law> ALL_LAWS;
    private static Collection<Punishment> ALL_PUNISHMENTS;
    private static Collection<Rule> ALL_RULES;

    private LinkedHashMap<UUID, Law> appliedLaws;
    private LinkedHashMap<UUID, RulePair> appliedRules;
    private boolean extendAoj = false;
    private BlockPos pylonPos;

    private record RulePair(Rule rule, Punishment punishment) {}

    public RulePylonScreen(RulePylonMessage message) {
        this(message.laws, message.rulePunishments, message.aoj, message.pylonPos);
    }

    public RulePylonScreen(RulePylonScreen oldScreen) {
        this(oldScreen.appliedLaws.values(), oldScreen.getRulesMap(), oldScreen.extendAoj, oldScreen.pylonPos);
    }

    private RulePylonScreen(Collection<Law> laws, Map<Rule, Punishment> rules, boolean aoj, BlockPos pos) {
        super(Component.literal("Rule Pylon Screen"));
        appliedLaws = new LinkedHashMap<>();
        for (Law law : laws) appliedLaws.put(UUID.randomUUID(), law);

        appliedRules = new LinkedHashMap<>();
        for (Map.Entry<Rule, Punishment> entry : rules.entrySet()) {
            appliedRules.put(UUID.randomUUID(), new RulePair(entry.getKey(), entry.getValue()));
        }

        extendAoj = aoj;
        pylonPos = pos;

        ALL_LAWS = RulePylonAbility.Law.values();
        ALL_RULES = RulePylonAbility.Rule.values();
        ALL_PUNISHMENTS = RulePylonAbility.Punishment.values();
    }

    public Map<Rule, Punishment> getRulesMap() {
        Map<Rule, Punishment> map = new LinkedHashMap<>();
        for (RulePair pair : appliedRules.values()) {
            map.put(pair.rule(), pair.punishment());
        }
        return map;
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

        leftPos = (this.width - IMAGE_WIDTH) / 2;
        topPos = (this.height - (IMAGE_HEIGHT - 56 + 14*MAX_COUNT)) / 2;

        int lawIdx = 0;
        for (UUID lawId : appliedLaws.keySet()) refreshLaw(lawId, lawIdx++);
        for (UUID ruleId : appliedRules.keySet()) refreshRulePair(ruleId, lawIdx++);

        addLawButton = new ImageButton(leftPos + 5, topPos + IMAGE_HEIGHT - 56 + MAX_COUNT*14, 13, 11, 5, IMAGE_HEIGHT, 11, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> addLaw(), Component.translatable("gui.potioneer.add_law"));
        addLawButton.setTooltip(Tooltip.create(Component.translatable("gui.potioneer.add_law")));
        addRenderableWidget(addLawButton);

        addRuleButton = new ImageButton(leftPos + 20, topPos + IMAGE_HEIGHT - 56 + MAX_COUNT*14, 13, 11, 5, IMAGE_HEIGHT, 11, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> addRulePair(), Component.translatable("gui.potioneer.add_rule"));
        addRuleButton.setTooltip(Tooltip.create(Component.translatable("gui.potioneer.add_rule")));
        addRenderableWidget(addRuleButton);

        saveButton = new ImageButton(leftPos + IMAGE_WIDTH - 50, topPos + IMAGE_HEIGHT - 56 + MAX_COUNT*14, 15, 15, 176, 85, 15, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> save());
        addRenderableWidget(saveButton);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        pGuiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, IMAGE_WIDTH, 95, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        pGuiGraphics.blit(TEXTURE, leftPos, topPos + 95, IMAGE_WIDTH, 14*MAX_COUNT, 0, 95, IMAGE_WIDTH, 56, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        pGuiGraphics.blit(TEXTURE, leftPos, topPos + 95 + 14*MAX_COUNT, 0, 151, IMAGE_WIDTH, 10, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        for (Renderable renderable : renderables) {
            if (renderable instanceof ConfigButton<?> configButton) {
                configButton.renderTitleAndTooltip(pGuiGraphics, pMouseX, pMouseY, this.font);
            }
        }

        pGuiGraphics.drawString(this.font, "Rule Pylon", leftPos + 5, topPos + 5, 0, false);

        String tx = "owned by: dinomine3000";
        pGuiGraphics.drawString(this.font, tx, leftPos + 5, topPos + 20, 0, false);

        RulePylonBlockEntity be = (RulePylonBlockEntity) Minecraft.getInstance().level.getBlockEntity(pylonPos);
        if(be.isWorking()){
            pGuiGraphics.blit(TEXTURE, leftPos + 5, topPos + 35, 176, 70, 7, 7, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            pGuiGraphics.drawString(this.font, "Pylon can see the sky.", leftPos + 15, topPos + 35, 0, false);
        } else {
            pGuiGraphics.blit(TEXTURE, leftPos + 5, topPos + 35, 176, 78, 7, 7, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            if(Minecraft.getInstance().level.canSeeSky(pylonPos))
                pGuiGraphics.drawString(this.font, "Chunk claimed by someone else", leftPos + 15, topPos + 35, 0, false);
            else
                pGuiGraphics.drawString(this.font, "Pylon can't see the sky", leftPos + 15, topPos + 35, 0, false);
        }
        pGuiGraphics.drawString(this.font, "Area: 1 Chunk", leftPos + 5, topPos + 50, 0, false);

        pGuiGraphics.blit(TEXTURE, leftPos + 5, topPos + 63, 176, 43, 13, 13, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        pGuiGraphics.drawString(this.font, "Extend area of jurisdiction?", leftPos + 20, topPos + 66, 0, false);
        if(extendAoj) pGuiGraphics.blit(TEXTURE, leftPos + 6, topPos + 64, 176, 57, 11, 11, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        pGuiGraphics.drawString(this.font, "Rules and Laws:", leftPos + 5, topPos + 83, 0, false);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if(pButton == InputConstants.MOUSE_BUTTON_LEFT){
            if(pMouseX > leftPos + 5 && pMouseX < leftPos + 18
                    && pMouseY > topPos + 63 && pMouseY < topPos + 76){
                extendAoj = !extendAoj;
                Minecraft.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK.get());
                return true;
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void addLaw() {
        if (ALL_LAWS.size() <= appliedLaws.size()) return;
        String law = getNextLaw();
        if (law.isEmpty()) return;
        addLaw(Law.byId(law), getCurrentCount());
    }

    private void addLaw(Law law, int previousCount) {
        UUID btnId = UUID.randomUUID();
        appliedLaws.put(btnId, law);
        refreshLaw(btnId, previousCount);
    }

    private void refreshLaw(UUID lawId, int previousCount) {
        int yLevel = topPos + 95 + 14 * previousCount;
        ConfigButton<Law> lawButton = new ConfigButton<>(leftPos + 6, yLevel, 147, 14, 6, 208, 14, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> cycleLaw(lawId), () -> appliedLaws.get(lawId), "gui.potioneer.law");
        ImageButton removeButton = new ImageButton(leftPos + 153, yLevel, 17, 14, 153, 208, 14, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> removeLaw(lawId, lawButton, btn));
        addRenderableWidget(lawButton);
        addRenderableWidget(removeButton);
    }

    private void cycleLaw(UUID id) {
        String currLaw = appliedLaws.get(id).id();
        Law nextLaw = Law.byId(getNextLaw(currLaw));
        appliedLaws.put(id, nextLaw);
    }

    private int getCurrentCount(){
        return appliedLaws.size() + appliedRules.size()*2;
    }

    private String getNextRule() {
        if (getCurrentCount() >= MAX_COUNT - 1) return "";
        ArrayList<Rule> availableRules = new ArrayList<>(ALL_RULES);
        List<Rule> usedRules = new ArrayList<>();
        for (RulePair pair : appliedRules.values()) usedRules.add(pair.rule());
        availableRules.removeAll(usedRules);
        if (availableRules.isEmpty()) return "";
        return availableRules.get(0).id();
    }

    private String getNextLaw() {
        if(getCurrentCount() >= MAX_COUNT) return "";
        ArrayList<Law> availableLaws = new ArrayList<>(ALL_LAWS);
        availableLaws.removeAll(appliedLaws.values());
        if (availableLaws.isEmpty()) return "";
        return availableLaws.get(0).id();
    }

    private String getNextLaw(String currentLaw) {
        ArrayList<Law> availableLaws = new ArrayList<>(ALL_LAWS);
        List<Law> toRemove = new ArrayList<>(appliedLaws.values());
        toRemove.remove(Law.byId(currentLaw));
        availableLaws.removeAll(toRemove);
        if (availableLaws.isEmpty()) return currentLaw;
        int idx = availableLaws.indexOf(Law.byId(currentLaw));
        return availableLaws.get((idx + 1) % availableLaws.size()).id();
    }

    private void removeLaw(UUID id, GuiEventListener button, GuiEventListener remover) {
        removeWidget(button);
        removeWidget(remover);
        appliedLaws.remove(id);
        Minecraft.getInstance().setScreen(new RulePylonScreen(this));
    }

    private void addRulePair() {
        if (ALL_RULES.size() <= appliedRules.size()) return;
        String ruleId = getNextRule();
        if (ruleId.isEmpty()) return;

        Rule rule = Rule.byId(ruleId);
        Punishment punishment = ALL_PUNISHMENTS.iterator().next();
        addRulePair(rule, punishment, getCurrentCount());
    }
    private void addRulePair(Rule rule, Punishment punishment, int previousCount) {
        UUID btnId = UUID.randomUUID();
        appliedRules.put(btnId, new RulePair(rule, punishment));
        refreshRulePair(btnId, previousCount);
    }

    private void refreshRulePair(UUID pairId, int previousCount) {
        int yLevel = topPos + 95 + 14 * previousCount;

        ConfigButton<Rule> ruleButton = new ConfigButton<>(leftPos + 6, yLevel,                         147, 14, 6, 236, 28, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> cycleRule(pairId), () -> appliedRules.get(pairId).rule(), "gui.potioneer.rule");
        ConfigButton<Punishment> punishmentButton = new ConfigButton<>(leftPos + 6, yLevel + 14,    147, 14, 6, 250, 28, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn ->  cyclePunishment(pairId), () -> appliedRules.get(pairId).punishment(), "gui.potioneer.punishment");
        ImageButton removeButton = new ImageButton(leftPos + 153, yLevel, 17, 14, 153, 208, 14, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> removeRulePair(pairId, ruleButton, punishmentButton, btn));

        addRenderableWidget(ruleButton);
        addRenderableWidget(punishmentButton);
        addRenderableWidget(removeButton);
    }

    private void cycleRule(UUID id) {
        RulePair current = appliedRules.get(id);
        Rule nextRule = Rule.byId(getNextRule(current.rule().id()));
        appliedRules.put(id, new RulePair(nextRule, current.punishment()));
    }

    private void cyclePunishment(UUID id) {
        RulePair current = appliedRules.get(id);
        List<Punishment> list = new ArrayList<>(ALL_PUNISHMENTS);
        int idx = list.indexOf(current.punishment());
        Punishment nextPunishment = list.get((idx + 1) % list.size());
        appliedRules.put(id, new RulePair(current.rule(), nextPunishment));
    }

    private String getNextRule(String currentRule) {
        ArrayList<Rule> availableRules = new ArrayList<>(ALL_RULES);
        List<Rule> usedRules = new ArrayList<>();
        for (RulePair pair : appliedRules.values()) usedRules.add(pair.rule());
        usedRules.remove(Rule.byId(currentRule));
        availableRules.removeAll(usedRules);
        if (availableRules.isEmpty()) return currentRule;
        int idx = availableRules.indexOf(Rule.byId(currentRule));
        return availableRules.get((idx + 1) % availableRules.size()).id();
    }

    private void removeRulePair(UUID id, GuiEventListener ruleBtn, GuiEventListener punishmentBtn, GuiEventListener remover) {
        removeWidget(ruleBtn);
        removeWidget(punishmentBtn);
        removeWidget(remover);
        appliedRules.remove(id);
        Minecraft.getInstance().setScreen(new RulePylonScreen(this));
    }

    private static class ConfigButton<T extends Displayable> extends ImageButton {
        private final Supplier<T> displaySupplier;
        private final String componentKey;

        public ConfigButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int pYDiffTex, ResourceLocation pResourceLocation, int pTextureWidth, int pTextureHeight, OnPress pOnPress, Supplier<T> displaySupplier, String componentKey) {
            super(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffTex, pResourceLocation, pTextureWidth, pTextureHeight, pOnPress);
            this.displaySupplier = displaySupplier;
            this.componentKey = componentKey;
        }

        public void renderTitleAndTooltip(GuiGraphics pGuiGraphics, int mouseX, int mouseY, Font font) {
            T item = displaySupplier.get();
            if (item == null) return;

            pGuiGraphics.drawString(font, Component.translatable(componentKey, item.title()), getX() + 5, getY() + 2, 0, false);
            if (mouseX > getX() && mouseX < getX() + width && mouseY > getY() && mouseY < getY() + height) {
                pGuiGraphics.renderTooltip(font, item.tooltip(), mouseX, mouseY);
            }
        }
    }

    private void save(){
        PacketHandler.sendMessageCTS(new RulePylonMessage(getRulesMap(), appliedLaws.values().stream().toList(), pylonPos, extendAoj));
    }
}