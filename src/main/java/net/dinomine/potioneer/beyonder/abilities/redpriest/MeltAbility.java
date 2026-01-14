package net.dinomine.potioneer.beyonder.abilities.redpriest;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific.EvaporateEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;

public class MeltAbility extends Ability {

    public MeltAbility(int sequence){
//        this.info = new AbilityInfo(83, 80, "Melt", 30 + sequence, 20, this.getMaxCooldown(), "melt");
        super(sequence);
        setCost(ignored -> 20);
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "melt";
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide() && cap.getSpirituality() >= cost()) return true;
        if(cap.getSpirituality() < cost()) return false;
        HitResult hit = target.pick(target.getAttributeValue(ForgeMod.BLOCK_REACH.get()) + 0.5f, 0, true);
        ArrayList<Entity> players = AbilityFunctionHelper.getEntitiesAroundPredicate(target, 16, ent -> ent instanceof Player);
        ServerLevel level = (ServerLevel) target.level();
        if(hit instanceof BlockHitResult blockHit){
            BlockPos pos = blockHit.getBlockPos();
            BlockState blockState = level.getBlockState(pos);

            ItemStack exampleStack = new ItemStack(blockState.getBlock().asItem());
            ItemStack burnResult = getSmeltingResultOrNull(exampleStack, level);
            if(!burnResult.isEmpty()){
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                level.playSound(null, target, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.PLAYERS, 1, 1);
                ItemEntity itemEntity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), burnResult, 0, 0, 0);
                level.addFreshEntity(itemEntity);
                cap.requestActiveSpiritualityCost(cost());
                for(Entity ent: players){
                    if(ent instanceof ServerPlayer player){
                        PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                                new EvaporateEffect(pos.getX(), pos.getY(), pos.getZ()));
                    }
                }
                return true;
            } else if(blockState.is(Blocks.WATER)
                    || blockState.isFlammable(target.level(), pos, blockHit.getDirection())){
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                level.playSound(null, target, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.PLAYERS, 1, 1);
                cap.requestActiveSpiritualityCost(cost());
                for(Entity ent: players){
                    if(ent instanceof ServerPlayer player){
                        PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                                new EvaporateEffect(pos.getX(), pos.getY(), pos.getZ()));
                    }
                }
                return true;
            }
        }

        ArrayList<Entity> entities = AbilityFunctionHelper.getEntitiesAroundPredicate(target, 2, ent -> ent instanceof ItemEntity);
        boolean flag = false;
        for(Entity ent: entities){
            if(ent instanceof ItemEntity itemEntity){
                ItemStack result = getSmeltingResultOrNull(itemEntity.getItem(), level);
                if(!result.isEmpty()){
                    flag = true;
                    itemEntity.setItem(result.copyWithCount(itemEntity.getItem().getCount()));
                    for(Entity playerEnt: players){
                        if(playerEnt instanceof ServerPlayer player){
                            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                                    new EvaporateEffect(itemEntity.getOnPos().getX(), itemEntity.getOnPos().getY(), itemEntity.getOnPos().getZ()));
                        }
                    }
                }
            }
        }
        if(flag){
            target.level().playSound(null, target, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.PLAYERS, 1, 1.5f - target.getRandom().nextFloat());
            cap.requestActiveSpiritualityCost(cost());
            return true;
        }
        return false;
    }
    public static ItemStack getSmeltingResultOrNull(ItemStack input, ServerLevel world) {
        RecipeManager recipeManager = world.getRecipeManager();
        RegistryAccess registryAccess = world.registryAccess();

        return recipeManager.getAllRecipesFor(RecipeType.SMELTING).stream()
                .filter(recipe -> recipe.getIngredients().get(0).test(input))
                .findFirst()
                .map(recipe -> recipe.getResultItem(registryAccess).copy())
                .orElse(ItemStack.EMPTY);
    }

}
