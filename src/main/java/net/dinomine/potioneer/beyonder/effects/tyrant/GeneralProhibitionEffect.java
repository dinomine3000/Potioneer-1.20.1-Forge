package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.abilities.DisabledAbilitiesManager;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GeneralProhibitionEffect extends BeyonderEffect {
    public String type = "";

    @Override
    public boolean shouldPersistInDeath() {
        return false;
    }

    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target, AcquireType acquireType) {
        if(acquireType != AcquireType.ADDED) return;
        if(target instanceof Player player && !target.level().isClientSide()){
            player.displayClientMessage(Component.translatable("message.potioneer.prohibit_" + type), true);
        }
        DisabledAbilitiesManager.DisabledAbilityProxy proxy = null;
        if(type.equalsIgnoreCase("artifact")){
            proxy = DisabledAbilitiesManager.DisabledAbilityProxy.ofGroup(maxLife - lifetime, AbilityInfo.Group.ARTIFACT);
        } else if(type.equalsIgnoreCase("intrinsic")) {
            proxy = DisabledAbilitiesManager.DisabledAbilityProxy.notOfGroup(maxLife - lifetime, AbilityInfo.Group.INTRINSIC);
        }
        if(proxy != null) cap.getAbilitiesManager().getDisabledAbilitiesManager().disableAbility("prohibition", proxy, cap, target);
    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        if (type.equals("flying")) {
            if (target instanceof Player player) player.getAbilities().flying = false;
            if(!AbilityFunctionHelper.isEntityStandingOnGround(1, target, true)){
                AbilityFunctionHelper.pushEntity(target, new Vec3(0, -1, 0));
                target.fallDistance += 5;
                target.setOnGround(false);
            }
        }
        //target.setOnGround(true);
    }

    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        nbt.putString("argument", type);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        type = nbt.getString("argument");
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {
        cap.getAbilitiesManager().getDisabledAbilitiesManager().enableAbility("prohibition", cap, target);
    }
}
