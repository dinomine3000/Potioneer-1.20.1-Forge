package net.dinomine.potioneer.beyonder.client.HUD;

import com.eliotlash.mclib.math.functions.limit.Min;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static net.dinomine.potioneer.beyonder.client.ClientConfigData.*;

public class AbilityHudHandler {
    private static final List<AbilityDataHud> LEFT_HUDS = new ArrayList<>();
    private static final List<AbilityDataHud> RIGHT_HUDS = new ArrayList<>();
    private static final List<AbilityDataHud> CENTER_HUDS = new ArrayList<>();

    public static final AbilityDataHud APPRAISAL = register(LEFT_HUDS, new LuckAppraisalHUD());
    public static final AbilityDataHud SPIRIT_WORLD_GUIDE = register(CENTER_HUDS, new SpiritWorldGuideHud());

    private static AbilityDataHud register(List<AbilityDataHud> list, AbilityDataHud hud){
        list.add(hud);
        return hud;
    }

    public static final IGuiOverlay ABILITY_OVERLAY = ((forgeGui, guiGraphics, partialTick, width, height) -> {
        if(!isRenderLeft() && !isRenderCenter() && !isRenderRight()) return;
        LocalPlayer player = forgeGui.getMinecraft().player;
        if(player == null) return;
        List<AbilityDataHud> leftHuds = new ArrayList<>(LEFT_HUDS);
        List<AbilityDataHud> centerHuds = new ArrayList<>(CENTER_HUDS);
        List<AbilityDataHud> rightHuds = new ArrayList<>(RIGHT_HUDS);

        boolean renderLeft = isRenderLeft();
        boolean renderCenter = isRenderCenter();
        boolean renderRight = isRenderRight();
        if(!renderLeft) rightHuds.addAll(leftHuds);
        if(!renderRight) leftHuds.addAll(rightHuds);
        if(!renderCenter){
            if(renderLeft) leftHuds.addAll(centerHuds);
            else rightHuds.addAll(centerHuds);
        }

        if(renderRight) render(rightHuds, getRightX(), getRightY(), getRightWidth(), guiGraphics, forgeGui, partialTick);
        if(renderCenter) render(centerHuds, getCenterX(), getCenterY(), getCenterWidth(), guiGraphics, forgeGui, partialTick);
        if(renderLeft) render(leftHuds, getLeftX(), getLeftY(), getLeftWidth(), guiGraphics, forgeGui, partialTick);

    });

    private static void render(List<AbilityDataHud> huds, float leftPos, float topPos, float widthPercent, GuiGraphics guiGraphics, ForgeGui forgeGui, float partialTick){
        int diff = 0;
        for(AbilityDataHud hud: huds){
            if(!hud.shouldRender(forgeGui.getMinecraft(), forgeGui.getMinecraft().player)) continue;
            hud.setDimensions(
                    (int)(topPos*guiGraphics.guiHeight()-diff), (int) (widthPercent*guiGraphics.guiWidth()),
                    (int)(leftPos*guiGraphics.guiWidth()), (int) (topPos*guiGraphics.guiHeight() + diff));
            diff += hud.render(forgeGui, guiGraphics, partialTick);
            diff += 5;
        }
    }
}
