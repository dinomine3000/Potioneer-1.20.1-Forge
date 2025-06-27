package net.dinomine.potioneer.beyonder.abilities.misc;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Properties;
import java.util.function.Predicate;

public class BoneMealAbility extends Ability {

    public BoneMealAbility(int sequence){
        this.info = new AbilityInfo(57, 56, "Bone Meal", 20 + sequence, 2*(10-sequence), 2*20, "");
        this.isActive = true;
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(cap.getSpirituality() < getInfo().cost()) return false;
        if(!(target instanceof Player player)) return false;
        Level level = target.level();
        if (!(level instanceof ServerLevel )) return false;
        HitResult hitResult = target.pick(5.0D, 0.0F, false);
        if (hitResult.getType() != HitResult.Type.BLOCK) return false;

        BlockHitResult blockHit = (BlockHitResult) hitResult;
        UseOnContext context = new UseOnContext(level, player, player.getUsedItemHand(), ItemStack.EMPTY, blockHit);
        Items.BONE_MEAL.useOn(context);
        cap.requestActiveSpiritualityCost(info.cost());
        return true;
    }

    @Override
    public void passive(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
    }
}
