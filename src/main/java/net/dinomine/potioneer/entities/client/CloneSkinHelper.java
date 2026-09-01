package net.dinomine.potioneer.entities.client;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.dinomine.potioneer.entities.custom.CloneEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CloneSkinHelper {

    public static void fetchAndCacheSkin(CloneEntity clone) {
        GameProfile baseProfile = clone.getProfile();
        if (baseProfile == null || clone.isSkinLoaded()) return;

        Minecraft mc = Minecraft.getInstance();

        if (mc.getConnection() != null) {
            PlayerInfo info = mc.getConnection().getPlayerInfo(baseProfile.getId());

            if (info != null) {
                clone.setSkinData(info.getSkinLocation(), info.getModelName().equalsIgnoreCase("slim"));
                return;
            }
        }

        // 1. Fill profile properties asynchronously via Mojang's session server
        SkullBlockEntity.updateGameprofile(baseProfile, filledProfile -> {
            SkinManager skinManager = mc.getSkinManager();

            // 2. Fetch texture and skin model metadata
            skinManager.registerSkins(filledProfile, (type, location, profileTexture) -> {
                if (type == MinecraftProfileTexture.Type.SKIN) {
                    String modelMetadata = profileTexture.getMetadata("model");
                    boolean isSlim = "slim".equalsIgnoreCase(modelMetadata);

                    // Update entity cache on the main client thread
                    mc.execute(() -> clone.setSkinData(location, isSlim));
                }
            }, true);
        });
    }

}
