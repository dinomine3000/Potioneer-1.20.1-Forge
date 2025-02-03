package net.dinomine.potioneer.beyonder.client;

import com.eliotlash.mclib.math.functions.limit.Min;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientAdvancementManager {
    public static int count = 30;
    public static int maxCount = 30;
    public static int targetSequence = 9;
    static float maxTime = 1;
    public static float progress = 1;
    public static int x;
    public static int y;
    public static int difficulty;
    public static boolean start;

    public static void setDifficulty(int diff){
        difficulty = Mth.clamp(diff, 0, 10);
    }

    public static void render(Screen screen, float partialTick){
        if(!start){
            progress -= partialTick*0.05f/maxTime;
            if(progress < 0){
                gameOver(screen, false);
                Minecraft.getInstance().player.sendSystemMessage(Component.literal("Failed advancement"));
            }
        }
    }

    public static void startGame(Screen screen){
        count = 4*difficulty+10;
        maxCount = count;
        maxTime = 1.5f-difficulty*0.1f;
        progress = 1;
        start = true;
        x = screen.width/2 - 15;
        y = screen.height/2 + 5;
        Minecraft.getInstance().player.sendSystemMessage(Component.literal("Difficulty: " + String.valueOf(difficulty)));
    }

    public static void onButtonSucceed(Screen screen) {
        start = false;
        count--;
        progress = 1;
        Player player = Minecraft.getInstance().player;
        if(player != null){
//            Minecraft.getInstance().level.playSound(player, player, ModSounds.ADVANCEMENT_CLICK.get(), SoundSource.MASTER, 1, 1);
            Minecraft.getInstance().player.playSound(ModSounds.ADVANCEMENT_CLICK.get(), 1 , 1);
        }
        x = (int) (Math.random() * (screen.width - 80) + 40);
        y = (int) (Math.random() * (screen.height - 80) + 40);
        if(count < 1) gameOver(screen, true);
    }

    public static void gameOver(Screen screen, boolean success){
        screen.onClose();
        if(success){
            Minecraft.getInstance().player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                cap.advance(targetSequence, Minecraft.getInstance().player, true, true);
            });
        } else {
            Player player = Minecraft.getInstance().player;
            if(!player.isCreative()){
                player.kill();
                //reduce sequence
            }
            player.sendSystemMessage(Component.literal("Lost control on the spot. oh well."));
        }
    }
}
