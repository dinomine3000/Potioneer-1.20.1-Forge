package net.dinomine.potioneer.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.dinomine.potioneer.beyonder.abilities.tyrant.ContractAbility;
import net.dinomine.potioneer.beyonder.abilities.tyrant.MistBlinkingAbility;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific.OpenContractScreenMessage;
import net.dinomine.potioneer.server.ServerTokenCache;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

public class BeyonderAbilityCommand {

    public BeyonderAbilityCommand(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("beyonderability")
                .then(Commands.literal("fix")
                    .then(Commands.argument("target", EntityArgument.entity())
                            .executes(this::fixDisabledAbilities)))
                .then(Commands.literal("teleport")
                        .then(Commands.argument("token", StringArgumentType.greedyString())
                                .executes(this::doWaterTrapTeleport)))
                .then(Commands.literal("contract")
                        .then(Commands.argument("token", StringArgumentType.greedyString())
                            .executes(this::openContract)))
        );
    }
    private int fixDisabledAbilities(CommandContext<CommandSourceStack> cmd){
        try {
            Entity target = EntityArgument.getEntity(cmd, "target");
            if(!(target instanceof LivingEntity lTarget)) return 0;
            lTarget.getCapability(CapProvider.BEYONDER_STATS).ifPresent(cap ->{
                cap.getAbilitiesManager().getDisabledAbilitiesManager().reset(cap, lTarget);
            });
            return 1;
        } catch (CommandSyntaxException e) {
            return 0;
        }
    }
    private int doWaterTrapTeleport(CommandContext<CommandSourceStack> cmd){
        UUID token = UUID.fromString(StringArgumentType.getString(cmd, "token"));
        if(!ServerTokenCache.validateToken(token)) return 0;
        ServerPlayer executor = cmd.getSource().getPlayer();
        if (executor == null) return 0;
        CompoundTag trapData = ServerTokenCache.getTokenData(token, true);
        executor.getCapability(CapProvider.BEYONDER_STATS).ifPresent(cap -> {
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
        UUID casterId = contractData.getUUID("casterId");
        PacketHandler.sendMessageSTC(new OpenContractScreenMessage(condition, reward, contractData.getInt("target"), casterId), executor);
        return 1;
    }

}
