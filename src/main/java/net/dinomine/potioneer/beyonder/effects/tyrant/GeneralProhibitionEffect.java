package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
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
    public void onAcquire(BeyonderCapability cap, LivingEntity target, boolean fromLoading) {
        if(fromLoading) return;
        if(target instanceof Player player && !target.level().isClientSide()){
            player.displayClientMessage(Component.translatable("message.potioneer.prohibit_" + type), true);
        }
        DisabledAbilitiesManager.DisabledAbilityProxy proxy = null;
        if(type.equalsIgnoreCase("artifact")){
            proxy = DisabledAbilitiesManager.DisabledAbilityProxy.ofGroup(maxLife - lifetime, PlayerAbilitiesManager.AbilityList.ARTIFACT.name());
        } else if(type.equalsIgnoreCase("intrinsic")) {
            proxy = DisabledAbilitiesManager.DisabledAbilityProxy.notOfGroup(maxLife - lifetime, PlayerAbilitiesManager.AbilityList.INTRINSIC.name());
        }
        if(proxy != null) cap.getAbilitiesManager().getDisabledAbilitiesManager().disableAbility("prohibition", proxy, cap, target);
    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        if (type.equals("flying")) {
            VoxelShape blockBelow = target.level().getBlockState(target.getOnPos().below()).getCollisionShape(target.level(), target.getOnPos().below(), CollisionContext.of(target));
            if (blockBelow.isEmpty() && target.mainSupportingBlockPos.isEmpty() && !target.isInWater() && !target.isInLava()) {
                AbilityFunctionHelper.pushEntity(target, new Vec3(0, -1, 0));
                target.fallDistance += 5;
                target.setOnGround(false);
            }
            if (target instanceof Player player) player.getAbilities().flying = false;
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
