package net.dinomine.potioneer.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class ResetBeyonderCommand {

    public ResetBeyonderCommand(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("beyonder").requires(stack -> stack.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.argument("id", IntegerArgumentType.integer())
                        .executes(this::setSequence)))
                .then(Commands.literal("reset").executes((command) -> {
            ServerPlayer player = command.getSource().getPlayer();
            player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                if(!player.level().isClientSide()) cap.resetBeyonder(true);
            });

            return 1;
        })));
    }

    private int setSequence(CommandContext<CommandSourceStack> cmd){
        ServerPlayer player = cmd.getSource().getPlayer();
        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap ->{
            if(!player.level().isClientSide()){
                cap.setBeyonderSequence(IntegerArgumentType.getInteger(cmd, "id"));
            }
        });
        return 1;
    }
}
