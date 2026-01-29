package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.wheeloffortune.MiningSpeedAbility;
import net.dinomine.potioneer.beyonder.abilities.wheeloffortune.VelocityAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;

public class BeyonderVelocityEffect extends BeyonderEffect {
    private static final UUID movementId = UUID.fromString("7ab53a6f-02ef-4468-9732-a8aace30137a");
    private static final UUID attackId = UUID.fromString("3a4d8969-bea2-4c53-872d-082cfcd4ad09");
    public int movementSpeed = -1;
    public int attackSpeed = -1;

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(movementSpeed == -1)
            movementSpeed = cap.getLuckManager().getRandomNumber(1, VelocityAbility.levelToMaxMovement.apply(getSequenceLevel()), true, target.getRandom());
        if(attackSpeed == -1)
            attackSpeed = cap.getLuckManager().getRandomNumber(1, VelocityAbility.levelToMaxAttack.apply(getSequenceLevel()), true, target.getRandom());
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        if(!(target instanceof Player player)) return;
        AbilityFunctionHelper.addAttributeTo(player, movementId, "velocity movement speed modifier", movementSpeed-1, AttributeModifier.Operation.MULTIPLY_BASE, Attributes.MOVEMENT_SPEED);
        AbilityFunctionHelper.addAttributeTo(player, attackId, "velocity attack speed modifier", attackSpeed-1, AttributeModifier.Operation.MULTIPLY_BASE, Attributes.ATTACK_SPEED);
        cap.requestPassiveSpiritualityCost(getCost()*(Math.max(movementSpeed, attackSpeed)-1));
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        if(!(target instanceof Player player)) return;
        AbilityFunctionHelper.removeAttribute(player, movementId, "velocity movement modifier", movementSpeed, AttributeModifier.Operation.MULTIPLY_TOTAL, Attributes.MOVEMENT_SPEED);
        AbilityFunctionHelper.removeAttribute(player, attackId, "velocity attack speed modifier", attackSpeed, AttributeModifier.Operation.MULTIPLY_TOTAL, Attributes.ATTACK_SPEED);

    }

    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        nbt.putInt("movement", movementSpeed);
        nbt.putInt("attack", attackSpeed);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        if(nbt.contains("movement")) movementSpeed = nbt.getInt("movement");
        if(nbt.contains("attack")) attackSpeed = nbt.getInt("attack");
    }
}
