package net.dinomine.potioneer.beyonder.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TestScreen extends Screen {
    protected TestScreen() {
        super(Component.literal("yepTitle"));
    }

    private int mouseX, mouseY;

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        mouseX = pMouseX;
        mouseY = pMouseY;
    }

    @Override
    public void onClose() {
        //this one is called only when i press Esc or E
        System.out.println("OnClose: Mouse Coords: " + mouseX + " - " + mouseY);
        super.onClose();
    }


    @Override
    public void removed() {
        //this one is called everytime it closes
        int w = this.width;
        int h = this.height;
        boolean xCheck = mouseX < w/2;
        boolean yCheck = mouseY < h/2;
        String quadrant = "";
        if(xCheck && yCheck) quadrant = "Top Left";
        if(!xCheck && yCheck) quadrant = "Top Right";
        if(xCheck && !yCheck) quadrant = "Bottom Left";
        if(!xCheck && !yCheck) quadrant = "Bottom Right";
        System.out.println("Quadrant: " + quadrant);
        super.removed();
    }
}
