package net.dinomine.potioneer.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.dinomine.potioneer.beyonder.abilities.tyrant.ContractAbility;
import net.dinomine.potioneer.beyonder.abilities.tyrant.MistBlinkingAbility;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific.OpenContractScreenMessage;
import net.dinomine.potioneer.server.ServerTokenCache;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class BeyonderAbilityCommand {

    public BeyonderAbilityCommand(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("beyonderability")
                .then(Commands.literal("teleport")
                        .then(Commands.argument("token", StringArgumentType.greedyString())
                                .executes(this::doWaterTrapTeleport)))
                .then(Commands.literal("contract")
                        .then(Commands.argument("token", StringArgumentType.greedyString())
                            .executes(this::openContract)))
        );
    }

    private int doWaterTrapTeleport(CommandContext<CommandSourceStack> cmd){
        UUID token = UUID.fromString(StringArgumentType.getString(cmd, "token"));
        if(!ServerTokenCache.validateToken(token)) return 0;
        ServerPlayer executor = cmd.getSource().getPlayer();
        if (executor == null) return 0;
        CompoundTag trapData = ServerTokenCache.getTokenData(token, true);
        executor.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            MistBlinkingAbility.doMistBlinkingTo(executor, cap, (ServerLevel) executor.level(), trapData.getString("dim"), 0, new BlockPos(trapData.getInt("x"), trapData.getInt("y"), trapData.getInt("z")), 0);
        });
        return 1;
    }

    private int openContract(CommandContext<CommandSourceStack> cmd){
        UUID token = UUID.fromString(StringArgumentType.getString(cmd, "token"));
        if(!ServerTokenCache.validateToken(token)) return 0;
        ServerPlayer executor = cmd.getSource().getPlayer();
        if (executor == null) return 0;

        CompoundTag contractData = ServerTokenCache.getTokenData(token, false);
        if(executor.getId() != contractData.getInt("target")) return 0;

        ServerTokenCache.invalidateToken(token);
        ContractAbility.ContractOption condition = ContractAbility.ContractOption.loadFromNbt(contractData.getCompound("condition")).get();
        ContractAbility.ContractOption reward = ContractAbility.ContractOption.loadFromNbt(contractData.getCompound("reward")).get();
        PacketHandler.sendMessageSTC(new OpenContractScreenMessage(condition, reward, contractData.getInt("target")), executor);
        return 1;
    }

}
