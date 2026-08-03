package net.dinomine.potioneer.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.dinomine.potioneer.beyonder.abilities.tyrant.ContractAbility;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific.OpenContractScreenMessage;
import net.dinomine.potioneer.server.ServerTokenCache;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

public class BeyonderAbilityCommand {

    public BeyonderAbilityCommand(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("beyonderability")
                .then(Commands.literal("teleport")
                        .then(Commands.argument("target", EntityArgument.entity())
                            .executes(this::doWaterTrapTeleport)))
                .then(Commands.literal("contract")
                        .then(Commands.argument("token", StringArgumentType.greedyString())
                            .executes(this::openContract)))
        );
    }

    private int doWaterTrapTeleport(CommandContext<CommandSourceStack> cmd){
        try {
            Entity target = EntityArgument.getEntity(cmd, "target");
            if(!(target instanceof LivingEntity lTarget)) return 0;
            lTarget.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap ->{
                cap.getEffectsManager().clearEffects(cap, lTarget);
            });
            return 1;
        } catch (CommandSyntaxException e) {
            return 0;
        }
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
