/*package net.dinomine.potioneer.mixin;

import net.dinomine.potioneer.beyonder.abilities.tyrant.AreaOfJurisdictionAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Optional;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Inject(method = "shouldEntityAppearGlowing", at = @At("HEAD"), cancellable = true)
    public void shouldEntityAppearGlowing(Entity pEntity, CallbackInfoReturnable<Boolean> ci) {
        if(player == null) return;
        Optional<LivingEntityBeyonderCapability> optCap = player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
        if(optCap.isEmpty()) return;
        LivingEntityBeyonderCapability cap = optCap.get();
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.TYRANT_SENSE_OF_ORDER) && (pEntity.type))
    }
}*/
